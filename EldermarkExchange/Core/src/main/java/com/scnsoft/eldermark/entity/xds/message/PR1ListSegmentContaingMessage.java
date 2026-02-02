package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.PR1ProceduresSegment;

import java.util.List;

public interface PR1ListSegmentContaingMessage {

    List<PR1ProceduresSegment> getPr1List();
}
