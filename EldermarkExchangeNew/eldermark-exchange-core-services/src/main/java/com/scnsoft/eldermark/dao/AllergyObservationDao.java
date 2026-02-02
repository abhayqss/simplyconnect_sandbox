package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.entity.document.facesheet.AllergyObservation;

@Repository
public interface AllergyObservationDao extends JpaRepository<AllergyObservation, Long> {

    public static final String QUERY_ALLERGY_OBSERVATION = " FROM  AllergyObservation ao"
            + " WHERE ao.id IN (SELECT MIN(aov.id) from AllergyObservation aov "
            + " WHERE aov.allergy.client.id IN (:clientId)"
            + " AND ( (lower(aov.observationStatusCode.displayName) = 'active') OR ((aov.allergy.timeLow < current_date()) AND ( (aov.allergy.timeHigh IS NULL) OR (aov.allergy.timeHigh > current_date()) ) ) )"
            + " GROUP BY aov.adverseEventTypeText,aov.productText,aov.observationStatusCode.id)";

    @Query("SELECT COUNT(ao) " + QUERY_ALLERGY_OBSERVATION)
    @Deprecated
    Long count(@Param("clientId") List<Long> clientId);

    @Query("SELECT ao " + QUERY_ALLERGY_OBSERVATION)
    @Deprecated
    Page<AllergyObservation> find(@Param("clientId") List<Long> clientId, Pageable pageable);
}
