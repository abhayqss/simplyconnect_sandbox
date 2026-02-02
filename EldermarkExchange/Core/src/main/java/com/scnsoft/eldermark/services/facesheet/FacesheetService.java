package com.scnsoft.eldermark.services.facesheet;

import com.scnsoft.eldermark.shared.FaceSheetDto;

public interface FacesheetService {
    FaceSheetDto construct(long residentId, boolean aggregated);
}
