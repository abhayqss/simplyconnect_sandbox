package com.scnsoft.eldermark.hl7v2.parse.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import com.scnsoft.eldermark.entity.xds.message.AdtMessage;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

//todo move to services or another common module and combine with ORU parsing
public interface AdtMessageParser<S extends AdtMessage, M extends Message> {

    S parse(M message, MessageSource messageSource) throws HL7Exception, ApplicationException;

    String getMessageType();

}
