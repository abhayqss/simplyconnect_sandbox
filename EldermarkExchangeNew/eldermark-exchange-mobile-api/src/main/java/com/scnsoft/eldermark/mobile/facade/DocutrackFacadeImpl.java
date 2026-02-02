package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.dto.docutrack.DocutrackSupportedFileListItemDto;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.mobile.dto.docutrack.BusinessUnitCodeListItemDto;
import com.scnsoft.eldermark.mobile.dto.docutrack.SendToDocutrackDto;
import com.scnsoft.eldermark.service.docutrack.DocutrackService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class DocutrackFacadeImpl implements DocutrackFacade {

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private DocutrackService docutrackService;

    @Override
    @Transactional(readOnly = true)
    public List<BusinessUnitCodeListItemDto> getBusinessUnitCodes() {
        var currentEmployee = loggedUserService.getCurrentEmployee();
        var pharmacies = docutrackService.getAssociatedPharmacies(currentEmployee);
        return pharmacies.stream()
            .map(Community::getBusinessUnitCodes)
            .filter(Objects::nonNull)
            .flatMap(Collection::stream)
            .map(BusinessUnitCodeListItemDto::new)
            .sorted(Comparator.comparing(BusinessUnitCodeListItemDto::getBusinessCode))
            .collect(Collectors.toList());
    }

    @Override
    public List<DocutrackSupportedFileListItemDto> getSupportedFileTypes() {
        return docutrackService.getSupportedFileTypes();
    }

    @Override
    @Transactional(readOnly = true)
    public void sendToDocutrack(SendToDocutrackDto sendToDocutrackDto) {
        docutrackService.sendToDocutrackFromChat(
            loggedUserService.getCurrentEmployee(),
            sendToDocutrackDto.getMediaSid(),
            sendToDocutrackDto.getBusinessUnitCode(),
            sendToDocutrackDto.getDocumentText()
        );
    }
}
