package org.openhealthtools.openxds.registry.patient.parser.segment;

import ca.uhn.hl7v2.model.AbstractGroup;

//todo perhaps unnecessary
public interface AdtSegmentGroupParser<TO, FROM extends AbstractGroup> {

    TO parse(FROM segmentGroup);

}
