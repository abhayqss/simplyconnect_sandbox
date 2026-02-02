package com.scnsoft.eldermark.entity.xds.message;


import com.scnsoft.eldermark.entity.xds.segment.AdtAL1AllergySegment;

import java.util.List;

public interface AL1ListSegmentContainingMessage {
    List<AdtAL1AllergySegment> getAL1List();
}
