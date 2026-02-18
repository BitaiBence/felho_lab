package bbitai.service;


import bbitai.api.model.ListPhotos200Response;
import bbitai.api.model.Photo;
import bbitai.api.model.UserResponse;
import bbitai.api.model.LoginResponse;
import bbitai.domain.User;
import hu.avhga.g3.lib.persistence.PageableBuilder;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public interface PhotoService {

	// Photo CRUD operations
	ListPhotos200Response listPhotos(String sortBy, String order);

	Optional<Photo> getPhoto(Long photoId);

	Photo uploadPhoto(String name, MultipartFile file, Long userId) throws Exception;

	boolean deletePhoto(Long photoId, Long userId);

	InputStream getPhotoImage(Long photoId);

	// User management
	UserResponse registerUser(String username, String password);

	LoginResponse loginUser(String username, String password);

	void logoutUser(Long userId);

}


