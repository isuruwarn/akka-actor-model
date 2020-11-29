package org.warn.example.akka.actormodel.iot;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import lombok.extern.slf4j.Slf4j;

/**
 * https://spring.io/guides/gs/spring-boot/
 *
 */
@SpringBootApplication
@Slf4j
public class IoTSpringBootApp {
	
	public static void main(String[] args) {
		SpringApplication.run(IoTSpringBootApp.class, args);
	}
	
	// the code below is NOT required to start a springboot application. just for demonstration.
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			log.info("Let's inspect the beans provided by Spring Boot:");

			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				log.info(beanName);
			}

		};
	}

}