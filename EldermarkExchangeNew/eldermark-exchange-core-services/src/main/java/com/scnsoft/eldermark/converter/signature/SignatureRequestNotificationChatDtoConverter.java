package com.scnsoft.eldermark.converter.signature;

import com.scnsoft.eldermark.dto.notification.signature.SignatureRequestNotificationChatDto;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class SignatureRequestNotificationChatDtoConverter {

    public SignatureRequestNotificationChatDto convert(DocumentSignatureRequestNotification source) {

        var documentSignatureRequest = source.getDocumentSignatureRequest();
        var documentSignatureTemplate = documentSignatureRequest.getSignatureTemplate();

        var requestedFromFullName = documentSignatureRequest.getRequestedFromEmployee() != null
                ? documentSignatureRequest.getRequestedFromEmployee().getFullName()
                : documentSignatureRequest.getRequestedFromClient().getFullName();

        var client = documentSignatureRequest.getClient();

        var target = new SignatureRequestNotificationChatDto();
        target.setMessage(documentSignatureRequest.getMessage());
        target.setRecipientName(requestedFromFullName);
        target.setUrl(documentSignatureRequest.getPdcflowSignatureUrl());
        target.setDateExpires(documentSignatureRequest.getDateExpires());
        target.setCommunityName(client.getCommunity().getName());
        target.setTemplateName(documentSignatureTemplate.getTitle());
        target.setRequestId(source.getDocumentSignatureRequestId());
        target.setClientId(client.getId());
        return target;
    }
}
