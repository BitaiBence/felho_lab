package hu.avhga.g3.lib.exception.handler;

import hu.avhga.g3.lib.api.model.ErrorResponse;
import hu.avhga.g3.lib.exception.BaseErrorCode;
import hu.avhga.g3.lib.exception.ErrorCode;
import jakarta.persistence.NoResultException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.NoSuchElementException;

@Order(3)
@RestControllerAdvice
@Slf4j
public class SpringExceptionHandler {
	/**
	 * Handle exceptions not caught by any of the other handlers.
	 *
	 * @return HTTP Status 500 - {@link HttpStatus#INTERNAL_SERVER_ERROR} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public ErrorResponse handle(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		return createErrorResponse(ex, BaseErrorCode.SVR001);
	}

	/**
	 * Handle exceptions that result {@link HttpStatus#BAD_REQUEST} defined by
	 * {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver}
	 *
	 * @return HTTP Status 400 - {@link HttpStatus#BAD_REQUEST} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler({
			MissingPathVariableException.class,
			ServletRequestBindingException.class,
			TypeMismatchException.class,
			HttpMessageNotReadableException.class,
			MissingServletRequestParameterException.class,
			MissingServletRequestPartException.class,
			BindException.class,
	})
	public ErrorResponse handleBadRequest(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		return createErrorResponse(ex, BaseErrorCode.CL001);
	}

	/**
	 * Handle {@link AccessDeniedException}.
	 *
	 * @return HTTP Status 403 - {@link HttpStatus#FORBIDDEN} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.FORBIDDEN)
	@ExceptionHandler(AccessDeniedException.class)
	protected ErrorResponse handleAccessDenied(Exception ex) {
		return createErrorResponse(ex, BaseErrorCode.CL003);
	}

	/**
	 * Handle {@link NoHandlerFoundException}. In order to be able to handle this exception, the {@link org.springframework.web.servlet.DispatcherServlet} has
	 * to be configured to throw this exception, which is disabled by default.
	 *
	 * @return HTTP Status 404 - {@link HttpStatus#NOT_FOUND} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ExceptionHandler({
			NoHandlerFoundException.class,
			NoResultException.class,
			NoSuchElementException.class,
			NoResourceFoundException.class })
	public ErrorResponse handleNotFound(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		return createErrorResponse(ex, BaseErrorCode.CL004);
	}

	/**
	 * Handle {@link HttpRequestMethodNotSupportedException} when no request handler was found for the HTTP method.
	 *
	 * @return HTTP Status 405 - {@link HttpStatus#METHOD_NOT_ALLOWED} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ErrorResponse handleMethodNotSupported(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		return createErrorResponse(ex, BaseErrorCode.CL001);
	}

	/**
	 * Handle {@link HttpMediaTypeNotAcceptableException} when no message converter was found that is acceptable for the client - defined in the Accept header.
	 *
	 * @return HTTP Status 406 - {@link HttpStatus#NOT_ACCEPTABLE} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
	@ExceptionHandler(HttpMediaTypeNotAcceptableException.class)
	public ErrorResponse handleNotAcceptable(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		return createErrorResponse(ex, BaseErrorCode.CL001);
	}

	/**
	 * Handle {@link HttpMediaTypeNotSupportedException} when no message converters were found for the content.
	 *
	 * @return HTTP Status 415 - {@link HttpStatus#UNSUPPORTED_MEDIA_TYPE} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
	@ExceptionHandler(HttpMediaTypeNotSupportedException.class)
	public ErrorResponse handleMediaTypeNotSupported(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		return createErrorResponse(ex, BaseErrorCode.CL001);
	}

	/**
	 * Handle {@link IllegalArgumentException}.
	 *
	 * @return HTTP Status 422 - {@link HttpStatus#UNPROCESSABLE_ENTITY} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
	@ExceptionHandler(IllegalArgumentException.class)
	protected ErrorResponse handleIllegalArgument(Exception ex) {
		return createErrorResponse(ex, BaseErrorCode.CL005);
	}

	/**
	 * Handle exceptions that result {@link HttpStatus#INTERNAL_SERVER_ERROR} defined by
	 * {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver}
	 *
	 * @return HTTP Status 500 - {@link HttpStatus#INTERNAL_SERVER_ERROR} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler({
			ConversionNotSupportedException.class,
			HttpMessageNotWritableException.class
	})
	public ErrorResponse handleInternalError(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		return createErrorResponse(ex, BaseErrorCode.SVR001);
	}

	/**
	 * Handle {@link AsyncRequestTimeoutException} when an async request timed out.
	 *
	 * @return HTTP Status 503 - {@link HttpStatus#SERVICE_UNAVAILABLE} with {@link ErrorResponse}
	 */
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	@ExceptionHandler(AsyncRequestTimeoutException.class)
	public ErrorResponse handleTimeout(Exception ex, HttpServletRequest request, HttpServletResponse response) {
		return createErrorResponse(ex, BaseErrorCode.SVR001);
	}

	private ErrorResponse createErrorResponse(Exception exception, ErrorCode errorCode) {
		log.error("Exception caught: " + exception.getClass().getName() + " - " + exception.getMessage(), exception);
		return new ErrorResponse()
				.code(errorCode.name())
				.message(exception.getLocalizedMessage());
	}
}