package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Medication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationDao extends JpaRepository<Medication, Long> {

    List<Medication> getAllByConsanaIdIsNotNullAndResidentId(Long residentId);
}
