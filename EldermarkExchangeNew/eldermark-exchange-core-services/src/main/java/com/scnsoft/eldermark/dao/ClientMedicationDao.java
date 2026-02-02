package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientMedicationDao extends AppJpaRepository<ClientMedication, Long>,
        CustomClientMedicationDao {
}