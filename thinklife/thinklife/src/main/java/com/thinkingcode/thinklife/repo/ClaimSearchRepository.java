package com.thinkingcode.thinklife.repo;

import com.thinkingcode.thinklife.dto.ClaimFilter;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.sql.ResultSet;

@Repository
public class ClaimSearchRepository {
    private final NamedParameterJdbcTemplate jdbc;
    public ClaimSearchRepository(NamedParameterJdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Map<String,Object> search(ClaimFilter f) {
        String base =
                "SELECT c.claim_number, c.claim_id, ao.adjusting_office_desc AS adjusting_office," +
                " COALESCE(ct.claimant_name, CONCAT_WS(', ', ct.last_name, ct.first_name)) AS claimant_name, " +
                " ct.ssn, " +
                " CASE ct.claimant_status_code WHEN 1 THEN 'Open' WHEN 2 THEN 'Pending' WHEN 3 THEN 'Closed' ELSE 'Unknown' END AS status, " +
                " NULL AS status_flag, " +
                " TO_CHAR(c.incident_date, 'YYYY-MM-DD') as incident_date_str, cov.coverage_type_desc AS insurance_type, " +
                " COALESCE(c.manual_insured_name, '') AS insured, c.policy_number " +
                " FROM claim c " +
                " LEFT JOIN adjusting_office ao ON ao.adjusting_office_code = c.adjusting_office_code" +
                " LEFT JOIN claimant ct ON ct.claim_id = c.claim_id" +
                " LEFT JOIN coverage_type cov ON cov.coverage_type_id = c.insurance_type WHERE 1=1";

        Map<String,Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder();

        if (f.claimNumber != null && !f.claimNumber.isBlank()) {
            String op = f.claimNumberOp == null ? "contains" : f.claimNumberOp;
            String clause = switch (op) {
                case "equals" -> " = :claimNumber";
                case "startsWith" -> " ILIKE :claimNumber";
                case "endsWith" -> " ILIKE :claimNumber";
                default -> " ILIKE :claimNumber";
            };
            where.append(" AND c.claim_number" + clause);
            String value = f.claimNumber;
            if ("equals".equals(op)) {
                params.put("claimNumber", value);
            } else if ("startsWith".equals(op)) {
                params.put("claimNumber", value + "%");
            } else if ("endsWith".equals(op)) {
                params.put("claimNumber", "%" + value);
            } else {
                params.put("claimNumber", "%" + value + "%");
            }
        }
        if (f.claimantFirstName != null && !f.claimantFirstName.isBlank()) {
            where.append(" AND ct.first_name ILIKE :claimantFirstName");
            params.put("claimantFirstName","%"+f.claimantFirstName+"%");
        }
        if (f.claimantLastName != null && !f.claimantLastName.isBlank()) {
            where.append(" AND ct.last_name ILIKE :claimantLastName");
            params.put("claimantLastName","%"+f.claimantLastName+"%");
        }
        if (f.status != null && f.status.length > 0) {
            // Convert string status values to their corresponding numeric codes
            List<Long> statusCodes = Arrays.stream(f.status)
                .map(status -> {
                    switch (status.toLowerCase()) {
                        case "open": return 1L;
                        case "pending": return 2L;
                        case "closed": return 3L;
                        case "reopen": return 4L; // Assuming 4 is the code for Reopen
                        default: return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());
                
            if (!statusCodes.isEmpty()) {
                where.append(" AND ct.claimant_status_code IN (:status)");
                params.put("status", statusCodes);
            }
        }
        if (f.brokerName != null && !f.brokerName.isBlank()) {
            where.append(" AND br.broker_name ILIKE :brokerName");
            params.put("brokerName","%"+f.brokerName+"%");
        }
        if (f.fromIncidentDate != null && !f.fromIncidentDate.isBlank()) {
            where.append(" AND c.incident_date >= :fromIncidentDate");
            params.put("fromIncidentDate", f.fromIncidentDate);
        }
        if (f.toIncidentDate != null && !f.toIncidentDate.isBlank()) {
            where.append(" AND c.incident_date <= :toIncidentDate");
            params.put("toIncidentDate", f.toIncidentDate);
        }
        if (f.ssn != null && !f.ssn.isBlank()) {
            where.append(" AND ct.ssn ILIKE :ssn");
            params.put("ssn","%"+f.ssn+"%");
        }

        // Sorting
        String sortCol = "c.incident_date";
        String sortDir = "DESC";
        if (f.sort != null && f.sort.contains(":")) {
            String[] s = f.sort.split(":");
            sortCol = s[0].equals("incidentDate") ? "c.incident_date" : "c.incident_date";
            sortDir = "asc".equalsIgnoreCase(s[1]) ? "ASC" : "DESC";
        }

        // Paging
        int page = (f.page == null || f.page < 1) ? 1 : f.page;
        int pageSize = (f.pageSize == null || f.pageSize < 1) ? 20 : f.pageSize;
        int offset = (page-1)*pageSize;

        String countSql = "SELECT COUNT(*) FROM claim c LEFT JOIN claimant ct ON ct.claim_id = c.claim_id" + where.toString();
        long total = jdbc.queryForObject(countSql, params, Long.class);

        String dataSql = base + where.toString() + " ORDER BY " + sortCol + " " + sortDir + " LIMIT :pageSize OFFSET :offset";
        params.put("pageSize", pageSize);
        params.put("offset", offset);

        List<Map<String,Object>> rows = jdbc.query(dataSql, params, (ResultSet rs, int rowNum) -> {
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("claimNumber", rs.getString("claim_number"));
            row.put("claimId", rs.getLong("claim_id"));
            row.put("adjustingOffice", rs.getString("adjusting_office"));
            row.put("claimantName", rs.getString("claimant_name"));
            row.put("ssn", rs.getString("ssn"));
            row.put("status", rs.getString("status"));
            row.put("statusFlag", rs.getString("status_flag"));
            row.put("incidentDateStr", rs.getString("incident_date_str"));
            row.put("insuranceType", rs.getString("insurance_type"));
            // broker_name column removed as it's no longer in the SELECT clause
            row.put("insured", rs.getString("insured"));
            row.put("policyNumber", rs.getString("policy_number"));
            return row;
        });
        Map<String,Object> resp = new HashMap<>();
        resp.put("items", rows);
        resp.put("total", total);
        resp.put("page", page);
        resp.put("pageSize", pageSize);
        return resp;
    }

    public List<Map<String,Object>> brokerTypeahead(String q) {
        String sql = "SELECT broker_id, broker_name as name FROM broker WHERE broker_name ILIKE :q LIMIT 10";
        return jdbc.queryForList(sql, Map.of("q","%"+q+"%"));
    }

    public List<Map<String,Object>> employerTypeahead(String q) {
        String sql = "SELECT employer_id AS insured_id, employer_name AS insured FROM employer WHERE employer_name ILIKE :q LIMIT 10";
        return jdbc.queryForList(sql, Map.of("q","%"+q+"%"));
    }

    public Map<String,Object> fetchMyQueue(ClaimFilter f) {
        String base =
                "SELECT c.claim_id, TO_CHAR(c.incident_date, 'MM-DD-YYYY HH12:MI:SS AM') AS incidentDateStr, " +
                " c.policy_number, c.state_code AS stateCode, ao.adjusting_office_desc AS adjustingOfficeDesc, " +
                " c.coverage_type_id AS insuranceType, ct.claimant_id, ct.claimant_name, ct.first_name AS claimantFirstName, ct.last_name AS claimantLastName, " +
                " c.claim_number, c.incident_date, ct.ssn, ct.claimant_status_code AS statusCode, " +
                " cs.claimant_status_desc AS status, " +
                " CASE WHEN ct.claimant_status_code = 4 THEN 'R ' ELSE ' ' END AS statusFlag, " +
                " c.manual_insured_name AS insured, c.add_date, cov.coverage_type_desc AS insuranceTypeDesc, c.state_code, " +
                " e.examiner_desc AS examiner, e.examiner_code AS examinerId, ct.suffix AS claimantSuffix, " +
                " pig.name AS programDesc, pig.pig_id AS programId, ct.claimant_type_code " +
                " FROM claim c " +
                " LEFT JOIN claimant ct ON ct.claim_id = c.claim_id " +
                " LEFT JOIN claimant_status cs ON cs.claimant_status_code = ct.claimant_status_code " +
                " LEFT JOIN adjusting_office ao ON ao.adjusting_office_code = c.adjusting_office_code " +
                " LEFT JOIN coverage_type cov ON cov.coverage_type_id = c.coverage_type_id " +
                " LEFT JOIN examiner e ON e.examiner_code = c.examiner_code " +
                " LEFT JOIN program_insurance_group pig ON pig.pig_id = c.pig_id " +
                " WHERE c.active = '1' AND ct.active = '1'";

        Map<String,Object> params = new HashMap<>();
        StringBuilder where = new StringBuilder();
        
        // Enhanced filtering based on ClaimFilter
        if (f.q != null && !f.q.isBlank()) {
            where.append(" AND (c.claim_number ILIKE :quickSearch OR ct.claimant_name ILIKE :quickSearch OR c.manual_insured_name ILIKE :quickSearch OR e.examiner_desc ILIKE :quickSearch)");
            params.put("quickSearch", "%" + f.q + "%");
        }
        
        if (f.claimNumber != null && !f.claimNumber.isBlank()) {
            where.append(" AND c.claim_number ILIKE :claimNumber");
            params.put("claimNumber","%"+f.claimNumber+"%");
        }
        
        if (f.claimantName != null && !f.claimantName.isBlank()) {
            String op = f.claimantNameOp == null ? "contains" : f.claimantNameOp;
            String clause = switch (op) {
                case "equals" -> " = :claimantName";
                case "startsWith" -> " ILIKE :claimantName";
                case "endsWith" -> " ILIKE :claimantName";
                default -> " ILIKE :claimantName";
            };
            where.append(" AND ct.claimant_name" + clause);
            String value = f.claimantName;
            if ("equals".equals(op)) {
                params.put("claimantName", value);
            } else if ("startsWith".equals(op)) {
                params.put("claimantName", value + "%");
            } else if ("endsWith".equals(op)) {
                params.put("claimantName", "%" + value);
            } else {
                params.put("claimantName", "%" + value + "%");
            }
        }
        
        if (f.ssn != null && !f.ssn.isBlank()) {
            String op = f.ssnOp == null ? "contains" : f.ssnOp;
            String clause = switch (op) {
                case "equals" -> " = :ssn";
                case "startsWith" -> " ILIKE :ssn";
                case "endsWith" -> " ILIKE :ssn";
                default -> " ILIKE :ssn";
            };
            where.append(" AND ct.ssn" + clause);
            String value = f.ssn;
            if ("equals".equals(op)) {
                params.put("ssn", value);
            } else if ("startsWith".equals(op)) {
                params.put("ssn", value + "%");
            } else if ("endsWith".equals(op)) {
                params.put("ssn", "%" + value);
            } else {
                params.put("ssn", "%" + value + "%");
            }
        }
        
        if (f.status != null && f.status.length > 0) {
            where.append(" AND cs.claimant_status_desc IN (:status)");
            params.put("status", Arrays.asList(f.status));
        }
        
        if (f.examiner != null && !f.examiner.isBlank()) {
            String op = f.examinerOp == null ? "contains" : f.examinerOp;
            String clause = switch (op) {
                case "equals" -> " = :examiner";
                case "startsWith" -> " ILIKE :examiner";
                case "endsWith" -> " ILIKE :examiner";
                default -> " ILIKE :examiner";
            };
            where.append(" AND e.examiner_desc" + clause);
            String value = f.examiner;
            if ("equals".equals(op)) params.put("examiner", value);
            else if ("startsWith".equals(op)) params.put("examiner", value+"%");
            else if ("endsWith".equals(op)) params.put("examiner", "%"+value);
            else params.put("examiner", "%"+value+"%");
        }
        
        if (f.policyNumber != null && !f.policyNumber.isBlank()) {
            String op = f.policyNumberOp == null ? "contains" : f.policyNumberOp;
            String clause = switch (op) {
                case "equals" -> " = :policyNumber";
                case "startsWith" -> " ILIKE :policyNumber";
                case "endsWith" -> " ILIKE :policyNumber";
                default -> " ILIKE :policyNumber";
            };
            where.append(" AND c.policy_number" + clause);
            String value = f.policyNumber;
            if ("equals".equals(op)) params.put("policyNumber", value);
            else if ("startsWith".equals(op)) params.put("policyNumber", value+"%");
            else if ("endsWith".equals(op)) params.put("policyNumber", "%"+value);
            else params.put("policyNumber", "%"+value+"%");
        }
        
        // Incident date operators
        String dateOp = (f.incidentDateOp == null || f.incidentDateOp.isBlank()) ? "between" : f.incidentDateOp;
        if ("on".equalsIgnoreCase(dateOp) && f.dolStart != null && !f.dolStart.isBlank()) {
            where.append(" AND DATE(c.incident_date) = DATE(:dolStart)");
            params.put("dolStart", f.dolStart);
        } else if ("before".equalsIgnoreCase(dateOp) && f.dolStart != null && !f.dolStart.isBlank()) {
            where.append(" AND c.incident_date < :dolStart");
            params.put("dolStart", f.dolStart);
        } else if ("after".equalsIgnoreCase(dateOp) && f.dolEnd != null && !f.dolEnd.isBlank()) {
            where.append(" AND c.incident_date > :dolEnd");
            params.put("dolEnd", f.dolEnd);
        } else {
            if (f.dolStart != null && !f.dolStart.isBlank()) {
                where.append(" AND c.incident_date >= :dolStart");
                params.put("dolStart", f.dolStart);
            }
            if (f.dolEnd != null && !f.dolEnd.isBlank()) {
                where.append(" AND c.incident_date <= :dolEnd");
                params.put("dolEnd", f.dolEnd);
            }
        }
        
        if (f.lossState != null && !f.lossState.isBlank()) {
            where.append(" AND c.state_code = :lossState");
            params.put("lossState", f.lossState);
        }
        
        if (f.program != null && !f.program.isBlank()) {
            String op = f.programOp == null ? "contains" : f.programOp;
            String clause = switch (op) {
                case "equals" -> " = :program";
                case "startsWith" -> " ILIKE :program";
                case "endsWith" -> " ILIKE :program";
                default -> " ILIKE :program";
            };
            where.append(" AND pig.name" + clause);
            String value = f.program;
            if ("equals".equals(op)) params.put("program", value);
            else if ("startsWith".equals(op)) params.put("program", value+"%");
            else if ("endsWith".equals(op)) params.put("program", "%"+value);
            else params.put("program", "%"+value+"%");
        }

        // Sorting
        String sortCol = "c.incident_date";
        String sortDir = "DESC";
        if (f.sort != null && f.sort.contains(":")) {
            String[] s = f.sort.split(":");
            switch (s[0]) {
                case "incidentDate":
                    sortCol = "c.incident_date";
                    break;
                case "claimNumber":
                    sortCol = "c.claim_number";
                    break;
                case "claimantName":
                    sortCol = "ct.claimant_name";
                    break;
                case "status":
                    sortCol = "cs.claimant_status_desc";
                    break;
                default:
                    sortCol = "c.incident_date";
            }
            sortDir = "asc".equalsIgnoreCase(s[1]) ? "ASC" : "DESC";
        }

        int page = (f.page == null || f.page < 1) ? 1 : f.page;
        int pageSize = (f.pageSize == null || f.pageSize < 1) ? 20 : Math.min(f.pageSize, 100);
        int offset = (page-1)*pageSize;

        String countSql = "SELECT COUNT(*) FROM claim c LEFT JOIN claimant ct ON ct.claim_id = c.claim_id LEFT JOIN claimant_status cs ON cs.claimant_status_code = ct.claimant_status_code LEFT JOIN adjusting_office ao ON ao.adjusting_office_code = c.adjusting_office_code LEFT JOIN coverage_type cov ON cov.coverage_type_id = c.coverage_type_id LEFT JOIN examiner e ON e.examiner_code = c.examiner_code LEFT JOIN program_insurance_group pig ON pig.pig_id = c.pig_id WHERE c.active = '1' AND ct.active = '1'" + where.toString();
        long total = jdbc.queryForObject(countSql, params, Long.class);

        String dataSql = base + where.toString() + " ORDER BY " + sortCol + " " + sortDir + " LIMIT :pageSize OFFSET :offset";
        params.put("pageSize", pageSize);
        params.put("offset", offset);

        List<Map<String,Object>> data = jdbc.query(dataSql, params, (ResultSet rs, int rowNum) -> {
            Map<String,Object> row = new LinkedHashMap<>();
            row.put("claimId", rs.getLong("claim_id"));
            row.put("incidentDateStr", rs.getString("incidentDateStr"));
            row.put("programId", rs.getString("programId"));
            row.put("coverageTypeId", rs.getInt("insuranceType"));
            row.put("vehicleIdentificationNumber", null);
            row.put("vehicleYear", null);
            row.put("vehicleMake", null);
            row.put("vehicleModelCode", null);
            row.put("stateCode", rs.getString("state_code"));
            row.put("coverageId", null);
            row.put("programDescription", null);
            row.put("programDesc", rs.getString("programDesc"));
            row.put("adjustingOfficeCode", null);
            row.put("adjustingOfficeDesc", rs.getString("adjustingOfficeDesc"));
            row.put("closedDate", null);
            row.put("closedDateStr", null);
            row.put("deathDate", null);
            row.put("deathDateStr", null);
            row.put("jurisdictionDesc", null);
            row.put("insuranceTypeDesc", rs.getString("insuranceTypeDesc"));
            row.put("claimantSuffix", rs.getString("claimantSuffix"));
            row.put("statusFlag", rs.getString("statusFlag"));
            row.put("idmFlag", "");
            row.put("billReviewVendorId", null);
            row.put("billReviewVendorName", null);
            row.put("addDate", rs.getTimestamp("add_date"));
            row.put("addDateStr", null);
            row.put("insuranceType", rs.getInt("insuranceType"));
            row.put("clientCode", 1);
            row.put("claimantId", rs.getLong("claimant_id"));
            row.put("claimantName", rs.getString("claimant_name"));
            row.put("claimantFirstName", rs.getString("claimantFirstName"));
            row.put("claimantLastName", rs.getString("claimantLastName"));
            row.put("claimNumber", rs.getString("claim_number"));
            row.put("incidentDate", rs.getTimestamp("incident_date"));
            row.put("fromincidentDate", null);
            row.put("toincidentDate", null);
            row.put("ssn", rs.getString("ssn"));
            row.put("statusCode", rs.getInt("statusCode"));
            row.put("status", rs.getString("status"));
            row.put("lossStateCode", rs.getString("state_code"));
            row.put("lossState", null);
            row.put("affiliateClaimNumber", null);
            row.put("examinerId", rs.getString("examinerId"));
            row.put("examiner", rs.getString("examiner"));
            row.put("insuredId", null);
            row.put("supervisorId", null);
            row.put("supervisor", null);
            row.put("insured", rs.getString("insured"));
            row.put("insurerNumber", null);
            row.put("insurer", null);
            row.put("claimantTypeCode", rs.getLong("claimant_type_code"));
            row.put("claimantTypeDesc", null);
            row.put("org1Code", null);
            row.put("org1", null);
            row.put("org2Code", null);
            row.put("org2", null);
            row.put("claimantNumber", rs.getInt("claimant_id"));
            row.put("accepted", "No");
            row.put("denied", "No");
            row.put("policyNumber", rs.getString("policy_number"));
            row.put("org3Code", null);
            row.put("org3", null);
            row.put("org4", null);
            row.put("org4Code", null);
            row.put("underwriter", null);
            row.put("employeeNumber", null);
            row.put("entityName", null);
            row.put("claimantOrEntityName", rs.getString("claimant_name"));
            row.put("claimantFirstOrEntityName", null);
            return row;
        });

        return Map.of(
                "iTotalRecords", total,
                "iTotalDisplayRecords", total,
                "data", data
        );
    }

    public void streamCsv(ClaimFilter f, java.io.Writer writer) throws java.io.IOException {
        // Reuse the same SQL used by fetchMyQueue without LIMIT/OFFSET for full export respecting filters
        StringBuilder where = new StringBuilder(" WHERE c.active = '1' AND ct.active = '1'");
        Map<String,Object> params = new HashMap<>();

        if (f.q != null && !f.q.isBlank()) {
            where.append(" AND (c.claim_number ILIKE :quickSearch OR ct.claimant_name ILIKE :quickSearch OR c.manual_insured_name ILIKE :quickSearch OR e.examiner_desc ILIKE :quickSearch)");
            params.put("quickSearch", "%" + f.q + "%");
        }
        // Simplified reuse: apply a subset of filters identical to fetchMyQueue for export
        if (f.claimNumber != null && !f.claimNumber.isBlank()) {
            String op = f.claimNumberOp == null ? "contains" : f.claimNumberOp;
            String clause = switch (op) { case "equals" -> " = :claimNumber"; case "startsWith" -> " ILIKE :claimNumber"; case "endsWith" -> " ILIKE :claimNumber"; default -> " ILIKE :claimNumber"; };
            where.append(" AND c.claim_number" + clause);
            String value = f.claimNumber;
            if ("equals".equals(op)) params.put("claimNumber", value);
            else if ("startsWith".equals(op)) params.put("claimNumber", value+"%");
            else if ("endsWith".equals(op)) params.put("claimNumber", "%"+value);
            else params.put("claimNumber", "%"+value+"%");
        }
        if (f.claimantName != null && !f.claimantName.isBlank()) {
            String op = f.claimantNameOp == null ? "contains" : f.claimantNameOp;
            String clause = switch (op) { case "equals" -> " = :claimantName"; case "startsWith" -> " ILIKE :claimantName"; case "endsWith" -> " ILIKE :claimantName"; default -> " ILIKE :claimantName"; };
            where.append(" AND ct.claimant_name" + clause);
            String value = f.claimantName;
            if ("equals".equals(op)) params.put("claimantName", value);
            else if ("startsWith".equals(op)) params.put("claimantName", value+"%");
            else if ("endsWith".equals(op)) params.put("claimantName", "%"+value);
            else params.put("claimantName", "%"+value+"%");
        }
        if (f.ssn != null && !f.ssn.isBlank()) {
            String op = f.ssnOp == null ? "contains" : f.ssnOp;
            String clause = switch (op) { case "equals" -> " = :ssn"; case "startsWith" -> " ILIKE :ssn"; case "endsWith" -> " ILIKE :ssn"; default -> " ILIKE :ssn"; };
            where.append(" AND ct.ssn" + clause);
            String value = f.ssn;
            if ("equals".equals(op)) params.put("ssn", value);
            else if ("startsWith".equals(op)) params.put("ssn", value+"%");
            else if ("endsWith".equals(op)) params.put("ssn", "%"+value);
            else params.put("ssn", "%"+value+"%");
        }
        String dateOp = (f.incidentDateOp == null || f.incidentDateOp.isBlank()) ? "between" : f.incidentDateOp;
        if ("on".equalsIgnoreCase(dateOp) && f.dolStart != null && !f.dolStart.isBlank()) {
            where.append(" AND DATE(c.incident_date) = DATE(:dolStart)");
            params.put("dolStart", f.dolStart);
        } else if ("before".equalsIgnoreCase(dateOp) && f.dolStart != null && !f.dolStart.isBlank()) {
            where.append(" AND c.incident_date < :dolStart");
            params.put("dolStart", f.dolStart);
        } else if ("after".equalsIgnoreCase(dateOp) && f.dolEnd != null && !f.dolEnd.isBlank()) {
            where.append(" AND c.incident_date > :dolEnd");
            params.put("dolEnd", f.dolEnd);
        } else {
            if (f.dolStart != null && !f.dolStart.isBlank()) { where.append(" AND c.incident_date >= :dolStart"); params.put("dolStart", f.dolStart); }
            if (f.dolEnd != null && !f.dolEnd.isBlank()) { where.append(" AND c.incident_date <= :dolEnd"); params.put("dolEnd", f.dolEnd); }
        }

        String sql = "SELECT c.claim_number, ct.claimant_id, ct.claimant_name, ct.ssn, cs.claimant_status_desc AS status, TO_CHAR(c.incident_date, 'YYYY-MM-DD') AS incident_date_str, COALESCE(c.manual_insured_name, '') AS insured, e.examiner_desc AS examiner FROM claim c LEFT JOIN claimant ct ON ct.claim_id = c.claim_id LEFT JOIN claimant_status cs ON cs.claimant_status_code = ct.claimant_status_code LEFT JOIN examiner e ON e.examiner_code = c.examiner_code" + where.toString() + " ORDER BY c.incident_date DESC";

        // Write CSV header
        writer.write("Claim Number,Claimant ID,Claimant Name,SSN,Status,Incident Date,Insured,Examiner\n");
        writer.flush();

        jdbc.query(sql, params, (ResultSet rs) -> {
            try {
                StringBuilder line = new StringBuilder();
                line.append(escapeCsv(rs.getString("claim_number"))).append(',')
                    .append(escapeCsv(rs.getString("claimant_id"))).append(',')
                    .append(escapeCsv(rs.getString("claimant_name"))).append(',')
                    .append(escapeCsv(rs.getString("ssn"))).append(',')
                    .append(escapeCsv(rs.getString("status"))).append(',')
                    .append(escapeCsv(rs.getString("incident_date_str"))).append(',')
                    .append(escapeCsv(rs.getString("insured"))).append(',')
                    .append(escapeCsv(rs.getString("examiner")))
                    .append("\n");
                writer.write(line.toString());
            } catch (java.io.IOException ioe) {
                throw new RuntimeException(ioe);
            }
        });
    }

    private String escapeCsv(String v) {
        if (v == null) return "";
        boolean needsQuotes = v.contains(",") || v.contains("\n") || v.contains("\"");
        String escaped = v.replace("\"", "\"\"");
        return needsQuotes ? ("\"" + escaped + "\"") : escaped;
    }
    
    // Additional typeahead methods for all required endpoints
    
    public List<Map<String,Object>> programTypeahead(String q) {
        String sql = "SELECT pig_id AS id, name FROM program_insurance_group WHERE name ILIKE :q LIMIT 10";
        return jdbc.queryForList(sql, Map.of("q","%"+q+"%"));
    }
    
    public List<Map<String,Object>> policyTypeahead(String q) {
        String sql = "SELECT master_policy_number AS id, master_policy_number AS name FROM master_policy WHERE master_policy_number ILIKE :q LIMIT 10";
        return jdbc.queryForList(sql, Map.of("q","%"+q+"%"));
    }
    
    public List<Map<String,Object>> examinerTypeahead(String q) {
        String sql = "SELECT examiner_code AS id, examiner_desc AS name FROM examiner WHERE examiner_desc ILIKE :q LIMIT 10";
        return jdbc.queryForList(sql, Map.of("q","%"+q+"%"));
    }
    
    public List<Map<String,Object>> underwriterTypeahead(String q) {
        String sql = "SELECT underwriter_id AS id, underwriter_name AS name FROM underwriter WHERE underwriter_name ILIKE :q LIMIT 10";
        return jdbc.queryForList(sql, Map.of("q","%"+q+"%"));
    }
    
    // Dropdown/Lookup methods
    
    public List<Map<String,Object>> getInsuranceTypes() {
        String sql = "SELECT coverage_type_id AS insuranceType, coverage_type_desc AS insuranceTypeDesc FROM coverage_type WHERE active = '1' ORDER BY coverage_type_desc";
        return jdbc.queryForList(sql, Map.of());
    }
    
    public List<Map<String,Object>> getStatuses() {
        String sql = "SELECT claimant_status_code AS code, claimant_status_desc AS desc FROM claimant_status WHERE active = '1' ORDER BY claimant_status_desc";
        return jdbc.queryForList(sql, Map.of());
    }
    
    public List<Map<String,Object>> getClaimantTypes() {
        String sql = "SELECT claimant_type_code AS code, claimant_type_desc AS desc FROM claimant_type WHERE active = '1' ORDER BY claimant_type_desc";
        return jdbc.queryForList(sql, Map.of());
    }
    
    public List<Map<String,Object>> getOrganizations() {
        String sql = "SELECT adjusting_office_code AS id, adjusting_office_desc AS name FROM adjusting_office ORDER BY adjusting_office_desc";
        return jdbc.queryForList(sql, Map.of());
    }

    public List<Map<String,Object>> getBrokers() {
        String sql = "SELECT broker_id AS code, broker_name AS description FROM broker WHERE active = '1' ORDER BY broker_name";
        return jdbc.queryForList(sql, Map.of());
    }

    public List<Map<String,Object>> getAdjustingOffices() {
        String sql = "SELECT adjusting_office_code AS code, adjusting_office_desc AS description FROM adjusting_office ORDER BY adjusting_office_desc";
        return jdbc.queryForList(sql, Map.of());
    }
}
