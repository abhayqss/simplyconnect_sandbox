package com.scnsoft.eldermark.service.document.facesheet;

import com.scnsoft.eldermark.entity.document.DocumentReport;
import com.scnsoft.eldermark.shared.FaceSheetDto;

import java.time.ZoneId;

public interface FacesheetService {
    FaceSheetDto construct(long residentId, boolean aggregated);

    DocumentReport generate(Long clientId, boolean aggregated, ZoneId zoneId);

    DocumentReport metadata();
}
