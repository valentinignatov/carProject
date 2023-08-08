package com.cars;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
	public static void main(String[] args) {
//		System.setProperty("spring.main.lazy-initialization", "true");
		SpringApplication.run(Application.class, args);
	}
}
