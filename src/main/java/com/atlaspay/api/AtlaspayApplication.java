package com.atlaspay.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class AtlaspayApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtlaspayApplication.class, args);
	}

}
