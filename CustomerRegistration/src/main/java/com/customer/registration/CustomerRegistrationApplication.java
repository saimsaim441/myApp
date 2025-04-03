package com.customer.registration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@ComponentScan(basePackages = "com.customer.registration")
@EnableAsync
public class CustomerRegistrationApplication {
	public static void main(String[] args) {
		SpringApplication.run(CustomerRegistrationApplication.class, args);
	}
}
