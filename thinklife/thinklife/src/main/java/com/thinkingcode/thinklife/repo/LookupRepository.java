package com.thinkingcode.thinklife.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class LookupRepository {

    private final JdbcTemplate jdbcTemplate;

    public LookupRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // For the /dropdown/insurance-types endpoint
    public List<Map<String, Object>> insuranceTypes() {
        String sql = "SELECT coverage_type_id AS \"insuranceType\", coverage_type_desc AS \"insuranceTypeDesc\" FROM coverage_type ORDER BY coverage_type_desc";
        return jdbcTemplate.queryForList(sql);
    }

    // For the /dropdown/statuses endpoint
    public List<Map<String, Object>> statuses() {
        String sql = "SELECT status_code AS \"statusCode\", status_description AS \"statusDesc\" FROM claim_status ORDER BY status_description";
        return jdbcTemplate.queryForList(sql);
    }

    // For the /typeahead/examiners endpoint
    public List<Map<String, Object>> examiners(String q) {
        String sql = "SELECT examiner_code AS \"examinerId\", examiner_name AS \"examinerName\" FROM examiner WHERE examiner_name ILIKE ?";
        return jdbcTemplate.queryForList(sql, q);
    }

    // For the /dropdown/organizations endpoint
    public List<Map<String, Object>> organizations() {
        String sql = "SELECT org_code AS \"orgCode\", org_name AS \"orgName\" FROM organization ORDER BY org_name";
        return jdbcTemplate.queryForList(sql);
    }

    // For the /dropdown/brokers endpoint
    public List<Map<String, Object>> brokers() {
        String sql = "SELECT broker_id AS \"brokerId\", broker_name AS \"brokerName\" FROM broker ORDER BY broker_name";
        return jdbcTemplate.queryForList(sql);
    }

    // For the /dropdown/claimant-types endpoint
    public List<Map<String, Object>> claimantTypes() {
        String sql = "SELECT code AS \"claimantTypeCode\", description AS \"claimantTypeDesc\" FROM claimant_type ORDER BY description";
        return jdbcTemplate.queryForList(sql);
    }
}