package com.scnsoft.eldermark.dao.healthdata;

import com.scnsoft.eldermark.entity.FamilyHistoryObservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FamilyHistoryObservationDao extends JpaRepository<FamilyHistoryObservation, Long> {
}
