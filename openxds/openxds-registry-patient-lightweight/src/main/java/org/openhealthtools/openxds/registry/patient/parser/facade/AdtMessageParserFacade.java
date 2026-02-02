package org.openhealthtools.openxds.registry.patient.parser.facade;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.Message;
import org.openhealthtools.openxds.entity.message.AdtMessage;

public interface AdtMessageParserFacade {

    AdtMessage parse(Message msgIn) throws HL7Exception, ApplicationException;

}
