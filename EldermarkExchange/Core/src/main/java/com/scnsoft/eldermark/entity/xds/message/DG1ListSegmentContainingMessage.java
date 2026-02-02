package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;

import java.util.List;

public interface DG1ListSegmentContainingMessage {
    List<AdtDG1DiagnosisSegment> getDg1List();
}
