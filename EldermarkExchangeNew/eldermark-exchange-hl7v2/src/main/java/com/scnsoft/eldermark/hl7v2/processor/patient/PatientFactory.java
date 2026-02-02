package com.scnsoft.eldermark.hl7v2.processor.patient;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.hl7v2.model.PatientIdentifiersHolder;
import com.scnsoft.eldermark.hl7v2.processor.patient.demographics.HL7v2PatientDemographics;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

public interface PatientFactory {

    Client createPatient(PatientIdentifiersHolder patientIdentifiersHolder, HL7v2PatientDemographics patientDemographics, MessageSource messageSource);
}
