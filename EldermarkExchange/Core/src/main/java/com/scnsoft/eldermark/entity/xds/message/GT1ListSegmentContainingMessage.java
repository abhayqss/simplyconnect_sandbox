package com.scnsoft.eldermark.entity.xds.message;


import com.scnsoft.eldermark.entity.xds.segment.AdtGT1GuarantorSegment;

import java.util.List;

public interface GT1ListSegmentContainingMessage {
    List<AdtGT1GuarantorSegment> getGt1List();
}
