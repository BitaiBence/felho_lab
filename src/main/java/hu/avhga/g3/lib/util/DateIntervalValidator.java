package hu.avhga.g3.lib.util;

import java.time.LocalDate;

public class DateIntervalValidator {

	private DateIntervalValidator() {
	}

	public static boolean isDateFormValid(LocalDate dateFrom, LocalDate dateTo) {
		if ( dateFrom == null ) {
			return dateTo == null;
		}
		if ( dateTo == null ) {
			return true;
		}
		return !dateFrom.isAfter(dateTo);
	}
}
