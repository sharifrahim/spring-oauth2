package com.github.sharifrahim.oauth2.boilerplate_oauth2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BoilerplateOauth2Application {

	public static void main(String[] args) {
		SpringApplication.run(BoilerplateOauth2Application.class, args);
	}

}
