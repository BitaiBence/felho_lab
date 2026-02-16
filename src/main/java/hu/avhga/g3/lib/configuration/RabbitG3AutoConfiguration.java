package hu.avhga.g3.lib.configuration;

import brave.Tracing;
import brave.spring.rabbit.SpringRabbitTracing;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.avhga.g3.lib.logger.MdcUtilService;
import hu.avhga.g3.lib.logger.rabbit.HeadersFromMdcMessagePropertiesConverter;
import hu.avhga.g3.lib.logger.rabbit.RabbitUserDetailsInterceptor;
import org.springframework.amqp.rabbit.config.ContainerCustomizer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.support.MessagePropertiesConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(ConnectionFactory.class)
public class RabbitG3AutoConfiguration {

	@Value("${spring.rabbitmq.channel-transacted:true}")
	private boolean channelTransacted;

	@Bean
	@ConditionalOnMissingBean
	Jackson2JsonMessageConverter messageConverter(ObjectMapper jsonObjectMapper) {
		return new Jackson2JsonMessageConverter(jsonObjectMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	HeadersFromMdcMessagePropertiesConverter messagePropertiesConverter() {
		return new HeadersFromMdcMessagePropertiesConverter();
	}

	@Bean
	@ConditionalOnMissingBean
	RabbitUserDetailsInterceptor rabbitListenerInterceptor(MdcUtilService mdcUtilService) {
		return new RabbitUserDetailsInterceptor(mdcUtilService);
	}

	@Bean
	@ConditionalOnMissingBean
	ContainerCustomizer<SimpleMessageListenerContainer> simpleMessageListenerContainerCustomizer(SpringRabbitTracing springRabbitTracing,
			RabbitUserDetailsInterceptor rabbitUserDetailsInterceptor) {
		return container -> {
			container.setAdviceChain(rabbitUserDetailsInterceptor);
			springRabbitTracing.decorateMessageListenerContainer(container);
		};
	}

	@Bean
	@ConditionalOnMissingBean
	RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter,
			MessagePropertiesConverter messagePropertiesConverter, SpringRabbitTracing springRabbitTracing) {
		RabbitTemplate rabbitTemplate = springRabbitTracing.newRabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(messageConverter);
		rabbitTemplate.setChannelTransacted(channelTransacted);
		rabbitTemplate.setMessagePropertiesConverter(messagePropertiesConverter);
		return rabbitTemplate;
	}

	@Bean
	@ConditionalOnMissingBean
	SpringRabbitTracing springRabbitTracing(Tracing tracing) {
		return SpringRabbitTracing.newBuilder(tracing).build();
	}
}