package org.openhealthtools.openxds.entity.message;

import org.openhealthtools.openxds.entity.segment.AdtDG1DiagnosisSegment;

import java.util.List;

public interface DG1ListSegmentContainingMessage {
    List<AdtDG1DiagnosisSegment> getDg1List();
}
