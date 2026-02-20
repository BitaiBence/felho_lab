package bbitai.service.impl;

import hu.avhga.g3.lib.exception.BaseErrorCode;
import hu.avhga.g3.lib.exception.ServiceException;
import hu.avhga.g3.lib.security.AuthorizationHelper;

import bbitai.api.model.ListPhotos200Response;
import bbitai.api.model.PhotoListItem;
import bbitai.api.model.Photo;
import bbitai.api.model.UserResponse;
import bbitai.api.model.LoginResponse;
import bbitai.domain.User;
import bbitai.repository.PhotoRepository;
import bbitai.repository.UserRepository;
import bbitai.service.PhotoService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger log = LoggerFactory.getLogger(PhotoServiceImpl.class);

	private final PhotoRepository photoRepository;
	private final UserRepository userRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;

	@Override
	@Transactional(readOnly = true)
	public ListPhotos200Response listPhotos(String sortBy, String order) {
		String username= AuthorizationHelper.getCurrentUserName();
		if(username==null) {
			throw new ServiceException(BaseErrorCode.CL002,"Nincs username a tokenben.");
		}
		log.error("username:{}",username);

		// Map sortBy parameter to actual column name
		String sortColumn = "date".equalsIgnoreCase(sortBy) ? "uploadDate" : "name";
		Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
		Sort sort = Sort.by(direction, sortColumn);
		List<bbitai.domain.Photo> photos = photoRepository.findAll(sort);

		List<PhotoListItem> items = photos.stream().filter(photo-> photo.getUploadedBy().getUsername().equals(username))
				.map(entity -> {
					PhotoListItem item = new PhotoListItem();
					item.setId(entity.getId());
					item.setName(entity.getName());
					item.setUploadDate(entity.getUploadDate());
					log.info("PhotoListItem - id: {}, name: {}, uploadDate: {}, uploadedBy: [id: {}, username: {}]",
							entity.getId(), entity.getName(), entity.getUploadDate(),
							entity.getUploadedBy().getId(), entity.getUploadedBy().getUsername());
					return item;
				})
				.collect(Collectors.toList());

		log.info("Total items returned for user '{}': {}", username, items.size());

		ListPhotos200Response response = new ListPhotos200Response();
		response.setPhotos(items);
		response.setTotal(items.size());
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Photo> getPhoto(Long photoId) {
		return photoRepository.findById(photoId)
				.map(entity -> {
					Photo photo = new Photo();
					photo.setId(entity.getId());
					photo.setName(entity.getName());
					photo.setUploadDate(entity.getUploadDate());
					photo.setImageUrl(URI.create("/photos/" + entity.getId() + "/image"));
					photo.setMimeType(entity.getMimeType());
					photo.setUploadedBy(entity.getUploadedBy().getId());
					return photo;
				});
	}

	@Override
	@Transactional
	public Photo uploadPhoto(String name, MultipartFile file, String username) throws IOException {
		List<User> users = userRepository.findByUsername(username);

		User user = null;
		if(users.size()>1) {
			throw new RuntimeException("Cannnot create new user, username must be unique.");
		}
		if(users.isEmpty()) {
			user=userRepository.saveAndFlush(User.builder().username(username).password("asd").createdAt(LocalDateTime.now()).build());
		}else {
			user =users.getFirst();
		}


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
		result.setId(saved.getId());
		result.setName(saved.getName());
		result.setUploadDate(saved.getUploadDate());
		result.setImageUrl(URI.create(saved.getImageUrl()));
		result.setMimeType(saved.getMimeType());
		result.setUploadedBy(saved.getUploadedBy().getId());
		return result;
	}

	@Override
	@Transactional
	public void deletePhoto(Long photoId) {
		String username= AuthorizationHelper.getCurrentUserName();
		if(username==null) {
			throw new ServiceException(BaseErrorCode.CL002,"Nincs username a tokenben.");
		}

		List<User> users = userRepository.findByUsername(username);
		User user = null;
		if(users.size()>1) {
			throw new RuntimeException("Username must be unique.");
		}
		if(users.isEmpty()) {
			throw new ServiceException(BaseErrorCode.CL002);
		}else {
			user =users.getFirst();
		}

		bbitai.domain.Photo toDelete=photoRepository.findById(photoId).orElseThrow();
		if(!toDelete.getUploadedBy().getUsername().equals(user.getUsername())){
			throw new ServiceException(BaseErrorCode.CL003,"csak saját fotót lehet törölni.");
		}
		photoRepository.deleteById(toDelete.getId());
	}

	@Override
	@Transactional(readOnly = true)
	public InputStream getPhotoImage(Long photoId) {
		byte[] imageByte = photoRepository.findById(photoId)
				.map(bbitai.domain.Photo::getImageData)
				.orElse(null);

		if ( imageByte != null && imageByte.length != 0 ) {
			return new ByteArrayInputStream(imageByte);
		}

		return null;
	}

	@Override
	@Transactional
	public UserResponse registerUser(String username, String password) {
//		if (userRepository.findByUsername(username).isPresent()) {
//			throw new RuntimeException("User already exists");
//		}
//
//		User user = User.builder()
//				.username(username)
//				.password(passwordEncoder.encode(password))
//				.createdAt(LocalDateTime.now())
//				.build();
//
//		User saved = userRepository.saveAndFlush(user);
//
//		UserResponse response = new UserResponse();
//		response.setId(saved.getId());
//		response.setUsername(saved.getUsername());
//		response.setCreatedAt(saved.getCreatedAt());
//		return response;
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public LoginResponse loginUser(String username, String password) {
//		User user = userRepository.findByUsername(username)
//				.orElseThrow(() -> new RuntimeException("Invalid credentials"));
//
//		if (!passwordEncoder.matches(password, user.getPassword())) {
//			throw new RuntimeException("Invalid credentials");
//		}
//
//		// TODO: Generate JWT token (for now using placeholder)
//		LoginResponse response = new LoginResponse();
//		response.setToken("jwt_token_placeholder");
//		response.setExpiresIn(3600);
//
//		UserResponse userResponse = new UserResponse();
//		userResponse.setId(user.getId());
//		userResponse.setUsername(user.getUsername());
//		userResponse.setCreatedAt(user.getCreatedAt());
//		response.setUser(userResponse);
//
//		return response;
		return null;
	}

	@Override
	@Transactional
	public void logoutUser(Long userId) {
		// TODO: Invalidate token/session if using stateful auth
	}
}


