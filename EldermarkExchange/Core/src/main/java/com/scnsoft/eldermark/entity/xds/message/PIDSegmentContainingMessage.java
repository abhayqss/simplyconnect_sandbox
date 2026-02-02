package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.PIDPatientIdentificationSegment;

public interface PIDSegmentContainingMessage {

    PIDPatientIdentificationSegment getPid();
}
