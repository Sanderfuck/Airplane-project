package com.mpsdevelopment.airplane.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AirplaneProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirplaneProjectApplication.class, args);
	}

}
