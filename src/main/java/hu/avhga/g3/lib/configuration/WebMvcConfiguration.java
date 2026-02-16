package hu.avhga.g3.lib.configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import hu.avhga.g3.lib.logger.rest.HeaderRequestInterceptor;
import hu.avhga.g3.lib.security.OidcProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.format.datetime.DateFormatterRegistrar;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Configuration
public class WebMvcConfiguration {

	@Bean
	@ConfigurationProperties(prefix = "spring.jackson")
	@ConditionalOnMissingBean
	JacksonProperties jacksonProperties() {
		JacksonProperties properties = new JacksonProperties();
		properties.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
		properties.getSerialization().put(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		properties.getDeserialization().put(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		return properties;
	}

	@Bean
	@ConditionalOnMissingBean
	public HttpMessageConverters messageConverters(StringHttpMessageConverter stringHttpMessageConverter,
			MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter) {
		// StringHttpMessageConverternek kell lennie az elsonek, hogy a SwaggerUI-on jol parsolja a JSON-t.
		return new HttpMessageConverters(Arrays.asList(stringHttpMessageConverter, mappingJackson2HttpMessageConverter));
	}

	@Bean
	public DateTimeFormatterRegistrar dateTimeFormatterRegistrar(FormatterRegistry registry) {
		DateTimeFormatterRegistrar dateTimeRegistrar = new DateTimeFormatterRegistrar();
		dateTimeRegistrar.setDateFormatter(DateTimeFormatter.ISO_DATE);
		dateTimeRegistrar.setDateTimeFormatter(DateTimeFormatter.ISO_DATE_TIME);
		dateTimeRegistrar.registerFormatters(registry);
		return dateTimeRegistrar;
	}

	@Bean
	public DateFormatterRegistrar DateFormatterRegistrar(FormatterRegistry registry) {
		DateFormatterRegistrar dateRegistrar = new DateFormatterRegistrar();
		dateRegistrar.setFormatter(new DateFormatter("yyyy-MM-dd"));
		dateRegistrar.registerFormatters(registry);
		return dateRegistrar;
	}

	@Bean
	@ConditionalOnProperty(value = "local.cors-config", havingValue = "true")
	public WebMvcConfigurer webMvcConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry corsRegistry) {
				corsRegistry.addMapping("/**")
						.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE");
			}
		};
	}

	@Bean
	public HeaderRequestInterceptor headerRequestInterceptor(OidcProperties.HeaderProperties headerProperties) {
		return new HeaderRequestInterceptor(headerProperties.getUserTokenHeaderName());
	}

	@Bean
	RestTemplate restTemplate(RestTemplateBuilder builder, HeaderRequestInterceptor headerRequestInterceptor) {
		RestTemplate restTemplate = builder.build();
		restTemplate.getInterceptors().add(headerRequestInterceptor);
		return restTemplate;
	}
}