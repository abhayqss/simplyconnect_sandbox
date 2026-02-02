package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.MSHMessageHeaderSegment;

public interface MSHSegmentContainingMessage {

    MSHMessageHeaderSegment getMsh();
}
