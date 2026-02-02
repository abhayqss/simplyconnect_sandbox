package org.openhealthtools.openxds.entity.message;

import org.openhealthtools.openxds.entity.segment.MSHMessageHeaderSegment;

public interface MSHSegmentContainingMessage {

    MSHMessageHeaderSegment getMsh();
}
