package com.disneymovie.disneyJava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.disneymovie.disneyJava")
public class DisneyJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DisneyJavaApplication.class, args);
	}
}
