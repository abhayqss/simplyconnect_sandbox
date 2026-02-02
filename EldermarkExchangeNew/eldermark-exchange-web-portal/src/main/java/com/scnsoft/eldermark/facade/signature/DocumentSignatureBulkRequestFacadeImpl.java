package com.scnsoft.eldermark.facade.signature;

import com.scnsoft.eldermark.dto.signature.DocumentSignatureBulkRequestInfoDto;
import com.scnsoft.eldermark.dto.signature.DocumentSignatureBulkRequestRenewDto;
import com.scnsoft.eldermark.dto.singature.SubmitTemplateSignatureBulkRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureBulkRequestService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentSignatureBulkRequestFacadeImpl implements DocumentSignatureBulkRequestFacade {

    @Autowired
    private DocumentSignatureBulkRequestService signatureBulkRequestService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Override
    @PreAuthorize("@documentSignatureBulkRequestSecurityService.canSubmit(#dto)")
    @Transactional
    public Long submitBulkRequest(SubmitTemplateSignatureBulkRequest dto) {
        var currentEmployee = loggedUserService.getCurrentEmployee();
        dto.setRequestedBy(currentEmployee);
        var submittedBulkRequest = signatureBulkRequestService.submitRequest(dto);
        return submittedBulkRequest.getId();
    }

    @Override
    @PreAuthorize("@documentSignatureBulkRequestSecurityService.canRenew(#dto.bulkRequestId)")
    @Transactional
    public Long renewBulkRequest(DocumentSignatureBulkRequestRenewDto dto) {
        return signatureBulkRequestService.renewBulkRequest(
                dto.getBulkRequestId(),
                DateTimeUtils.toInstant(dto.getExpirationDate()),
                dto.getTemplateId(),
                loggedUserService.getCurrentEmployee()
        ).getId();
    }

    @Override
    @PreAuthorize("@documentSignatureBulkRequestSecurityService.canView(#id)")
    @Transactional(readOnly = true)
    public List<DocumentSignatureBulkRequestInfoDto> fetchRequests(Long id, List<DocumentSignatureRequestStatus> statuses) {
        return signatureBulkRequestService.fetchRequestsByIdAndStatusIn(id, statuses)
                .stream()
                .map(request -> {
                    var requestInfoDto = new DocumentSignatureBulkRequestInfoDto();
                    requestInfoDto.setId(request.getId());
                    requestInfoDto.setTemplateTitle(request.getSignatureTemplate().getTitle());
                    requestInfoDto.setClientFullName(request.getClient().getFullName());
                    requestInfoDto.setClientId(request.getClientId());
                    requestInfoDto.setTemplateId(request.getSignatureTemplateId());
                    return requestInfoDto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@documentSignatureBulkRequestSecurityService.canCancel(#id)")
    @Transactional
    public void cancelRequest(Long id, Long templateId) {
        signatureBulkRequestService.cancelBulkRequest(id, templateId, loggedUserService.getCurrentEmployee());
    }
}
