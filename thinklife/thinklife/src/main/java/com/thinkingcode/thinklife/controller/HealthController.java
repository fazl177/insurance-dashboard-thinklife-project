package com.thinkingcode.thinklife.controller;

import com.thinkingcode.thinklife.repository.ClaimRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class HealthController {

    private final ClaimRepository claimRepository;

    public HealthController(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    @GetMapping("/health/db")
    public Map<String, Object> checkDatabaseConnection() {
        try {
            long count = claimRepository.count();
            return Map.of("ok", true, "claimsCount", count);
        } catch (Exception e) {
            return Map.of("ok", false, "error", e.getMessage());
        }
    }
}
