package hu.avhga.g3.lib.exception;

import hu.avhga.g3.lib.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BaseErrorCode implements ErrorCode {

	CL001(HttpStatus.BAD_REQUEST, "Kliens oldali hiba."),
	CL002(HttpStatus.UNAUTHORIZED, "Hibás autentikációs adatok!"),
	CL003(HttpStatus.FORBIDDEN, "Nincs elég jogosultsága a művelet végrehajtásához!"),
	CL004(HttpStatus.NOT_FOUND, "A keresett objektum nem található!"),
	CL005(HttpStatus.UNPROCESSABLE_ENTITY, "Validációs hiba történt a kért művelet végrehajtása során!"),

	DOC001(HttpStatus.BAD_REQUEST, "Nem került beküldésre dokumentum!"),
	DOC002(HttpStatus.BAD_REQUEST, "A dokumentum könyvtárának a neve üres."),
	DOC003(HttpStatus.BAD_REQUEST, "A dokumentum neve üres."),
	DOC004(HttpStatus.BAD_REQUEST, "A dokumentum elérési útvonala üres."),
	DOC005(HttpStatus.INTERNAL_SERVER_ERROR, "Váratlan hiba történt a dokumentum kezelése során."),
	DOC006(HttpStatus.NOT_FOUND, "A dokumentum nem létezik vagy nem olvasható!"),
	DOC007(HttpStatus.NOT_FOUND, "A dokumentum nem található, elérési útvonala hibás."),

	SVR001(HttpStatus.INTERNAL_SERVER_ERROR, "Váratlan szerver oldali hiba.");

	private final HttpStatus httpStatus;
	private final String message;

	BaseErrorCode(HttpStatus httpStatus, String message) {
		this.httpStatus = httpStatus;
		this.message = message;
	}

	@Override
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	@Override
	public String getMessage() {
		return message;
	}
}