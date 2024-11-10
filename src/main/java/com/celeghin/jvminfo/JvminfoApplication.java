package com.celeghin.jvminfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JvminfoApplication {
	public static void main(String[] args) {
        for (String arg : args) {
            System.out.printf("param %s%n%n", arg);
        }
		SpringApplication.run(JvminfoApplication.class, args);
	}
}
