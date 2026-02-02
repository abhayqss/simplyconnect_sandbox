package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.document.ccd.Immunization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImmunizationDao extends JpaRepository<Immunization, Long> {
    List<Immunization> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
