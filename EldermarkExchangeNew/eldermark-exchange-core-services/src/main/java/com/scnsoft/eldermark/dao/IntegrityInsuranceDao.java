package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.lab.IntegrityInsurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IntegrityInsuranceDao extends JpaRepository<IntegrityInsurance, Long> {

    Optional<IntegrityInsurance> findFirstByName(String name);
}
