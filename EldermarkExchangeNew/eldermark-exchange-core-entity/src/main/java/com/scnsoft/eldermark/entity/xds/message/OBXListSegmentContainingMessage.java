package com.scnsoft.eldermark.entity.xds.message;

import com.scnsoft.eldermark.entity.xds.segment.OBXObservationResult;

import java.util.List;

public interface OBXListSegmentContainingMessage {

    List<OBXObservationResult> getObxList();

}
