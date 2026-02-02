package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.AbstractSegment;
import org.openhealthtools.openxds.registry.patient.parser.segment.AdtSegmentParser;

public abstract class AbstractAdtSegmentParser<TO, FROM extends AbstractSegment> implements AdtSegmentParser<TO, FROM> {

    @Override
    public TO parse(FROM segment) throws HL7Exception, ApplicationException {
        if (isHl7SegmentEmpty(segment)) {
            return null;
        }
        return doParse(segment);
    }

    protected abstract TO doParse(FROM segment) throws HL7Exception, ApplicationException ;

    @Override
    public boolean isHl7SegmentEmpty(FROM hl7Segment) {
        return hl7Segment == null;
    }
}
