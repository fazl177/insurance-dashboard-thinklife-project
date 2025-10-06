package com.thinkingcode.thinklife.service;

import com.thinkingcode.thinklife.model.LookupDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Arrays;
import java.util.List;

@Service
public class LookupService {

    public List<LookupDTO> getAdjustingOffices() {
        return Arrays.asList(
            new LookupDTO("ANC", "Anchorage"),
            new LookupDTO("420", "Chicago"),
            new LookupDTO("NYC", "New York")
        );
    }

    public List<LookupDTO> getOrganizations() {
        return Arrays.asList(
            new LookupDTO("ORG1", "Organization One"),
            new LookupDTO("ORG2", "Organization Two"),
            new LookupDTO("ORG3", "Organization Three")
        );
    }

    public List<LookupDTO> getBrokers() {
        return Arrays.asList(
            new LookupDTO("1", "Demo broker"),
            new LookupDTO("24", "Aon eSolutions Inc")
        );
    }

    public List<LookupDTO> getClaimantTypes() {
        return Arrays.asList(
            new LookupDTO("41", "Collision"),
            new LookupDTO("10", "Employee"),
            new LookupDTO("20", "Dependent")
        );
    }

    public List<LookupDTO> getStatus() {
        return Arrays.asList(
            new LookupDTO("O", "Open"),
            new LookupDTO("C", "Closed"),
            new LookupDTO("R", "Reopen"),
            new LookupDTO("P", "Pending")
        );
    }
}