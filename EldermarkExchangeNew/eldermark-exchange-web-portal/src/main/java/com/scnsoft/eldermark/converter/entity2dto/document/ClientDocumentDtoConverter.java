package com.scnsoft.eldermark.converter.entity2dto.document;

import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;
import com.scnsoft.eldermark.dto.document.DocumentDto;
import com.scnsoft.eldermark.dto.document.DocumentSignatureInfoDto;
import com.scnsoft.eldermark.entity.document.ClientDocument;
import com.scnsoft.eldermark.entity.signature.*;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestSecurityService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.document.singature.DocumentSignatureRequestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientDocumentDtoConverter extends BaseClientDocumentDtoConverter<DocumentDto> {

    @Autowired
    private DocumentSignatureRequestSecurityService signatureRequestSecurityService;

    @Override
    public DocumentDto convert(ClientDocument source) {
        var target = new DocumentDto();
        fillData(source, target);
        target.setTemporarilyDeletedDate(DateTimeUtils.toEpochMilli(source.getTemporaryDeletionTime()));
        if (source.getTemporaryDeletedBy() != null) {
            target.setTemporarilyDeletedBy(source.getTemporaryDeletedBy().getFullName());
        }
        target.setAssignedDate(DateTimeUtils.toEpochMilli(source.getCreationTime()));
        target.setAssignedBy(target.getAuthor());
        fillSignatureData(source, target);
        return target;
    }

    private void fillSignatureData(ClientDocument source, DocumentDto target) {
        var signatureRequest = source.getSignatureRequest();
        if (signatureRequest != null) {
            var signature = new DocumentSignatureInfoDto();

            signature.setRequestId(signatureRequest.getId());

            var status = DocumentSignatureRequestUtils.resolveCorrectSignatureStatus(signatureRequest);

            signature.setStatusName(status.name());
            signature.setStatusTitle(status.getTitle());

            signature.setCanRequest(signatureRequestSecurityService.canAdd(DocumentSignatureRequestSecurityFieldsAware.of(
                    source.getClientId(),
                    DocumentSignatureRequestSecurityService.ANY_TEMPLATE
            )));

            signature.setCanSign(
                    signatureRequestSecurityService.canSign(signatureRequest)
                            && signatureRequest.getStatus().isSignatureRequestSentStatus()
                            && BooleanUtils.isNotTrue(source.getTemporaryDeleted())
            );
            if (signature.getCanSign()) {
                signature.setPdcFlowLink(signatureRequest.getPdcflowSignatureUrl());
                signature.setPdcFlowPinCode(signatureRequest.getPdcflowPinCode());
            }

            if (signature.getCanRequest() && signatureRequest.getClient().getCommunity().getIsSignaturePinEnabled()) {
                signature.setCanResendPdcFlowPinCode(
                        status == DocumentSignatureStatus.REQUESTED
                                && signatureRequest.getRequestedFromClientId() != null
                                && CollectionUtils.isEmpty(signatureRequest.getRequestedFromClient().getAssociatedEmployeeIds())
                );
            }

            signature.setCanCancelRequest(
                    signature.getCanRequest()
                            && signatureRequest.getStatus().isSignatureRequestSentStatus()
                            && BooleanUtils.isNotTrue(source.getTemporaryDeleted())
            );

            signature.setHasAreas(
                    signatureRequest.getSubmittedFields().stream()
                            .map(DocumentSignatureRequestSubmittedField::getPdcflowOverlayType)
                            .anyMatch(it -> PdcFlowOverlayBoxType.signatureBoxIds().contains(it))
            );

            signature.setHasAvailableAreas(
                    signatureRequest.getNotSubmittedFields().stream()
                            .map(DocumentSignatureRequestNotSubmittedField::getPdcFlowType)
                            .anyMatch(TemplateFieldPdcFlowType.SIGNATURE::equals)
            );

            target.setTemplateId(signatureRequest.getSignatureTemplateId());
            target.setSignature(signature);
            target.setBulkRequestId(signatureRequest.getBulkRequestId());
        }
    }
}
