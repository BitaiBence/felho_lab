package hu.avhga.g3.lib.persistence.configuration;

import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.SessionCustomizer;

/**
 * Tesztekhez kellhet, mert profilban nem lehet felülírni üresre egy korábban beállított értéket
 */
public class EmptySessionCustomizer implements SessionCustomizer {

	@Override
	public void customize(Session session) throws Exception {
		// üres
	}

}
