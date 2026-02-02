package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.scnsoft.eldermark.entity.document.ccd.MedicalEquipment;

public interface MedicalEquipmentDao extends JpaRepository<MedicalEquipment, Long> {
    List<MedicalEquipment> findByClient_IdIn(List<Long> clientIds);

    void deleteAllByClientId(Long clientId);
}
