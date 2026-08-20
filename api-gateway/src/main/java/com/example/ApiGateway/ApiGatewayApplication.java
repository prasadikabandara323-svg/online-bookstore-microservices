package com.example.ApiGateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
        System.out.println("=================================================");
        System.out.println(" API Gateway is running -> http://localhost:8080");
        System.out.println(" Try: http://localhost:8080/api/auth/login (POST)");
        System.out.println("=================================================");
    }
}
