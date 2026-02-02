package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.serviceplan.ProgramSubTypeToServiceTreatmentApproach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProgramSubTypeToServiceTreatmentApproachDao extends JpaRepository<ProgramSubTypeToServiceTreatmentApproach, Long> {

    List<ProgramSubTypeToServiceTreatmentApproach> findAllByProgramSubTypeId(Long programSubTypeId);
}
