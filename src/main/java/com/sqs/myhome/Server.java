package com.sqs.myhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.sqs.myhome")
public class Server {

	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}
}
