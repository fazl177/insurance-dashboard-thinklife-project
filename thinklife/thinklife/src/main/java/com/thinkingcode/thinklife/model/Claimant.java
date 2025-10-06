package com.thinkingcode.thinklife.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "claimant")
public class Claimant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "claimant_id")
    private Long claimantId;

    @Column(name = "claim_id")
    private Long claimId;

    @Column(name = "claimant_number")
    private Integer claimantNumber;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "claimant_name")
    private String claimantName;

    @Column(name = "ssn")
    private String ssn;

    @Column(name = "claimant_status_code")
    private Long claimantStatusCode;

    @Column(name = "claimant_type_code")
    private Long claimantTypeCode;

    @Column(name = "suffix")
    private String suffix;

    @Column(name = "active")
    private String active;

    @Column(name = "add_date")
    private LocalDateTime addDate;

    // Getters and Setters
    public Long getClaimantId() {
        return claimantId;
    }

    public void setClaimantId(Long claimantId) {
        this.claimantId = claimantId;
    }

    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }

    public Integer getClaimantNumber() {
        return claimantNumber;
    }

    public void setClaimantNumber(Integer claimantNumber) {
        this.claimantNumber = claimantNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getClaimantName() {
        return claimantName;
    }

    public void setClaimantName(String claimantName) {
        this.claimantName = claimantName;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public Long getClaimantStatusCode() {
        return claimantStatusCode;
    }

    public void setClaimantStatusCode(Long claimantStatusCode) {
        this.claimantStatusCode = claimantStatusCode;
    }

    public Long getClaimantTypeCode() {
        return claimantTypeCode;
    }

    public void setClaimantTypeCode(Long claimantTypeCode) {
        this.claimantTypeCode = claimantTypeCode;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public LocalDateTime getAddDate() {
        return addDate;
    }

    public void setAddDate(LocalDateTime addDate) {
        this.addDate = addDate;
    }
}
