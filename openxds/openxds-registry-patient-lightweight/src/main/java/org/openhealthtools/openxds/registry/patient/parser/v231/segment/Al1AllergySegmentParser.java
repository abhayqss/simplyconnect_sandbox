package org.openhealthtools.openxds.registry.patient.parser.v231.segment;

import ca.uhn.hl7v2.model.v231.segment.AL1;
import ca.uhn.hl7v2.model.v231.segment.PD1;
import org.openhealthtools.openxds.entity.segment.AdtAL1AllergySegment;
import org.openhealthtools.openxds.entity.segment.AdtPD1AdditionalDemographicSegment;
import org.openhealthtools.openxds.registry.patient.parser.segment.AdtSegmentParser;

public interface Al1AllergySegmentParser extends AdtSegmentParser<AdtAL1AllergySegment, AL1> {
}
