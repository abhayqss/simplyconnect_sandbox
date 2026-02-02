package org.openhealthtools.openxds.registry.patient.parser.segment;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.AbstractSegment;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;

public interface AdtSegmentParser<TO, FROM extends AbstractSegment> {

    TO parse(FROM segment) throws HL7Exception, ApplicationException;

    /**
     *  service is used to find out whether a segment is empty according to specification.
     *  To find out please use any mandatory field of the HL7 segment using specification,
     *  e. g. on http://hl7-definition.caristix.com:9010/HL7%20v2.3/triggerEvent/Default.aspx?version=HL7+v2.3.1&segment=EVN
     * @param hl7Segment
     * @return
     * @see EmptyHL7Field231Service
     */
    boolean isHl7SegmentEmpty(FROM hl7Segment);

}
