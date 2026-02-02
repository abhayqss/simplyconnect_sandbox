package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.lab.LabResearchOrderORM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LabResearchOrderORMDao extends JpaRepository<LabResearchOrderORM, Long> {
}
