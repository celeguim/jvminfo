package com.celeghin.jvminfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JvminfoApplication {
	public static void main(String[] args) {
//        System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");

        for (String arg : args) {
            System.out.printf("param %s%n%n", arg);
        }
        for (String prop : System.getProperties().stringPropertyNames()) {
            String value = System.getProperty(prop);
            System.out.printf("Prop: %s : %s %n", prop, value);
        }
		SpringApplication.run(JvminfoApplication.class, args);
	}
}
