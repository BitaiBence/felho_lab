package hu.avhga.g3.lib.exception;

import hu.avhga.g3.lib.exception.BaseErrorCode;
import hu.avhga.g3.lib.exception.ErrorCode;
import hu.avhga.g3.lib.exception.RestResponseException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

public class NoResultException extends RestResponseException {

	@Serial
	private static final long serialVersionUID = -2704329308231144587L;

	private static final ErrorCode DEFAULT_CODE = BaseErrorCode.CL004;
	private static final HttpStatus DEFAULT_HTTP_STATUS = DEFAULT_CODE.getHttpStatus();

	public NoResultException() {
		super(DEFAULT_CODE);
	}

	public NoResultException(Throwable cause) {
		super(DEFAULT_CODE, cause);
	}

	public NoResultException(String errorMessage) {
		super(DEFAULT_CODE, errorMessage);
	}

	public NoResultException(String errorMessage, Throwable cause) {
		super(DEFAULT_CODE, errorMessage, cause);
	}

	public NoResultException(ErrorCode errorCode) {
		super(errorCode, DEFAULT_HTTP_STATUS);
	}

	public NoResultException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, DEFAULT_HTTP_STATUS, cause);
	}

	public NoResultException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, DEFAULT_HTTP_STATUS, errorMessage);
	}

	public NoResultException(ErrorCode errorCode, String errorMessage, Throwable cause) {
		super(errorCode, DEFAULT_HTTP_STATUS, errorMessage, cause);
	}

	public NoResultException(Class<?> clazz, Long id) {
		super(DEFAULT_CODE, "A " + id + " azonosítójú " + clazz.getSimpleName() + " objektum nem található!");
	}

	public NoResultException(Class<?> clazz) {
		super(DEFAULT_CODE, "A keresett " + clazz.getSimpleName() + " objektum nem található!");
	}
}