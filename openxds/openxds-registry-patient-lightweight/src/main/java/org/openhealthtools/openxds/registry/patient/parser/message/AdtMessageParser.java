package org.openhealthtools.openxds.registry.patient.parser.message;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import org.openhealthtools.openxds.entity.message.AdtMessage;

public interface AdtMessageParser<S extends AdtMessage, M extends Message> {

    S parse(M message) throws HL7Exception, ApplicationException;

    Class<M> getMessageClass();

}
