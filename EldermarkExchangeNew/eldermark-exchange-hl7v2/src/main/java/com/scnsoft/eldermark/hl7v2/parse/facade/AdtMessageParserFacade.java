package com.scnsoft.eldermark.hl7v2.parse.facade;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

import java.util.Optional;

public interface AdtMessageParserFacade {

    Optional<AdtMessage> parse(Message msgIn, MessageSource messageSource) throws HL7Exception, ApplicationException;

}
