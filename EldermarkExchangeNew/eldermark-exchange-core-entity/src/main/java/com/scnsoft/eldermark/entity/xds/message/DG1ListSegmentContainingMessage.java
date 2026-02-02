package com.scnsoft.eldermark.entity.xds.message;

import java.util.List;

import com.scnsoft.eldermark.entity.xds.segment.AdtDG1DiagnosisSegment;

public interface DG1ListSegmentContainingMessage {
    List<AdtDG1DiagnosisSegment> getDg1List();
}

