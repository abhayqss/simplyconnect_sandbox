package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.dto.docutrack.DocutrackSupportedFileListItemDto;
import com.scnsoft.eldermark.mobile.dto.docutrack.BusinessUnitCodeListItemDto;
import com.scnsoft.eldermark.mobile.dto.docutrack.SendToDocutrackDto;

import java.util.List;

public interface DocutrackFacade {

    List<BusinessUnitCodeListItemDto> getBusinessUnitCodes();

    List<DocutrackSupportedFileListItemDto> getSupportedFileTypes();

    void sendToDocutrack(SendToDocutrackDto sendToDocutrackDto);
}
