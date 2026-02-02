package com.scnsoft.eldermark.services.converters.hl7.v251;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.util.Terser;

public interface EntityFiller<T> {

    void fill(String base, Terser messageTerser, T entity) throws HL7Exception;
}
