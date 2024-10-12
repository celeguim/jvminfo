package com.celeghin.jvminfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JvminfoApplication {

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			System.out.println(String.format("param %s", args[i]));
		}

		SpringApplication.run(JvminfoApplication.class, args);

	}

}
