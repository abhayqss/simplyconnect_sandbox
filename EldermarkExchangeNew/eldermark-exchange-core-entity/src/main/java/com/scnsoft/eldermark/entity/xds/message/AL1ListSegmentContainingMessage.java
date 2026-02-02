package com.scnsoft.eldermark.entity.xds.message;

import java.util.List;

import com.scnsoft.eldermark.entity.xds.segment.AdtAL1AllergySegment;

public interface AL1ListSegmentContainingMessage {
    List<AdtAL1AllergySegment> getAL1List();
}

