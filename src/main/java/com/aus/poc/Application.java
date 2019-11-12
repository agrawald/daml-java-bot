package com.aus.poc;

import com.aus.poc.service.DamlContractSvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String[] args) {
		final ApplicationContext ctx = SpringApplication.run(Application.class, args);
		ctx.getBean(DamlContractSvc.class).fetch();
	}
}
