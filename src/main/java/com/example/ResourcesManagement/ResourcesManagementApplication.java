package com.example.ResourcesManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ResourcesManagementApplication {

	public static void main(String[] args) {

		SpringApplication.run(ResourcesManagementApplication.class, args);
		System.out.println("Server run at: http://localhost:8080");
	}

}
