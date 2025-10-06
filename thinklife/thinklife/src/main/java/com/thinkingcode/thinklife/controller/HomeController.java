package com.thinkingcode.thinklife.controller;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Home Controller to handle root path and provide API information
 */
@RestController
@CrossOrigin(origins = "*")
public class HomeController {

    /**
     * Root endpoint - GET /
     * Provides basic API information and available endpoints
     */
    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "Claims Search API");
        response.put("version", "1.0.0");
        response.put("status", "running");
        response.put("description", "Spring Boot backend for Claims Search UI");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("fetchMyqueue", "GET /api/fetchMyqueue - Main claims search endpoint");
        endpoints.put("claims", "GET /api/claims - Alternative claims search");
        endpoints.put("export", "GET /api/claims/export?format={csv|xlsx|json} - Export claims data");
        endpoints.put("lookups", "GET /api/lookups/{insurance-types|statuses|claimant-types|organizations}");
        endpoints.put("typeahead", "GET /api/typeahead/{employers|programs|policies|examiners|underwriters|brokers}?q=searchterm");
        endpoints.put("h2-console", "GET /h2-console - Database console (development only)");
        
        response.put("endpoints", endpoints);
        return response;
    }

    /**
     * Health check endpoint - GET /health
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Claims Search API");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}