package hu.avhga.g3.lib.exception;

import hu.avhga.g3.lib.exception.BaseErrorCode;
import hu.avhga.g3.lib.exception.ErrorCode;
import hu.avhga.g3.lib.exception.RestResponseException;
import org.springframework.http.HttpStatus;

import java.io.Serial;

/**
 * Szerver oldali hiba. Olyan hibák dobására való, amivel a modul nem tud mit tenni, és nem bejött adatokkal kapcsolatos probléma okozza.
 */
public class ServiceException extends RestResponseException {

	@Serial
	private static final long serialVersionUID = -2703324308231144587L;

	private static final ErrorCode DEFAULT_CODE = BaseErrorCode.SVR001;
	private static final HttpStatus DEFAULT_HTTP_STATUS = DEFAULT_CODE.getHttpStatus();

	public ServiceException() {
		super(DEFAULT_CODE);
	}

	public ServiceException(Throwable cause) {
		super(DEFAULT_CODE, cause);
	}

	public ServiceException(String errorMessage) {
		super(DEFAULT_CODE, errorMessage);
	}

	public ServiceException(String errorMessage, Throwable cause) {
		super(DEFAULT_CODE, errorMessage, cause);
	}

	public ServiceException(ErrorCode errorCode) {
		super(errorCode, DEFAULT_HTTP_STATUS);
	}

	public ServiceException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, DEFAULT_HTTP_STATUS, cause);
	}

	public ServiceException(ErrorCode errorCode, String errorMessage) {
		super(errorCode, DEFAULT_HTTP_STATUS, errorMessage);
	}

	public ServiceException(ErrorCode errorCode, String errorMessage, Throwable cause) {
		super(errorCode, DEFAULT_HTTP_STATUS, errorMessage, cause);
	}
}