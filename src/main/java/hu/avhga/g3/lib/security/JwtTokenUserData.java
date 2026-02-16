package hu.avhga.g3.lib.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JwtTokenUserData implements Serializable {

	@Serial
	private static final long serialVersionUID = 12343211234321L;

	private String userName;
	private String userGroupName;
	private String impersonatorName;
	private String givenName;
	private String familyName;
	private String realm;

	public JwtTokenUserData copy() {
		return JwtTokenUserData.builder()
				.userName(userName)
				.userGroupName(userGroupName)
				.impersonatorName(impersonatorName)
				.givenName(givenName)
				.familyName(familyName)
				.realm(realm)
				.build();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserGroupName() {
		return userGroupName;
	}

	public void setUserGroupName(String userGroupName) {
		this.userGroupName = userGroupName;
	}

	public String getImpersonatorName() {
		return impersonatorName;
	}

	public void setImpersonatorName(String impersonatorName) {
		this.impersonatorName = impersonatorName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getFamilyName() {
		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	public String getRealm() {
		return realm;
	}

	public String getRealmName() {
		return realm.substring(realm.lastIndexOf("/") + 1);
	}

	public void setRealm(String realm) {
		this.realm = realm;
	}
}