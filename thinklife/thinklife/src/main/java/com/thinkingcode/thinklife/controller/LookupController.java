package com.thinkingcode.thinklife.controller;

import com.thinkingcode.thinklife.repo.LookupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200") // Be specific for security
public class LookupController {

    private final LookupRepository lookups;

    @Autowired
    public LookupController(LookupRepository lookups) {
        this.lookups = lookups;
    }

    // --- EXISTING: Insurance Types ---
    @GetMapping("/dropdown/insurance-types")
    public List<Map<String, Object>> insuranceTypes() {
        return lookups.insuranceTypes().stream().map(row -> Map.of(
                "insuranceType", row.get("id"),
                "insuranceTypeDesc", row.get("name")
        )).toList();
    }

    // --- EXISTING: Statuses ---
    @GetMapping("/dropdown/statuses")
    public List<Map<String, Object>> statuses() {
        return lookups.statuses();
    }

    // --- Examiners ---
    @GetMapping("/typeahead/examiners")
    public List<Map<String, Object>> examiners(@RequestParam String q) {
        return lookups.examiners(q);
    }

    // --- Organizations ---
    @GetMapping("/dropdown/organizations")
    public List<Map<String, Object>> organizations() {
        return lookups.organizations();
    }

    // --- Brokers ---
    @GetMapping("/dropdown/brokers")
    public List<Map<String, Object>> brokers() {
        return lookups.brokers();
    }

    // --- Status (for /api/lookups/status) ---
    @GetMapping("/lookups/status")
    public List<Map<String, Object>> statusLookup() {
        return lookups.statuses();
    }

    // --- Claimant Types (for both /api/lookups/claimantTypes and /dropdown/claimant-types) ---
    @GetMapping({"/lookups/claimantTypes", "/dropdown/claimant-types"})
    public List<Map<String, Object>> claimantTypes() {
        return lookups.claimantTypes();
    }
}