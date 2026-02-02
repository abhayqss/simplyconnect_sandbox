package com.scnsoft.eldermark.hl7v2.processor.patient;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.hl7v2.processor.patient.demographics.HL7v2PatientDemographics;

public interface PatientDemographicsUpdater {

    Client updateDemographics(Client patient, HL7v2PatientDemographics patientDemographics);
}
