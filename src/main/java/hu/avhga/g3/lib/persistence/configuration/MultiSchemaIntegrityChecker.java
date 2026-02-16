package hu.avhga.g3.lib.persistence.configuration;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.i18n.ExceptionMessageGenerator;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Heckelés, mert az eclipselink 4.x-ben az integrity checker hibásan a DatabaseAccessor.getTableInfo(String tableName, String[] types, AbstractSession session)
 * metódust használja, ami a user default sémájából kérdezi le csak a táblákat. Utána a ClassDescriptor pedig a DatabaseAccessor.getColumnInfo(String tableName,
 * String columnName, AbstractSession session) metódust használja, aminek szintén ez a hibája.
 */
public class MultiSchemaIntegrityChecker extends IntegrityChecker {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LoggerFactory.getLogger(MultiSchemaIntegrityChecker.class);

	List<DatabaseTable> tables = new ArrayList<>();
	List<String> falsHibauzenetek = new ArrayList<>();
	String defaultQualifier = "dbo";

	@Override
	public void initializeTables(AbstractSession session) {
		try {
			session.getAccessor().incrementCallCount(session);
			defaultQualifier = session.getAccessor().getConnection().getSchema();
		} catch (SQLException e) {
			// lekezeli a getSchema-n belül
		}
		List<AbstractRecord> result = session.getAccessor().getTableInfo(null, null, null, null, session);
		for ( Iterator<AbstractRecord> iterator = result.iterator(); iterator.hasNext(); ) {
			AbstractRecord row = iterator.next();
			String tableName = ((String) row.get("TABLE_NAME"));
			String qualifier = ((String) row.get("TABLE_SCHEM"));
			if ( session.getPlatform().shouldForceFieldNamesToUpperCase() ) {
				this.tables.add(new DatabaseTable(tableName.toUpperCase(), qualifier.toUpperCase()));
			} else {
				this.tables.add(new DatabaseTable(tableName, qualifier));
			}
		}
	}

	@Override
	public boolean checkTable(DatabaseTable table, AbstractSession session) {
		if ( tables.size() == 0 ) {
			// load the tables from the session
			initializeTables(session);
		}

		if ( table.getTableQualifier() == null || table.getTableQualifier().isBlank() ) {
			return tables.contains(new DatabaseTable(table.getName(), defaultQualifier));
		} else {
			loadFalsHibauzenetek(table, session);
			return tables.contains(table);
		}
	}

	private void loadFalsHibauzenetek(DatabaseTable table, AbstractSession session) {
		if ( defaultQualifier.equals(table.getTableQualifier()) ) {
			return;
		}

		List<AbstractRecord> result = session.getAccessor().getColumnInfo(null, table.getTableQualifier(), table.getName(), null, session);
		if ( result.isEmpty() && session.getPlatform().shouldForceFieldNamesToUpperCase() ) {
			result = session.getAccessor().getColumnInfo(null, table.getTableQualifier(), table.getName().toLowerCase(), null, session);
		}
		List<String> databaseFields = new ArrayList<>();
		for ( Iterator<AbstractRecord> resultIterator = result.iterator(); resultIterator.hasNext(); ) {
			AbstractRecord row = resultIterator.next();
			if ( session.getPlatform().shouldForceFieldNamesToUpperCase() ) {
				databaseFields.add(((String) row.get("COLUMN_NAME")).toUpperCase());
			} else {
				databaseFields.add((String) row.get("COLUMN_NAME"));
			}
		}

		for ( String fieldName : databaseFields ) {
			Object[] args = { fieldName, table.getName() };
			String falseException = ExceptionMessageGenerator.buildMessage(DescriptorException.class,
					DescriptorException.FIELD_IS_NOT_PRESENT_IN_DATABASE, args);
			falsHibauzenetek.add(falseException);
		}
	}

	/**
	 * Mivel a ClassDescriptor field validációba nem lehet belenyúlni, ezért az onnan dobott hibát nyelem le, ha az nem valós
	 */
	@Override
	public void handleError(RuntimeException runtimeException) {
		if ( runtimeException instanceof DescriptorException de
				&& de.getErrorCode() == DescriptorException.FIELD_IS_NOT_PRESENT_IN_DATABASE
				&& falsHibauzenetek.stream()
						.filter(de.getMessage()::contains)
						.findAny().isPresent() ) {
			logger.warn("Fals integrity check hibaüzenet kiszűrve: {}", de.getMessage());
			return;
		}
		super.handleError(runtimeException);
	}
}
