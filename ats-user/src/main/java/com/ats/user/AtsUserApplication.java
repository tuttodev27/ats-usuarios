package com.ats.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AtsUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(AtsUserApplication.class, args);
	}

}
