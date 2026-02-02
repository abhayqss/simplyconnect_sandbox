package com.scnsoft.eldermark.mobile.converters.document;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestStatus;
import com.scnsoft.eldermark.mobile.dto.document.DocumentDto;
import com.scnsoft.eldermark.mobile.dto.document.DocumentSignatureDto;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestSecurityService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.service.document.signature.notification.SignatureNotificationService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.DocumentUtils;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureRequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class ClientDocumentDtoConverter extends BaseClientDocumentDtoConverter<DocumentDto> {

    @Autowired
    private DocumentSignatureRequestSecurityService signatureRequestSecurityService;

    @Autowired
    private DocumentSignatureRequestService documentSignatureRequestService;

    @Autowired
    private SignatureNotificationService signatureNotificationService;

    @Override
    public DocumentDto convert(ClientDocument source) {
        var target = new DocumentDto();
        fillBase(source, target);

        target.setSize(source.getSize());

        Client client = source.getClient();

        target.setClientFirstName(client.getFirstName());
        target.setClientLastName(client.getLastName());

        Community community = client.getCommunity();
        if (community != null) {
            target.setCommunityId(community.getId());
            target.setCommunityName(community.getName());
        }

        target.setDescription(source.getDescription());

        Employee author = source.getAuthor();
        target.setAuthor(author.getFullName());
        adjustForIntegrations(source, target);

        target.setAssignedDate(DateTimeUtils.toEpochMilli(source.getCreationTime()));
        target.setAssignedBy(target.getAuthor());

        if (source.getSignatureRequestId() != null) {
            fillSignatureInfo(source, target);
        }

        return target;
    }

    private void fillSignatureInfo(ClientDocument source, DocumentDto target) {
        var request = source.getSignatureRequest();
        var signatureDto = new DocumentSignatureDto();

        signatureDto.setRequestId(request.getId());
        signatureDto.setNotificationMethod(request.getNotificationMethod());
        signatureDto.setEmail(request.getEmail());
        signatureDto.setPhone(request.getPhoneNumber());
        signatureDto.setRequestedDate(DateTimeUtils.toEpochMilli(request.getDateCreated()));
        signatureDto.setRequestExpirationDate(DateTimeUtils.toEpochMilli(request.getDateExpires()));
        signatureDto.setStatus(DocumentSignatureRequestUtils.resolveCorrectSignatureStatus(request));
        signatureDto.setTemplateName(request.getSignatureTemplate().getTitle());
        signatureDto.setSignedDate(DateTimeUtils.toEpochMilli(request.getDateSigned()));

        // todo pdcflow integration
        signatureDto.setSignerIp(null);
        signatureDto.setSignerLocation(null);

        signatureDto.setCanceledDate(DateTimeUtils.toEpochMilli(request.getDateCanceled()));
        signatureDto.setFailedDate(DateTimeUtils.toEpochMilli(request.getPdcflowErrorDatetime()));

        signatureDto.setErrorMessage(request.getPdcflowErrorMessage());

        var currentRequestStatus = DocumentSignatureRequestUtils.resolveCorrectRequestStatus(request);
        signatureDto.setCanSign(
                currentRequestStatus.isSignatureRequestSentStatus() &&
                        signatureRequestSecurityService.canSign(request.getId())
        );
        if (signatureDto.getCanSign() && currentRequestStatus.isSignatureRequestSentStatus()) {
            signatureDto.setPdcFlowLink(request.getPdcflowSignatureUrl());
            signatureDto.setPdcFlowPinCode(request.getPdcflowPinCode());
        }

        signatureDto.setCanRenew(documentSignatureRequestService.canRenewByStatus(currentRequestStatus) &&
                signatureRequestSecurityService.canRenew(request.getId()));

        if (
                signatureRequestSecurityService.canResendPin(request.getId())
                        && request.getClient().getCommunity().getIsSignaturePinEnabled()
        ) {
            signatureNotificationService.getCanResendPinTimeAt(request)
                    .map(DateTimeUtils::toEpochMilli)
                    .ifPresent(signatureDto::setCanResendPinAt);
        }

        target.setSignature(signatureDto);
    }

    private void adjustForIntegrations(ClientDocument source, DocumentDto target) {
        DocumentUtils.adjustForIntegrations(source, new DocumentUtils.DocumentDtoAdjustableForIntegrations() {
            @Override
            public void setAuthor(String author) {
                target.setAuthor(author);
            }

            @Override
            public void setOrganizationTitle(String organizationTitle) {
                //do nothing
            }

            @Override
            public void setOrganizationOid(String oid) {
                //do nothing
            }
        });
    }
}
