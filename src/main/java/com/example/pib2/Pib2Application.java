package com.example.pib2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Pib2Application {

	public static void main(String[] args) {
		SpringApplication.run(Pib2Application.class, args);
	}

}
