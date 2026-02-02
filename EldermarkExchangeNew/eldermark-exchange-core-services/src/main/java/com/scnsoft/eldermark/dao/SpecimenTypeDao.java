package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.lab.SpecimenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecimenTypeDao extends JpaRepository<SpecimenType, Long> {
}
