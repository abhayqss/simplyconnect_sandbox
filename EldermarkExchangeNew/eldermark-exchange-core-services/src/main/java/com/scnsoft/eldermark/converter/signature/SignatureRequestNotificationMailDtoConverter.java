package com.scnsoft.eldermark.converter.signature;

import com.scnsoft.eldermark.dto.notification.signature.SignatureRequestNotificationMailDto;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotificationType;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;

@Component
@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
public class SignatureRequestNotificationMailDtoConverter {

    private static final String EMAIL_TEMPLATE_FILE = "signature/SignatureRequestNotificationEmail.vm";
    private static final String SIGNATURE_REQUEST_MAIL_SUBJECT = "Your Document is Ready for Review";

    @Autowired
    private UrlService urlService;

    public SignatureRequestNotificationMailDto convert(DocumentSignatureRequestNotification source) {

        var documentSignatureRequest = source.getDocumentSignatureRequest();
        var documentSignatureTemplate = documentSignatureRequest.getSignatureTemplate();

        var requestedFromFullName = documentSignatureRequest.getRequestedFromEmployee() != null
                ? documentSignatureRequest.getRequestedFromEmployee().getFullName()
                : documentSignatureRequest.getRequestedFromClient().getFullName();

        var client = documentSignatureRequest.getClient();

        SignatureRequestNotificationMailDto target = new SignatureRequestNotificationMailDto();

        target.setEmail(source.getEmail());
        target.setRecipientName(requestedFromFullName);
        target.setSubject(SIGNATURE_REQUEST_MAIL_SUBJECT);
        target.setTemplateFile(EMAIL_TEMPLATE_FILE);
        target.setCommunityName(client.getCommunity().getName());
        target.setTemplateName(documentSignatureTemplate.getTitle());

        if (source.getType() == DocumentSignatureRequestNotificationType.SIGN_LINK_EMAIL_TO_CONTACT) {
            target.setUrl(urlService.signatureRequestUrl(client.getId(), documentSignatureRequest.getId()));
            target.setDateExpires(DateTimeUtils.formatDateTimeWithZone(documentSignatureRequest.getDateExpires(), ZoneId.of("America/Chicago")));
        } else if (source.getType() == DocumentSignatureRequestNotificationType.SIGN_LINK_EMAIL_TO_CLIENT) {
            target.setUrl(documentSignatureRequest.getPdcflowSignatureUrl());
        } else {
            throw new IllegalArgumentException("Unexpected notification type");
        }

        return target;
    }
}
