package com.scnsoft.eldermark.hl7v2.processor.patient;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.hl7v2.model.PatientIdentifiersHolder;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

import java.util.Optional;

public interface PatientResolver {

    Optional<Client> resolvePatient(PatientIdentifiersHolder patientIdentifiersHolder, MessageSource messageSource);
}
