package hu.avhga.g3.lib.exception;

import hu.avhga.g3.lib.exception.BaseErrorCode;
import hu.avhga.g3.lib.exception.ErrorCode;
import hu.avhga.g3.lib.exception.RestResponseException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class ValidationException extends RestResponseException {

	@Serial
	private static final long serialVersionUID = -6046830966338028350L;

	private static final ErrorCode DEFAULT_CODE = BaseErrorCode.CL005;
	private static final HttpStatus DEFAULT_HTTP_STATUS = DEFAULT_CODE.getHttpStatus();

	public ValidationException() {
		super(DEFAULT_CODE);
	}

	public ValidationException(Throwable cause) {
		super(DEFAULT_CODE, cause);
	}

	public ValidationException(String errorMessage) {
		super(DEFAULT_CODE, errorMessage);
	}

	public ValidationException(String errorMessage, Throwable cause) {
		super(DEFAULT_CODE, errorMessage, cause);
	}

	public ValidationException(ErrorCode errorCode) {
		super(errorCode, DEFAULT_HTTP_STATUS);
	}

	public ValidationException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, DEFAULT_HTTP_STATUS, cause);
	}

	public ValidationException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, DEFAULT_HTTP_STATUS, errorMessage);
	}

	public ValidationException(ErrorCode errorCode, String errorMessage, Throwable cause) {
		super(errorCode, DEFAULT_HTTP_STATUS, errorMessage, cause);
	}
}