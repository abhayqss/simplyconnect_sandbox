package com.scnsoft.eldermark.consana.sync.server.services;

import com.scnsoft.eldermark.consana.sync.server.model.entity.Medication;
import com.scnsoft.eldermark.consana.sync.server.model.entity.Resident;

import java.util.List;

public interface MedicationService {

    Medication saveMedication(Medication medication);

    List<Medication> findAllConsanaMedicationsByResident(Resident resident);

    void delete(Medication medication);
}
