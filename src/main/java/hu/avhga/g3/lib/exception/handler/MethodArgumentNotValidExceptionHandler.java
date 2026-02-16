package hu.avhga.g3.lib.exception.handler;

import hu.avhga.g3.lib.api.model.ErrorResponse;
import hu.avhga.g3.lib.exception.BaseErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Order(2)
@ControllerAdvice
@Slf4j
public class MethodArgumentNotValidExceptionHandler {

	@ResponseStatus(UNPROCESSABLE_ENTITY)
	@ResponseBody
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ErrorResponse methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		return processFieldErrors(fieldErrors);
	}

	private ErrorResponse processFieldErrors(List<FieldError> fieldErrors) {
		String message = createMessage(fieldErrors);

		return new ErrorResponse()
				.code(BaseErrorCode.CL005.name())
				.message(message);
	}

	private String createMessage(List<FieldError> fieldErrors) {
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < fieldErrors.size(); i++ ) {
			FieldError fieldError = fieldErrors.get(i);
			sb.append("'")
					.append(fieldError.getField())
					.append("' mező tartalma hibás: ")
					.append(magyaritas(fieldError.getDefaultMessage()));
			if ( i + 1 < fieldErrors.size() ) {
				sb.append("; ");
			}
		}
		return sb.toString();
	}

	private String magyaritas(String defaultMessage) {
		if ( defaultMessage == null ) {
			return null;
		}
		// min/max ellenőrzéseknél a \org\hibernate\validator\ValidationMessages_hu.properties bug miatt kell
		return defaultMessage.replace("or equal to", "vagy egyenlőnek");
	}

}