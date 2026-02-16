package hu.avhga.g3.lib.logger.rabbit;

import com.rabbitmq.client.AMQP;
import hu.avhga.g3.lib.logger.MdcField;
import hu.avhga.g3.lib.logger.rabbit.RabbitHeaderField;
import org.slf4j.MDC;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.support.DefaultMessagePropertiesConverter;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class HeadersFromMdcMessagePropertiesConverter extends DefaultMessagePropertiesConverter {

	@Override
	public AMQP.BasicProperties fromMessageProperties(MessageProperties source, String charset) {
		Map<String, String> headers = new HashMap<>();
		addHeaderIfHasText(headers, MdcField.USER_NAME, RabbitHeaderField.USER_NAME);
		addHeaderIfHasText(headers, MdcField.USER_GROUP_NAME, RabbitHeaderField.USER_GROUP_NAME);
		addHeaderIfHasText(headers, MdcField.IMPERSONATOR_NAME, RabbitHeaderField.IMPERSONATOR_NAME);

		source.getHeaders().putAll(headers);
		return super.fromMessageProperties(source, charset);
	}

	private void addHeaderIfHasText(Map<String, String> headers, MdcField mdcField, RabbitHeaderField headerField) {
		String value = MDC.get(mdcField.getName());
		if ( StringUtils.hasText(value) ) {
			headers.put(headerField.getName(), value);
		}
	}
}