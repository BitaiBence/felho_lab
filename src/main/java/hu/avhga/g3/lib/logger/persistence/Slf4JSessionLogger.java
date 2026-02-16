package hu.avhga.g3.lib.logger.persistence;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Slf4JSessionLogger extends AbstractSessionLog {

	public static final Logger LOGGER = LoggerFactory.getLogger(Slf4JSessionLogger.class);

	@Override
	public void log(SessionLogEntry sessionLogEntry) {

		switch (sessionLogEntry.getLevel()) {
		case SEVERE -> LOGGER.error(sessionLogEntry.getMessage() + paramsToString(sessionLogEntry), sessionLogEntry.getException());
		case WARNING -> LOGGER.warn(sessionLogEntry.getMessage() + paramsToString(sessionLogEntry));
		case INFO, CONFIG -> LOGGER.info(sessionLogEntry.getMessage() + paramsToString(sessionLogEntry));
		case FINE -> LOGGER.debug(sessionLogEntry.getMessage() + paramsToString(sessionLogEntry));
		default -> LOGGER.trace(sessionLogEntry.getMessage() + paramsToString(sessionLogEntry));
		}
	}

	private String paramsToString(SessionLogEntry sessionLogEntry) {
		if ( sessionLogEntry.getParameters() == null || sessionLogEntry.getParameters().length == 0 ) {
			return "";
		}
		return " - params:" + Arrays.toString(sessionLogEntry.getParameters());
	}
}