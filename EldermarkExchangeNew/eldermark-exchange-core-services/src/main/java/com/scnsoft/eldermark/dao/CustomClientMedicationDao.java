package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ClientMedicationCount;
import com.scnsoft.eldermark.entity.medication.ClientMedication;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CustomClientMedicationDao {

    List<ClientMedicationCount> countGroupedByStatus(Specification<ClientMedication> specification);
}
