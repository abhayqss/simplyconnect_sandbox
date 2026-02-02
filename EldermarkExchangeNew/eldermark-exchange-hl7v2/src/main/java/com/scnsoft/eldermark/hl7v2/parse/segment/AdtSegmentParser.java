package com.scnsoft.eldermark.hl7v2.parse.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.AbstractSegment;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;

public interface AdtSegmentParser<TO, FROM extends AbstractSegment> {

    TO parse(FROM segment, MessageSource messageSource) throws HL7Exception, ApplicationException;

}
