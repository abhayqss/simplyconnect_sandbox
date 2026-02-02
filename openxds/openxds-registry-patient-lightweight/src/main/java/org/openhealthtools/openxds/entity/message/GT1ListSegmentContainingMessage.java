package org.openhealthtools.openxds.entity.message;

import org.openhealthtools.openxds.entity.segment.AdtGT1GuarantorSegment;

import java.util.List;

public interface GT1ListSegmentContainingMessage {
    List<AdtGT1GuarantorSegment> getGt1List();
}
