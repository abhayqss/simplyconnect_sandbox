package com.scnsoft.eldermark.hl7v2.facade;

import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

public interface HL7v2MessageFacade {

    MessageAndLogProcessingResult processMessage(Message message, MessageSource messageSource);

}
