package org.openhealthtools.openxds.entity.message;


import org.openhealthtools.openxds.entity.segment.PIDPatientIdentificationSegment;

public interface PIDSegmentContainingMessage {

    PIDPatientIdentificationSegment getPid();
}
