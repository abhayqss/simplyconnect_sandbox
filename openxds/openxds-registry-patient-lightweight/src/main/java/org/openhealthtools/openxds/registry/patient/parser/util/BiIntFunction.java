package org.openhealthtools.openxds.registry.patient.parser.util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;

public interface BiIntFunction<P, R> {
    R apply(P parameter, int i) throws HL7Exception, ApplicationException;
}
