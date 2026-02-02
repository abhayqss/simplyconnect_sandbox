package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureHistory;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureHistoryDto;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureRequestDto;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureRequestRenewDto;
import com.scnsoft.eldermark.mobile.dto.signature.DocumentSignatureResendPinResponseDto;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureHistoryService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestSecurityService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.service.document.signature.notification.SignatureNotificationService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestSecurityService.ANY_TEMPLATE;

@Service
@Transactional
public class DocumentSignatureRequestFacadeImpl implements DocumentSignatureRequestFacade {

    @Autowired
    private DocumentSignatureRequestService signatureRequestService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private DocumentSignatureRequestSecurityService signatureRequestSecurityService;

    @Autowired
    private DocumentSignatureHistoryService signatureHistoryService;

    @Autowired
    private SignatureNotificationService signatureNotificationService;

    @Autowired
    private Converter<DocumentSignatureRequest, DocumentSignatureRequestDto> signatureRequestDtoConverter;

    @Autowired
    private Converter<DocumentSignatureHistory, DocumentSignatureHistoryDto> signatureHistoryConverter;

    @Override
    @PreAuthorize("@documentSignatureRequestSecurityService.canSign(#requestId)")
    @Transactional(readOnly = true)
    public DocumentSignatureRequestDto getById(Long requestId) {
        return signatureRequestDtoConverter.convert(
                signatureRequestService.findById(requestId)
        );
    }

    @Override
    @PreAuthorize("@clientDocumentSecurityService.canView(#documentId)")
    @Transactional(readOnly = true)
    public Page<DocumentSignatureHistoryDto> findHistoryByDocumentId(Long documentId, Pageable pageable, Integer timezoneOffset) {

        return signatureHistoryService.findByDocumentId(
                documentId,
                PaginationUtils.applyEntitySort(pageable, DocumentSignatureHistoryDto.class)
        ).map(entity -> {
            var dto = Objects.requireNonNull(signatureHistoryConverter.convert(entity));
            dto.setComments(signatureHistoryService.generateCommentsForHistory(entity, timezoneOffset));
            return dto;
        });
    }

    @Override
    @PreAuthorize("@documentSignatureRequestSecurityService.canRenew(#dto.requestId)")
    public Long renewRequest(DocumentSignatureRequestRenewDto dto) {
        return signatureRequestService.renewRequest(
                dto.getRequestId(),
                DateTimeUtils.toInstant(dto.getExpirationDate()),
                loggedUserService.getCurrentEmployee()
        ).getId();
    }

    @Override
    @PreAuthorize("@documentSignatureRequestSecurityService.canCancel(#requestId)")
    public void cancelRequest(Long requestId) {
        signatureRequestService.cancelRequest(requestId, loggedUserService.getCurrentEmployee());
    }

    @Override
    @PreAuthorize("@documentSignatureRequestSecurityService.canResendPin(#requestId)")
    @Transactional
    public DocumentSignatureResendPinResponseDto resendPin(Long requestId) {
        var pinNotification = signatureRequestService.resendPin(requestId);

        var result = new DocumentSignatureResendPinResponseDto();
        result.setReceiverPhone(pinNotification.getPhoneNumber());
        result.setCanResendPinAt(DateTimeUtils.toEpochMilli(
                signatureNotificationService.getCanResendPinTimeAt(pinNotification)
        ));
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long clientId) {
        return signatureRequestSecurityService.canAdd(
                DocumentSignatureRequestSecurityFieldsAware.of(clientId, ANY_TEMPLATE)
        );
    }
}
