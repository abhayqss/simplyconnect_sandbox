package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.lab.LabIcd10Code;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabIcd10CodeDao extends JpaRepository<LabIcd10Code, Long> {
}
