package com.scnsoft.eldermark.hl7v2.processor.patient.demographics;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

public interface PatientDemographicsExtractor {

    HL7v2PatientDemographics extractDemographics(Message message, MessageSource messageSource) throws ApplicationException, HL7Exception;

}
