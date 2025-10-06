package com.thinkingcode.thinklife.dto;

import java.time.LocalDate;

/**
 * ClaimFilter DTO matching the project requirements API contract
 * Supports all search filters defined in the Claims Search UI specification
 */
public class ClaimFilter {
    // Core search fields from requirements
    public String q; // Quick search (full-text across claim#, claimant)
    public String[] status; // Open/Reopen/Closed/Pending
    public String dolStart; // Incident date start (ISO date)
    public String dolEnd; // Incident date end (ISO date)
    public String reportedStart; // Reported date start
    public String reportedEnd; // Reported date end
    public Double minAmount; // Minimum claim amount
    public Double maxAmount; // Maximum claim amount
    public String[] orgIds; // Organization IDs
    public String[] adjusterIds; // Adjuster/Examiner IDs
    public String[] tags; // Tags
    
    // Specific search fields
    public String claimantName;
    public String ssn;
    public String claimNumber;
    public String lossState;
    public String program;
    public String[] insuranceType;
    public String examiner;
    public String policyNumber;
    public String organization1;
    public String organization2;
    public String organization3;
    public String organization4;
    
    // Operator parameters for server-side filtering
    public String claimNumberOp;     // contains|equals|startsWith|endsWith
    public String claimantNameOp;    // contains|equals|startsWith|endsWith
    public String ssnOp;             // contains|equals|startsWith|endsWith
    public String incidentDateOp;    // between|on|before|after
    public String policyNumberOp;    // contains|equals|startsWith|endsWith
    public String examinerOp;        // contains|equals|startsWith|endsWith
    public String programOp;         // contains|equals|startsWith|endsWith
    
    // Additional fields to match frontend SearchCriteria interface
    public String claimantFirstName;
    public String claimantLastName;
    public String brokerName;
    public String employer;
    public String fromIncidentDate;
    public String toIncidentDate;
    public String employeeNumber;
    public String affiliateClaimNumber;
    public String jurisdictionClaimNumber;
    public String emailId;
    public String phoneNumber;
    public String dob;
    public String adjustingOffice;
    public String claimantType;
    public String org1;
    public String org2;
    
    // Pagination and sorting
    public String sort = "incidentDate:desc"; // "field:asc|desc"
    public Integer page = 1; // 1-based pagination
    public Integer pageSize = 20; // Max 100
    
    // Getters and Setters
    public String getQ() { return q; }
    public void setQ(String q) { this.q = q; }
    
    public String[] getStatus() { return status; }
    public void setStatus(String[] status) { this.status = status; }
    
    public String getDolStart() { return dolStart; }
    public void setDolStart(String dolStart) { this.dolStart = dolStart; }
    
    public String getDolEnd() { return dolEnd; }
    public void setDolEnd(String dolEnd) { this.dolEnd = dolEnd; }
    
    public String getReportedStart() { return reportedStart; }
    public void setReportedStart(String reportedStart) { this.reportedStart = reportedStart; }
    
    public String getReportedEnd() { return reportedEnd; }
    public void setReportedEnd(String reportedEnd) { this.reportedEnd = reportedEnd; }
    
    public Double getMinAmount() { return minAmount; }
    public void setMinAmount(Double minAmount) { this.minAmount = minAmount; }
    
    public Double getMaxAmount() { return maxAmount; }
    public void setMaxAmount(Double maxAmount) { this.maxAmount = maxAmount; }
    
    public String[] getOrgIds() { return orgIds; }
    public void setOrgIds(String[] orgIds) { this.orgIds = orgIds; }
    
    public String[] getAdjusterIds() { return adjusterIds; }
    public void setAdjusterIds(String[] adjusterIds) { this.adjusterIds = adjusterIds; }
    
    public String[] getTags() { return tags; }
    public void setTags(String[] tags) { this.tags = tags; }
    
    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }
    
    public String getSsn() { return ssn; }
    public void setSsn(String ssn) { this.ssn = ssn; }
    
    public String getClaimNumber() { return claimNumber; }
    public void setClaimNumber(String claimNumber) { this.claimNumber = claimNumber; }
    
    public String getLossState() { return lossState; }
    public void setLossState(String lossState) { this.lossState = lossState; }
    
    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }
    
    public String[] getInsuranceType() { return insuranceType; }
    public void setInsuranceType(String[] insuranceType) { this.insuranceType = insuranceType; }
    
    public String getExaminer() { return examiner; }
    public void setExaminer(String examiner) { this.examiner = examiner; }
    
    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    
    public String getOrganization1() { return organization1; }
    public void setOrganization1(String organization1) { this.organization1 = organization1; }
    
    public String getOrganization2() { return organization2; }
    public void setOrganization2(String organization2) { this.organization2 = organization2; }
    
    public String getOrganization3() { return organization3; }
    public void setOrganization3(String organization3) { this.organization3 = organization3; }
    
    public String getOrganization4() { return organization4; }
    public void setOrganization4(String organization4) { this.organization4 = organization4; }
    
    public String getClaimNumberOp() { return claimNumberOp; }
    public void setClaimNumberOp(String claimNumberOp) { this.claimNumberOp = claimNumberOp; }
    
    public String getClaimantNameOp() { return claimantNameOp; }
    public void setClaimantNameOp(String claimantNameOp) { this.claimantNameOp = claimantNameOp; }
    
    public String getSsnOp() { return ssnOp; }
    public void setSsnOp(String ssnOp) { this.ssnOp = ssnOp; }
    
    public String getIncidentDateOp() { return incidentDateOp; }
    public void setIncidentDateOp(String incidentDateOp) { this.incidentDateOp = incidentDateOp; }
    
    public String getPolicyNumberOp() { return policyNumberOp; }
    public void setPolicyNumberOp(String policyNumberOp) { this.policyNumberOp = policyNumberOp; }
    
    public String getExaminerOp() { return examinerOp; }
    public void setExaminerOp(String examinerOp) { this.examinerOp = examinerOp; }
    
    public String getProgramOp() { return programOp; }
    public void setProgramOp(String programOp) { this.programOp = programOp; }
    
    public String getClaimantFirstName() { return claimantFirstName; }
    public void setClaimantFirstName(String claimantFirstName) { this.claimantFirstName = claimantFirstName; }
    
    public String getClaimantLastName() { return claimantLastName; }
    public void setClaimantLastName(String claimantLastName) { this.claimantLastName = claimantLastName; }
    
    public String getBrokerName() { return brokerName; }
    public void setBrokerName(String brokerName) { this.brokerName = brokerName; }
    
    public String getEmployer() { return employer; }
    public void setEmployer(String employer) { this.employer = employer; }
    
    public String getFromIncidentDate() { return fromIncidentDate; }
    public void setFromIncidentDate(String fromIncidentDate) { this.fromIncidentDate = fromIncidentDate; }
    
    public String getToIncidentDate() { return toIncidentDate; }
    public void setToIncidentDate(String toIncidentDate) { this.toIncidentDate = toIncidentDate; }
    
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
    
    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }
    
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
