package org.openhealthtools.openxds.entity.message;

import org.openhealthtools.openxds.entity.segment.AdtAL1AllergySegment;

import java.util.List;

public interface AL1ListSegmentContainingMessage {
    List<AdtAL1AllergySegment> getAL1List();
}
