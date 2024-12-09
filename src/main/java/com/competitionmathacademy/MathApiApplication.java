package com.competitionmathacademy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.competitionmathacademy.mathapi.model")
public class MathApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(MathApiApplication.class, args);
	}

}
