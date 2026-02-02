package org.openhealthtools.openxds.entity.message;


import org.openhealthtools.openxds.entity.segment.PR1ProceduresSegment;

import java.util.List;

public interface PR1ListSegmentContaingMessage {

    List<PR1ProceduresSegment> getPr1List();
}
