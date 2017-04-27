package net.muratov.mercury200;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@ComponentScan("net.muratov")
public class Mercury200ProviderApplication {

	public static void main(String[] args) {
		SpringApplication.run(Mercury200ProviderApplication.class, args);
	}
}
