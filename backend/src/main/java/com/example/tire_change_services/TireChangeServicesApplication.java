package com.example.tire_change_services;

import com.example.tire_change_services.config.WorkshopInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
public class TireChangeServicesApplication {

	public static void main(String[] args) {
		SpringApplication.run(TireChangeServicesApplication.class, args);
	}

}
