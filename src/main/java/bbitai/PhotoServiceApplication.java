package bbitai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class PhotoServiceApplication {

	private static final Logger log = LoggerFactory.getLogger(PhotoServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PhotoServiceApplication.class, args);
	}
}