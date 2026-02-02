package com.scnsoft.eldermark.hl7v2.processor;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

public interface HL7v2MessageService {

    MessageProcessingResult processMessage(Message message, MessageSource messageSource) throws HL7Exception, ApplicationException;

}
