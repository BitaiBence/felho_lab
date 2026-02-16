package hu.avhga.g3.lib.logger;

import hu.avhga.g3.lib.logger.MdcField;
import hu.avhga.g3.lib.logger.rabbit.RabbitHeaderField;
import hu.avhga.g3.lib.security.JwtTokenUserData;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.function.Consumer;

@Service
public class MdcUtilService {

	public void addUserDataToMdc(String method, JwtTokenUserData dto) {
		mdcPutIfHasText(MdcField.ENTRY_POINT_NAME.getName(), method);
		if ( dto != null ) {
			mdcPutIfHasText(MdcField.USER_NAME.getName(), dto.getUserName());
			mdcPutIfHasText(MdcField.USER_GROUP_NAME.getName(), dto.getUserGroupName());
			mdcPutIfHasText(MdcField.IMPERSONATOR_NAME.getName(), dto.getImpersonatorName());
		}
	}

	public void removeUserDataFromMdc() {
		MDC.remove(MdcField.ENTRY_POINT_NAME.getName());
		MDC.remove(MdcField.USER_NAME.getName());
		MDC.remove(MdcField.USER_GROUP_NAME.getName());
		MDC.remove(MdcField.IMPERSONATOR_NAME.getName());
	}

	private void mdcPutIfHasText(String mdcKey, String pathKey) {
		if ( StringUtils.hasText(pathKey) ) {
			MDC.put(mdcKey, pathKey);
		}
	}

	public JwtTokenUserData addUserDataToMdcFromRabbitMessage(String consumerQueue, Map<String, Object> headers) {
		JwtTokenUserData dto = getMdcUserDataFromRabbitMessage(headers);
		addUserDataToMdc(consumerQueue, dto);
		return dto;
	}

	private JwtTokenUserData getMdcUserDataFromRabbitMessage(Map<String, Object> propertyMap) {
		JwtTokenUserData dto = new JwtTokenUserData();
		if ( !propertyMap.isEmpty() ) {
			setUserDataField(propertyMap.get(RabbitHeaderField.USER_NAME.getName()), dto::setUserName);
			setUserDataField(propertyMap.get(RabbitHeaderField.USER_GROUP_NAME.getName()), dto::setUserGroupName);
			setUserDataField(propertyMap.get(RabbitHeaderField.IMPERSONATOR_NAME.getName()), dto::setImpersonatorName);
		}
		return dto;
	}

	private void setUserDataField(Object object, Consumer<String> setter) {
		if ( object != null ) {
			setter.accept((String) object);
		}
	}
}