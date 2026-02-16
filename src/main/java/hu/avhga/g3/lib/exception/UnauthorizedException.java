package hu.avhga.g3.lib.exception;

import hu.avhga.g3.lib.exception.BaseErrorCode;
import hu.avhga.g3.lib.exception.ErrorCode;
import hu.avhga.g3.lib.exception.RestResponseException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class UnauthorizedException extends RestResponseException {

	@Serial
	private static final long serialVersionUID = -2704324308231144587L;

	private static final ErrorCode DEFAULT_CODE = BaseErrorCode.CL003;
	private static final HttpStatus DEFAULT_HTTP_STATUS = DEFAULT_CODE.getHttpStatus();

	public UnauthorizedException() {
		super(DEFAULT_CODE);
	}

	public UnauthorizedException(Throwable cause) {
		super(DEFAULT_CODE, cause);
	}

	public UnauthorizedException(String errorMessage) {
		super(DEFAULT_CODE, errorMessage);
	}

	public UnauthorizedException(String errorMessage, Throwable cause) {
		super(DEFAULT_CODE, errorMessage, cause);
	}

	public UnauthorizedException(ErrorCode errorCode) {
		super(errorCode, DEFAULT_HTTP_STATUS);
	}

	public UnauthorizedException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, DEFAULT_HTTP_STATUS, cause);
	}

	public UnauthorizedException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, DEFAULT_HTTP_STATUS, errorMessage);
	}

	public UnauthorizedException(ErrorCode errorCode, String errorMessage, Throwable cause) {
		super(errorCode, DEFAULT_HTTP_STATUS, errorMessage, cause);
	}
}