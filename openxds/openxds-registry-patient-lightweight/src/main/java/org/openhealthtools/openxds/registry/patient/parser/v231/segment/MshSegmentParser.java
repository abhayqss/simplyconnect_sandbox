package org.openhealthtools.openxds.registry.patient.parser.v231.segment;

import ca.uhn.hl7v2.model.v231.segment.MSH;
import org.openhealthtools.openxds.entity.segment.MSHMessageHeaderSegment;
import org.openhealthtools.openxds.registry.patient.parser.segment.AdtSegmentParser;

public interface MshSegmentParser extends AdtSegmentParser<MSHMessageHeaderSegment, MSH> {

}
