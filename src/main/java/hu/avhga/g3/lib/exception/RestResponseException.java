package hu.avhga.g3.lib.exception;

import hu.avhga.g3.lib.exception.BaseErrorCode;
import hu.avhga.g3.lib.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.io.Serial;

public class RestResponseException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -2703324308231144587L;

	private final ErrorCode errorCode;
	private final HttpStatus httpStatus;
	private static final ErrorCode DEFAULT_CODE = BaseErrorCode.SVR001;

	public RestResponseException(String errorMessage) {
		this(DEFAULT_CODE, errorMessage);
	}

	public RestResponseException(Throwable cause) {
		this(DEFAULT_CODE, cause);
	}

	public RestResponseException(String errorMessage, Throwable cause) {
		this(DEFAULT_CODE, errorMessage, cause);
	}

	public RestResponseException(HttpStatus httpStatus, String errorMessage) {
		this(DEFAULT_CODE, httpStatus, errorMessage);
	}

	public RestResponseException(HttpStatus httpStatus, Throwable cause) {
		this(DEFAULT_CODE, httpStatus, cause);
	}

	public RestResponseException(HttpStatus httpStatus, String errorMessage, Throwable cause) {
		this(DEFAULT_CODE, httpStatus, errorMessage, cause);
	}

	public RestResponseException(ErrorCode errorCode) {
		this(errorCode, errorCode.getHttpStatus());
	}

	public RestResponseException(ErrorCode errorCode, String errorMessage) {
		this(errorCode, errorCode.getHttpStatus(), errorMessage);
	}

	public RestResponseException(ErrorCode errorCode, Throwable cause) {
		this(errorCode, errorCode.getHttpStatus(), cause);
	}

	public RestResponseException(ErrorCode errorCode, String errorMessage, Throwable cause) {
		this(errorCode, errorCode.getHttpStatus(), errorMessage, cause);
	}

	public RestResponseException(ErrorCode errorCode, HttpStatus httpStatus) {
		this(errorCode, httpStatus, errorCode.getMessage());
	}

	public RestResponseException(ErrorCode errorCode, HttpStatus httpStatus, String errorMessage) {
		this(errorCode, httpStatus, errorMessage, null);
	}

	public RestResponseException(ErrorCode errorCode, HttpStatus httpStatus, Throwable cause) {
		this(errorCode, httpStatus, errorCode.getMessage(), cause);
	}

	public RestResponseException(ErrorCode errorCode, HttpStatus httpStatus, String errorMessage, Throwable cause) {
		super(errorMessage, cause);
		Assert.notNull(errorCode, "ErrorCode megadása kötelező!");
		this.errorCode = errorCode;
		this.httpStatus = httpStatus;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
