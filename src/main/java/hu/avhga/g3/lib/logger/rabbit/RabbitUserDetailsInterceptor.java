package hu.avhga.g3.lib.logger.rabbit;

import hu.avhga.g3.lib.logger.MdcUtilService;
import hu.avhga.g3.lib.security.JwtTokenUserData;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;

public class RabbitUserDetailsInterceptor implements MethodInterceptor {
	private static final Logger logger = LoggerFactory.getLogger(RabbitUserDetailsInterceptor.class);

	private final MdcUtilService mdcUtilService;

	public RabbitUserDetailsInterceptor(MdcUtilService mdcUtilService) {
		this.mdcUtilService = mdcUtilService;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Message message = (Message) invocation.getArguments()[1];

		String queueName = message.getMessageProperties().getConsumerQueue();
		JwtTokenUserData dto = mdcUtilService.addUserDataToMdcFromRabbitMessage(queueName, message.getMessageProperties().getHeaders());

		logger.info("RabbitMQ üzenet érkezett a {} queueba, felhasználónév: {}", queueName, dto.getUserName());

		try {
			return invocation.proceed();
		} catch (Exception e) {
			logger.error("Hiba történt a RabbitMQ üzenet feldolgozása közben!", e);
			throw e;
		} finally {
			logger.info("RabbitMQ üzenet foldolgozása befejeződött.");
			mdcUtilService.removeUserDataFromMdc();
		}
	}
}
