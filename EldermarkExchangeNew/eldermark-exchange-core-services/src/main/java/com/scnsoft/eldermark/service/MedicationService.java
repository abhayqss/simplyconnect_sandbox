package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dto.medication.SaveMedicationRequest;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.medication.Medication;

import java.util.Optional;

public interface MedicationService {

    Medication save(Medication medication);

    Medication save(SaveMedicationRequest saveMedicationRequest);

    Optional<Medication> findHealthPartnersMedication(Long clientId,
                                                      CcdCode rxNormCode,
                                                      String drugName,
                                                      Integer refillNumber,
                                                      String prescriberFirstName,
                                                      String prescriberLastName,
                                                      String prescriberNpi);

    void delete(Medication medication);
}
