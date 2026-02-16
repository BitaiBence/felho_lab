package bbitai.service.impl;

import bbitai.api.model.ListPhotos200Response;
import bbitai.api.model.PhotoListItem;
import bbitai.api.model.Photo;
import bbitai.api.model.UserResponse;
import bbitai.api.model.LoginResponse;
import bbitai.domain.User;
import bbitai.repository.PhotoRepository;
import bbitai.repository.UserRepository;
import bbitai.service.PhotoService;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

	private final PhotoRepository photoRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public ListPhotos200Response listPhotos(String sortBy, String order) {
		// Map sortBy parameter to actual column name
		String sortColumn = "date".equalsIgnoreCase(sortBy) ? "uploadDate" : "name";
		Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
		Sort sort = Sort.by(direction, sortColumn);

		List<bbitai.domain.Photo> photos = photoRepository.findAll(sort);

		List<PhotoListItem> items = photos.stream()
				.map(entity -> {
					PhotoListItem item = new PhotoListItem();
					item.setId(UUID.nameUUIDFromBytes(entity.getId().toString().getBytes()));
					item.setName(entity.getName());
					item.setUploadDate(entity.getUploadDate());
					return item;
				})
				.collect(Collectors.toList());

		ListPhotos200Response response = new ListPhotos200Response();
		response.setPhotos(items);
		response.setTotal(items.size());
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(cacheNames = "photosById", key = "#photoId")
	public Optional<Photo> getPhoto(Long photoId) {
		return photoRepository.findById(photoId)
				.map(entity -> {
					Photo photo = new Photo();
					photo.setId(UUID.nameUUIDFromBytes(entity.getId().toString().getBytes()));
					photo.setName(entity.getName());
					photo.setUploadDate(entity.getUploadDate());
					photo.setImageUrl(URI.create("/photos/" + entity.getId() + "/image"));
					photo.setMimeType(entity.getMimeType());
					photo.setUploadedBy(UUID.nameUUIDFromBytes(entity.getUploadedBy().getId().toString().getBytes()));
					return photo;
				});
	}

	@Override
	@Transactional
	public Photo uploadPhoto(String name, MultipartFile file, Long userId) throws IOException {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User not found"));

		bbitai.domain.Photo photo = bbitai.domain.Photo.builder()
				.name(name)
				.uploadDate(LocalDateTime.now())
				.imageUrl("/photos/" + System.currentTimeMillis() + "/image")
				.mimeType(file.getContentType())
				.fileSize(file.getSize())
				.imageData(file.getBytes())
				.uploadedBy(user)
				.build();

		bbitai.domain.Photo saved = photoRepository.saveAndFlush(photo);

		Photo result = new Photo();
		result.setId(UUID.nameUUIDFromBytes(saved.getId().toString().getBytes()));
		result.setName(saved.getName());
		result.setUploadDate(saved.getUploadDate());
		result.setImageUrl(URI.create(saved.getImageUrl()));
		result.setMimeType(saved.getMimeType());
		result.setUploadedBy(UUID.nameUUIDFromBytes(saved.getUploadedBy().getId().toString().getBytes()));
		return result;
	}

	@Override
	@Transactional
	@CacheEvict(cacheNames = "photosById", key = "#photoId")
	public boolean deletePhoto(Long photoId, Long userId) {
		return photoRepository.findById(photoId).map(existing -> {
			if (!existing.getUploadedBy().getId().equals(userId)) {
				throw new RuntimeException("User does not own this photo");
			}
			photoRepository.deleteById(photoId);
			return true;
		}).orElse(false);
	}

	@Override
	@Transactional(readOnly = true)
	public byte[] getPhotoImage(Long photoId) {
		return photoRepository.findById(photoId)
				.map(bbitai.domain.Photo::getImageData)
				.orElse(null);
	}

	@Override
	@Transactional
	public UserResponse registerUser(String username, String password) {
		if (userRepository.findByUsername(username).isPresent()) {
			throw new RuntimeException("User already exists");
		}

		User user = User.builder()
				.username(username)
				.password(passwordEncoder.encode(password))
				.createdAt(LocalDateTime.now())
				.build();

		User saved = userRepository.saveAndFlush(user);

		UserResponse response = new UserResponse();
		response.setId(UUID.nameUUIDFromBytes(saved.getId().toString().getBytes()));
		response.setUsername(saved.getUsername());
		response.setCreatedAt(saved.getCreatedAt());
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public LoginResponse loginUser(String username, String password) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new RuntimeException("Invalid credentials"));

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}

		// TODO: Generate JWT token (for now using placeholder)
		LoginResponse response = new LoginResponse();
		response.setToken("jwt_token_placeholder");
		response.setExpiresIn(3600);

		UserResponse userResponse = new UserResponse();
		userResponse.setId(UUID.nameUUIDFromBytes(user.getId().toString().getBytes()));
		userResponse.setUsername(user.getUsername());
		userResponse.setCreatedAt(user.getCreatedAt());
		response.setUser(userResponse);

		return response;
	}

	@Override
	@Transactional
	public void logoutUser(Long userId) {
		// TODO: Invalidate token/session if using stateful auth
	}
}


