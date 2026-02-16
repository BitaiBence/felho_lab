package hu.avhga.g3.lib.logger;

public enum MdcField {
	USER_NAME("userName"),
	USER_GROUP_NAME("userGroupName"),
	IMPERSONATOR_NAME("impersonatorName"),
	ENTRY_POINT_NAME("entryPointName");

	final String name;

	MdcField(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
