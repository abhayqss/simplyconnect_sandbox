package com.scnsoft.eldermark.hl7v2.processor.patient;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.hl7v2.model.PatientIdentifiersHolder;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

public interface PatientIdentifiersExtractor {

    PatientIdentifiersHolder extractPatientIdentifiers(Message message, MessageSource messageSource) throws HL7Exception;
}
