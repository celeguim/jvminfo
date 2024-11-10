package com.celeghin.jvminfo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JvminfoApplication {
	public static void main(String[] args) {
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


//Prop: java.vm.name : OpenJDK 64-Bit Server VM
//Prop: java.vm.version : 17.0.13+0

//Prop: os.version : 14.7.1
//Prop: os.arch : aarch64

//Prop: java.class.version : 61.0

//Prop: java.runtime.version : 17.0.13+0
//Prop: java.version : 17.0.13
//Prop: java.runtime.name : OpenJDK Runtime Environment
