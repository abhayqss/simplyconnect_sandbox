package com.scnsoft.eldermark.entity.xds.message;

import java.util.List;

import com.scnsoft.eldermark.entity.xds.segment.AdtGT1GuarantorSegment;

public interface GT1ListSegmentContainingMessage {
    List<AdtGT1GuarantorSegment> getGt1List();
}
