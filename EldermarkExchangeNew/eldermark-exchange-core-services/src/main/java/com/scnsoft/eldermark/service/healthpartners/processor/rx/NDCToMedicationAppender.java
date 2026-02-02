package com.scnsoft.eldermark.service.healthpartners.processor.rx;

import com.scnsoft.eldermark.entity.medication.Medication;

interface NDCToMedicationAppender {

    void addUniqueNdcToMedication(Medication medication, String nationalDrugCode, String drugName);

}
