package hu.avhga.g3.lib;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:META-INF/build-info.properties")
public class ApplicationInfo {

	@Value("${build.Version-Number:unknown-version}")
	private String versionNumber;

	@Value("${build.Revision-Number:unknown-revision}")
	private String revisionNumber;

	public String getVersion() {
		return versionNumber;
	}

	public String getAppBuildNumber() {
		return revisionNumber;
	}
}


