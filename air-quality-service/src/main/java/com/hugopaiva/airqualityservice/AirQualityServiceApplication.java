package com.hugopaiva.airqualityservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AirQualityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirQualityServiceApplication.class, args);
	}

}
