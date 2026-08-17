package com.example.bookcatalogservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    // Friendly response at the root path instead of the default Whitelabel error page.
    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "service", "Book Catalog Service",
                "status", "UP",
                "endpoints", "/books",
                "docs", "http://localhost:9099"
        );
    }
}