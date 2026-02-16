package hu.avhga.g3.lib.persistence.configuration;

import hu.avhga.g3.lib.persistence.configuration.MultiSchemaIntegrityChecker;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionCustomizer;

public class EnableIntegrityChecker implements SessionCustomizer {

	@Override
	public void customize(Session session) {
		session.setIntegrityChecker(new MultiSchemaIntegrityChecker());
		session.getIntegrityChecker().checkDatabase();
		session.getIntegrityChecker().dontCatchExceptions();
	}
}