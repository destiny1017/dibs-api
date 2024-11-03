package com.ably.dibs_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class DibsApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(DibsApiApplication.class, args);
	}

}
