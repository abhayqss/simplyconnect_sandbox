package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.dto.CertificateInfoDto;
import com.scnsoft.eldermark.dto.docutrack.AttachFromDocutrackDto;
import com.scnsoft.eldermark.dto.docutrack.BusinessUnitCodeListItemDto;
import com.scnsoft.eldermark.dto.docutrack.DocutrackSupportedFileListItemDto;
import com.scnsoft.eldermark.dto.docutrack.SendToDocutrackDto;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.service.docutrack.DocutrackService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.cert.X509Certificate;
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

    @Autowired
    private Converter<X509Certificate, CertificateInfoDto> certificateInfoDtoConverter;

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
        docutrackService.sendToDocutrackFromChat(loggedUserService.getCurrentEmployee(),
                sendToDocutrackDto.getMediaSid(),
                sendToDocutrackDto.getBusinessUnitCode(),
                sendToDocutrackDto.getDocumentText()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public void attachFromDocutrack(AttachFromDocutrackDto attachFromDocutrackDto) {
        docutrackService.attachFromDocutrackToChat(
                loggedUserService.getCurrentEmployee(),
                attachFromDocutrackDto.getConversationSid(),
                attachFromDocutrackDto.getDocumentId()
        );

    }

    @Override
    @PreAuthorize("@docutrackSecurityService.canConfigureDocutrackInAnyOrg()")
    public CertificateInfoDto getSelfSignedCert(String serverDomain) {
        var cert = docutrackService.loadServerCertIfSelfSigned(serverDomain);
        return cert
                .map(certificateInfoDtoConverter::convert)
                .orElse(null);
    }

    @Override
    @PreAuthorize("@docutrackSecurityService.canConfigureDocutrackInAnyOrg()")
    public List<String> nonUniqueBusinessUnitCodes(String serverDomain, Long excludeCommunityId, List<String> businessUnitCodes) {
        return docutrackService.nonUniqueBusinessUnitCodes(serverDomain, excludeCommunityId, businessUnitCodes);
    }
}
