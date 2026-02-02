package com.scnsoft.eldermark.service.document.signature.notification;

import com.scnsoft.eldermark.dto.notification.signature.SignatureRequestCancelNotificationMailDto;
import com.scnsoft.eldermark.entity.PersonTelecomCode;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SignatureCancelNotificationServiceImpl implements SignatureCancelNotificationService {

    private static final String EMAIL_TEMPLATE_FILE = "signature/signatureRequestCancelNotificationEmail.vm";
    private static final String SIGNATURE_REQUEST_CANCEL_MAIL_SUBJECT = "Signature request has been cancelled";

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private UrlService urlService;

    @Override
    public void sendCancelNotification(DocumentSignatureRequest request) {
        var signatureCancelNotification = createSignatureCancelNotification(request);
        exchangeMailService.sendCancelSignatureRequestNotification(signatureCancelNotification);
    }

    private SignatureRequestCancelNotificationMailDto createSignatureCancelNotification(DocumentSignatureRequest request) {
        var signatureRequestCancelNotificationMailDto = new SignatureRequestCancelNotificationMailDto();
        signatureRequestCancelNotificationMailDto.setSubject(SIGNATURE_REQUEST_CANCEL_MAIL_SUBJECT);
        signatureRequestCancelNotificationMailDto.setTemplateFile(EMAIL_TEMPLATE_FILE);
        var signatureRequestUrl = urlService.signatureRequestUrl(request.getClientId(), request.getId());
        if (request.getRequestedFromEmployee() != null) {
            var employee = request.getRequestedFromEmployee();
            var employeeEmail = PersonTelecomUtils.findValue(employee.getPerson(), PersonTelecomCode.EMAIL)
                    .orElse(null);
            signatureRequestCancelNotificationMailDto.setEmail(employeeEmail);
            signatureRequestCancelNotificationMailDto.setRecipientName(employee.getFullName());
            signatureRequestCancelNotificationMailDto.setUrl(signatureRequestUrl);
        } else if (request.getRequestedFromClient() != null) {
            var client = request.getRequestedFromClient();
            var clientEmail = PersonTelecomUtils.findValue(client.getPerson(), PersonTelecomCode.EMAIL)
                    .orElse(null);
            signatureRequestCancelNotificationMailDto.setEmail(clientEmail);
            signatureRequestCancelNotificationMailDto.setRecipientName(client.getFullName());
            signatureRequestCancelNotificationMailDto.setUrl(signatureRequestUrl);
        }
        return signatureRequestCancelNotificationMailDto;
    }
}
