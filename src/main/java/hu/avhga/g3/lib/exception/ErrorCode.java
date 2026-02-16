package hu.avhga.g3.lib.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {

	String name();

	HttpStatus getHttpStatus();

	String getMessage();

}
