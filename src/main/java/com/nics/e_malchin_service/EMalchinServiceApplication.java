package com.nics.e_malchin_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class EMalchinServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EMalchinServiceApplication.class, args);
	}
}
