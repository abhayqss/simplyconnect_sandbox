package com.scnsoft.eldermark.consana.sync.server.dao;

import com.scnsoft.eldermark.consana.sync.server.model.entity.AllergyObservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AllergyObservationDao extends JpaRepository<AllergyObservation, Long> {

    List<AllergyObservation> getAllByConsanaIdIsNotNullAndAllergy_ResidentId(Long residentId);
}
