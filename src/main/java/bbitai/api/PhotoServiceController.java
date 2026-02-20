package bbitai.api;

import bbitai.api.model.DeletePhoto200Response;
import bbitai.api.model.Error;
import bbitai.api.model.ListPhotos200Response;
import bbitai.api.model.LoginResponse;
import bbitai.api.model.LogoutUser200Response;
import bbitai.api.model.Photo;
import bbitai.api.model.User;
import bbitai.api.model.UserResponse;
import bbitai.service.PhotoService;

import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import hu.avhga.g3.lib.exception.BaseErrorCode;
import hu.avhga.g3.lib.exception.ServiceException;
import hu.avhga.g3.lib.security.AuthorizationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PhotoServiceController implements PhotosApi, AuthApi,HealthApi {

	@Autowired
	private PhotoService photoService;

	@Autowired
	private NativeWebRequest request;

	@Override
	public Optional<NativeWebRequest> getRequest() {
		return Optional.ofNullable(request);
	}

	@Override
	public ResponseEntity<Void> healthGet() {
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	// ==================== Photos API ====================

	@Override
	public ResponseEntity<ListPhotos200Response> listPhotos(String sortBy, String order) {
		if(!AuthorizationHelper.hasAuthority("ROLE_PS_USER") ){
			throw new ServiceException(BaseErrorCode.CL003);
		}
		try {
			ListPhotos200Response response = photoService.listPhotos(sortBy, order);
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@Override
	public ResponseEntity<Photo> uploadPhoto(String name, MultipartFile file) {
		if(!AuthorizationHelper.hasAuthority("ROLE_PS_USER") ){
			throw new ServiceException(BaseErrorCode.CL003);
		}
		try {
			if (name == null || name.length() > 40) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

			if (file == null || file.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

			String username= AuthorizationHelper.getCurrentUserName();
			if(username==null) {
				throw new ServiceException(BaseErrorCode.CL002,"Nincs username a tokenben.");
			}

			Photo photo = photoService.uploadPhoto(name, file, username);
			return ResponseEntity.status(HttpStatus.CREATED).body(photo);
		} catch (Exception e) {
			if (e.getMessage() != null && e.getMessage().contains("User not found")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@Override
	public ResponseEntity<Photo> getPhoto(Long photoId) {
		if(!AuthorizationHelper.hasAuthority("ROLE_PS_USER") ){
			throw new ServiceException(BaseErrorCode.CL003);
		}
		try {
			var photo = photoService.getPhoto(photoId);
			if (photo.isEmpty()) {
				return ResponseEntity.notFound().build();
			}
			return ResponseEntity.ok(photo.get());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@Override
	public ResponseEntity<DeletePhoto200Response> deletePhoto(Long photoId) {
		if(!AuthorizationHelper.hasAuthority("ROLE_PS_USER") ){
			throw new ServiceException(BaseErrorCode.CL003);
		}
		try {
			photoService.deletePhoto(photoId);

			DeletePhoto200Response response = new DeletePhoto200Response();
			response.setMessage("Photo deleted successfully");
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			if (e.getMessage() != null && e.getMessage().contains("does not own")) {
				return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
			}
			return ResponseEntity.notFound().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@Override
	public ResponseEntity<Resource> getPhotoImage(Long photoId) {
		try {
			var photoOpt = photoService.getPhoto(photoId);
			if (photoOpt.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			InputStream imageData = photoService.getPhotoImage(photoId);

			InputStreamResource inputStreamResource = new InputStreamResource(imageData);
			return ResponseEntity.ok(inputStreamResource);

//			String mimeType = photoOpt.get().getMimeType();
//			if (mimeType == null) {
//				mimeType = "application/octet-stream";
//			}

//			return ResponseEntity.ok()
//					.header("Content-Type", mimeType)
//					.body(resource);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	// ==================== Auth API ====================

	@Override
	public ResponseEntity<UserResponse> registerUser(User user) {
		try {
			if (user.getUsername() == null || user.getUsername().isEmpty() ||
					user.getPassword() == null || user.getPassword().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

			UserResponse response = photoService.registerUser(user.getUsername(), user.getPassword());
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (RuntimeException e) {
			if (e.getMessage() != null && e.getMessage().contains("already exists")) {
				return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
			}
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@Override
	public ResponseEntity<LoginResponse> loginUser(User user) {
		try {
			if (user.getUsername() == null || user.getUsername().isEmpty() ||
					user.getPassword() == null || user.getPassword().isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

			LoginResponse response = photoService.loginUser(user.getUsername(), user.getPassword());
			return ResponseEntity.ok(response);
		} catch (RuntimeException e) {
			if (e.getMessage() != null && e.getMessage().contains("Invalid credentials")) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@Override
	public ResponseEntity<LogoutUser200Response> logoutUser() {
		try {

			photoService.logoutUser(null);

			LogoutUser200Response response = new LogoutUser200Response();
			response.setMessage("Successfully logged out");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
}


