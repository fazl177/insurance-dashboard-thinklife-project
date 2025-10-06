package com.thinkingcode.thinklife.repository;

import com.thinkingcode.thinklife.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    // Custom query methods can be added here if needed
}
