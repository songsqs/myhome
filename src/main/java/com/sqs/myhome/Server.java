package com.sqs.myhome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages="com.sqs.myhome")
@EnableScheduling
public class Server {

	public static void main(String[] args) {
		SpringApplication.run(Server.class, args);
	}
}
