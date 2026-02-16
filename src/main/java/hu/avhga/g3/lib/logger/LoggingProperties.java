package hu.avhga.g3.lib.logger;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration("loggingProperties")
@ConfigurationProperties(prefix = "logging.rest")
public class LoggingProperties {
	private String hideFields;
	private int maxPayloadLength = 10000;
	private HeaderProperties header = new HeaderProperties();

	@Getter
	@Setter
	public static class HeaderProperties {
		private String traceId = "traceparent";
	}

}
