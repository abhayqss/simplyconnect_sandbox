package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.lab.LabIcd10Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabIcd10GroupDao extends JpaRepository<LabIcd10Group, Long> {
}
