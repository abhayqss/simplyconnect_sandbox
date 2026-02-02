package org.openhealthtools.openxds.registry.patient.parser.v231.segment;

import ca.uhn.hl7v2.model.v231.segment.PID;
import org.openhealthtools.openxds.entity.segment.PIDPatientIdentificationSegment;
import org.openhealthtools.openxds.registry.patient.parser.segment.AdtSegmentParser;

public interface PidSegmentParser extends AdtSegmentParser<PIDPatientIdentificationSegment, PID> {

}
