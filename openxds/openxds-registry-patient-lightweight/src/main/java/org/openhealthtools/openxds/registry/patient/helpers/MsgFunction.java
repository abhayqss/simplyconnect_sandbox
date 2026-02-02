package org.openhealthtools.openxds.registry.patient.helpers;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;

public interface MsgFunction<R> {
    R apply(Message message) throws HL7Exception;
}
