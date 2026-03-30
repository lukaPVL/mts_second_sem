package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Главный класс приложения
 */
@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
@ComponentScan(basePackages = "com.example")
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
