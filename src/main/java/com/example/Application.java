package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Главный класс приложения
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "com.example")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
