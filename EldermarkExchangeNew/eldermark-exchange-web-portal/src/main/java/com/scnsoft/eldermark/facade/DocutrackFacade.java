package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.CertificateInfoDto;
import com.scnsoft.eldermark.dto.docutrack.AttachFromDocutrackDto;
import com.scnsoft.eldermark.dto.docutrack.BusinessUnitCodeListItemDto;
import com.scnsoft.eldermark.dto.docutrack.DocutrackSupportedFileListItemDto;
import com.scnsoft.eldermark.dto.docutrack.SendToDocutrackDto;

import java.util.List;

public interface DocutrackFacade {

    List<BusinessUnitCodeListItemDto> getBusinessUnitCodes();

    List<DocutrackSupportedFileListItemDto> getSupportedFileTypes();

    void sendToDocutrack(SendToDocutrackDto sendToDocutrackDto);

    void attachFromDocutrack(AttachFromDocutrackDto attachFromDocutrackDto);

    CertificateInfoDto getSelfSignedCert(String serverDomain);

    List<String> nonUniqueBusinessUnitCodes(String serverDomain, Long excludeCommunityId, List<String> businessUnitCodes);
}
