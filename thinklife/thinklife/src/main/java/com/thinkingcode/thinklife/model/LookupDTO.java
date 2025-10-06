package com.thinkingcode.thinklife.model;

public class LookupDTO {
    private String code;
    private String description;

    public LookupDTO(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}