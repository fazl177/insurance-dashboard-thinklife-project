package com.thinkingcode.thinklife.controller;

import com.thinkingcode.thinklife.dto.ClaimFilter;
import com.thinkingcode.thinklife.repo.ClaimSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.util.*;

/**
 * Claims Controller implementing the API contract from project requirements
 * Supports all endpoints defined in the Claims Search UI specification
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ClaimController {

    private final ClaimSearchRepository claims;

    @Autowired
    public ClaimController(ClaimSearchRepository claims) {
        this.claims = claims;
    }

    /**
     * Main claims search endpoint - GET /api/claims
     * Returns data in the format expected by the frontend
     */
    @GetMapping("/claims")
    public ResponseEntity<Map<String, Object>> searchClaims(ClaimFilter filter) {
        try {
            Map<String,Object> result = claims.search(filter);

            // Transform response to match frontend expectations
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> items = (List<Map<String,Object>>) result.get("items");
            Long total = (Long) result.get("total");
            Integer page = filter.page != null ? filter.page : 1;
            Integer pageSize = filter.pageSize != null ? filter.pageSize : 20;

            // Convert items to frontend ClaimItem format
            List<Map<String,Object>> formattedItems = new ArrayList<>();
            for (Map<String,Object> item : items) {
                Map<String,Object> formatted = new LinkedHashMap<>();
                formatted.put("claimId", item.get("claimId"));
                formatted.put("claimNumber", item.get("claimNumber"));
                formatted.put("claimantName", item.get("claimantName"));
                formatted.put("ssn", item.get("ssn"));
                formatted.put("status", item.get("status"));
                formatted.put("statusFlag", item.get("statusFlag"));
                formatted.put("incidentDate", item.get("incidentDateStr"));
            formatted.put("adjOffice", item.get("adjustingOffice"));
            formatted.put("type", item.get("insuranceType"));
            formatted.put("brokerName", item.get("brokerName"));
                formatted.put("insured", item.get("insured"));
                formatted.put("injuryType", item.get("insuranceType"));
                formattedItems.add(formatted);
            }

            Map<String,Object> response = new LinkedHashMap<>();
            response.put("items", formattedItems);
            response.put("total", total);
            response.put("page", page);
            response.put("pageSize", pageSize);
            response.put("totalPages", (int) Math.ceil((double) total / pageSize));
            
            // Add pagination details
            response.put("pagination", Map.of(
                "currentPage", page,
                "pageSize", pageSize,
                "totalItems", total,
                "totalPages", (total + pageSize - 1) / pageSize
            ));
            
            if (filter != null) {
                response.put("filters", filter);
                response.put("sort", filter.sort);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log the error details for debugging
            System.err.println("Error in searchClaims: " + e.getMessage());
            e.printStackTrace();
            
            // Create a proper error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "An error occurred while processing your request");
            errorResponse.put("message", e.getMessage());
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
        }
    }

    /**
     * MyQueue endpoint - GET /api/fetchMyqueue
     * Returns data in the specified API response format
     */
    @GetMapping("/fetchMyqueue")
    public Map<String,Object> fetchMyQueue(ClaimFilter filter) {
        return claims.fetchMyQueue(filter);
    }

    /**
     * Export endpoint - GET /api/claims/export
     * Supports CSV, XLSX, and JSON formats
     */
    @GetMapping("/claims/export")
    public ResponseEntity<InputStreamResource> exportClaims(
            @RequestParam(defaultValue="csv") String format, 
            ClaimFilter filter) throws IOException {
        
        if ("csv".equalsIgnoreCase(format)) {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            java.io.OutputStreamWriter writer = new java.io.OutputStreamWriter(out);
            claims.streamCsv(filter, writer);
            writer.flush();
            java.io.ByteArrayInputStream in = new java.io.ByteArrayInputStream(out.toByteArray());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=claims.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(new InputStreamResource(in));
        }

        Map<String,Object> payload = claims.fetchMyQueue(filter);
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> items = (List<Map<String,Object>>) payload.get("data");
        if ("xlsx".equalsIgnoreCase(format)) {
            return exportAsExcel(items);
        } else if ("json".equalsIgnoreCase(format)) {
            return exportAsJson(payload);
        } else {
            return exportAsCsv(items);
        }
    }

    // Dropdown API endpoints
    
    /**
     * Insurance Types dropdown - GET /api/lookups/insurance-types
     */
    @GetMapping("/lookups/insurance-types")
    public List<Map<String,Object>> getInsuranceTypes() {
        return claims.getInsuranceTypes();
    }
    
    /**
     * Status dropdown - GET /api/lookups/statuses
     */
    @GetMapping("/lookups/statuses")
    public List<Map<String,Object>> getStatuses() {
        return claims.getStatuses();
    }
    
    /**
     * Claimant Types dropdown - GET /api/lookups/claimant-types
     */
    @GetMapping("/lookups/claimant-types")
    public List<Map<String,Object>> getClaimantTypes() {
        return claims.getClaimantTypes();
    }
    
    /**
     * Organizations dropdown - GET /api/lookups/organizations
     */
    @GetMapping("/lookups/organizations")
    public List<Map<String,Object>> getOrganizations() {
        return claims.getOrganizations();
    }

    /**
     * Brokers dropdown - GET /api/lookups/brokers
     */
    @GetMapping("/lookups/brokers")
    public List<Map<String,Object>> getBrokers() {
        return claims.getBrokers();
    }

    /**
     * Adjusting Offices dropdown - GET /api/lookups/adjustingOffices
     */
    @GetMapping("/lookups/adjustingOffices")
    public List<Map<String,Object>> getAdjustingOffices() {
        return claims.getAdjustingOffices();
    }

    /**
     * Examiner typeahead - GET /api/typeahead/examiner
     */
    @GetMapping("/typeahead/examiner")
    public List<Map<String,Object>> searchExaminers(@RequestParam String q) {
        return claims.examinerTypeahead(q);
    }
    
    // Typeahead API endpoints
    
    /**
     * Employer typeahead - GET /api/typeahead/employers
     */
    @GetMapping("/typeahead/employers")
    public List<Map<String,Object>> searchEmployers(@RequestParam String q) {
        return claims.employerTypeahead(q);
    }
    
    /**
     * Program typeahead - GET /api/typeahead/programs
     */
    @GetMapping("/typeahead/programs")
    public List<Map<String,Object>> searchPrograms(@RequestParam String q) {
        return claims.programTypeahead(q);
    }
    
    /**
     * Policy typeahead - GET /api/typeahead/policies
     */
    @GetMapping("/typeahead/policies")
    public List<Map<String,Object>> searchPolicies(@RequestParam String q) {
        return claims.policyTypeahead(q);
    }
    
    /**
     * Underwriter typeahead - GET /api/typeahead/underwriters
     */
    @GetMapping("/typeahead/underwriters")
    public List<Map<String,Object>> searchUnderwriters(@RequestParam String q) {
        return claims.underwriterTypeahead(q);
    }
    
    /**
     * Broker typeahead - GET /api/typeahead/brokers (legacy)
     */
    @GetMapping("/typeahead/brokers")
    public List<Map<String,Object>> searchBrokers(@RequestParam String q) {
        return claims.brokerTypeahead(q);
    }
    
    // Private helper methods for export functionality
    
    private ResponseEntity<InputStreamResource> exportAsExcel(List<Map<String,Object>> items) throws IOException {
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet("Claims");
        int r = 0;
        
        // Header row
        XSSFRow headerRow = sheet.createRow(r++);
        String[] headers = {"Claim Number", "Claimant ID", "Claimant Name", "SSN", "Status", 
                           "Incident Date", "Examiner", "Insured", "Adjusting Office"};
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        
        // Data rows
        for (Map<String,Object> row : items) {
            XSSFRow dataRow = sheet.createRow(r++);
            dataRow.createCell(0).setCellValue(getString(row, "claimNumber"));
            dataRow.createCell(1).setCellValue(getString(row, "claimantId"));
            dataRow.createCell(2).setCellValue(getString(row, "claimantName"));
            dataRow.createCell(3).setCellValue(getString(row, "ssn"));
            dataRow.createCell(4).setCellValue(getString(row, "status"));
            dataRow.createCell(5).setCellValue(getString(row, "incidentDateStr"));
            dataRow.createCell(6).setCellValue(getString(row, "examiner"));
            dataRow.createCell(7).setCellValue(getString(row, "insured"));
            dataRow.createCell(8).setCellValue(getString(row, "adjustingOfficeDesc"));
        }
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        wb.close();
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=claims.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
    
    private ResponseEntity<InputStreamResource> exportAsCsv(List<Map<String,Object>> items) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(out))) {
            // Header
            pw.println("Claim Number,Claimant ID,Claimant Name,SSN,Status,Incident Date,Examiner,Insured,Adjusting Office");
            
            // Data rows
            for (Map<String,Object> row : items) {
                pw.printf("\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                        getString(row, "claimNumber"),
                        getString(row, "claimantId"),
                        getString(row, "claimantName"),
                        getString(row, "ssn"),
                        getString(row, "status"),
                        getString(row, "incidentDateStr"),
                        getString(row, "examiner"),
                        getString(row, "insured"),
                        getString(row, "adjustingOfficeDesc"));
            }
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=claims.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(in));
    }
    
    private ResponseEntity<InputStreamResource> exportAsJson(Map<String,Object> payload) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(out))) {
            // Simple JSON serialization (in production, use Jackson ObjectMapper)
            pw.print("{");
            pw.printf("\"iTotalRecords\": %s,", payload.get("iTotalRecords"));
            pw.printf("\"iTotalDisplayRecords\": %s,", payload.get("iTotalDisplayRecords"));
            pw.print("\"data\": [");
            
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> items = (List<Map<String,Object>>) payload.get("data");
            for (int i = 0; i < items.size(); i++) {
                if (i > 0) pw.print(",");
                pw.print("{");
                Map<String,Object> item = items.get(i);
                boolean first = true;
                for (Map.Entry<String,Object> entry : item.entrySet()) {
                    if (!first) pw.print(",");
                    pw.printf("\"%s\": \"%s\"", entry.getKey(), getString(item, entry.getKey()));
                    first = false;
                }
                pw.print("}");
            }
            
            pw.print("]}");
        }
        
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=claims.json")
                .contentType(MediaType.parseMediaType("application/json"))
                .body(new InputStreamResource(in));
    }
    
    private String getString(Map<String,Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : "";
    }
}
