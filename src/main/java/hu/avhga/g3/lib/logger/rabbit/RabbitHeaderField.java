package hu.avhga.g3.lib.logger.rabbit;

public enum RabbitHeaderField {
	USER_NAME("userName"),
	USER_GROUP_NAME("userGroupName"),
	IMPERSONATOR_NAME("impersonatorName");

	final String name;

	RabbitHeaderField(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
