package hu.avhga.g3.lib.exception.handler;

import hu.avhga.g3.lib.api.model.ErrorResponse;
import hu.avhga.g3.lib.exception.RestResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
@Slf4j
public class BaseExceptionHandler {

	@ExceptionHandler(RestResponseException.class)
	protected ResponseEntity<ErrorResponse> handleRestResponseException(RestResponseException ex) {
		return new ResponseEntity<>(createErrorResponse(ex), ex.getHttpStatus());
	}

	private ErrorResponse createErrorResponse(RestResponseException ex) {
		log.error("Exception caught: {}", ex.getMessage(), ex);
		return new ErrorResponse()
				.code(ex.getErrorCode().name())
				.message(ex.getMessage());
	}
}