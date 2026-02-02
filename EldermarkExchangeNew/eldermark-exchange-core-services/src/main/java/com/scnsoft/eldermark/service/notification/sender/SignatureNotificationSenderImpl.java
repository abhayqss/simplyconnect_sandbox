package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.converter.signature.SignatureRequestNotificationMailDtoConverter;
import com.scnsoft.eldermark.dao.signature.DocumentSignatureRequestNotificationDao;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotificationType;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.SmsService;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.service.UrlShortenerService;
import com.scnsoft.eldermark.service.mail.ExchangeMailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

import static com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotificationType.*;

@Service
public class SignatureNotificationSenderImpl implements SignatureNotificationSender {

    private static final Logger logger = LoggerFactory.getLogger(SignatureNotificationSenderImpl.class);

    private static final String SMS_PIN_CODE_NOTIFICATION_TEMPLATE = "Your %s PIN is %s.";
    private static final String SMS_REVIEW_AND_SIGN_NOTIFICATION_TEMPLATE = "Dear %s,\n" +
            "%s %s is ready for review. \n" +
            "Review/Sign Document: %s";

    private static final List<DocumentSignatureRequestNotificationType> SIGN_LINK_EMAIL_NOTIFICATION_TYPES = List.of(
            SIGN_LINK_EMAIL_TO_CLIENT,
            SIGN_LINK_EMAIL_TO_CONTACT
    );
    private static final List<DocumentSignatureRequestNotificationType> SIGN_LINK_SMS_NOTIFICATION_TYPES = List.of(
            SIGN_LINK_SMS_TO_CLIENT,
            SIGN_LINK_SMS_TO_CONTACT
    );

    @Autowired
    private ExchangeMailService exchangeMailService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private DocumentSignatureRequestNotificationDao signatureRequestNotificationDao;

    @Autowired
    private SignatureRequestNotificationMailDtoConverter signatureRequestNotificationMailDtoConverter;

    @Autowired
    private UrlShortenerService urlShortenerService;

    @Autowired
    private UrlService urlService;

    @Autowired
    private ChatNotificationSender chatNotificationSender;

    @Autowired
    private ClientService clientService;

    @Async
    @Transactional
    public void send(Long id) {
        var notification = signatureRequestNotificationDao.getOne(id);

        try {
            boolean result;
            if (SIGN_LINK_EMAIL_NOTIFICATION_TYPES.contains(notification.getType())) {
                result = sendEmailSignLinkNotification(notification);
            } else if (SIGN_LINK_SMS_NOTIFICATION_TYPES.contains(notification.getType())) {
                result = sendSmsSignLinkNotification(notification);
            } else if (PIN_CODE_SMS == notification.getType()) {
                result = sendSmsPinCodeNotification(notification);
            } else if (SIGN_LINK_CHAT == notification.getType()) {
                result = chatNotificationSender.sendDocumentSignatureRequestNotificationAndWait(notification);
            } else {
                throw new IllegalStateException("Unexpected notification type: " + notification.getType());
            }
            if (result) {
                logger.info("Signature request notification [{}] was sent", notification.getId());
                notification.setSentDatetime(Instant.now());
                signatureRequestNotificationDao.save(notification);
            } else {
                logger.info("Signature request notification [{}] wasn't sent", notification.getId());
            }
        } catch (Exception e) {
            logger.warn("Couldn't send signature request notification [{}]", notification.getId(), e);
        }
    }

    private boolean sendSmsPinCodeNotification(DocumentSignatureRequestNotification notification) {
        return smsService.sendSmsNotificationAndWait(
                notification.getPhoneNumber(),
                generatePinCodeNotificationMessage(notification)
        );
    }

    private boolean sendSmsSignLinkNotification(DocumentSignatureRequestNotification notification) {
        return smsService.sendSmsNotificationAndWait(
                notification.getPhoneNumber(),
                generateReviewAndSignNotificationMessage(notification)
        );
    }

    private boolean sendEmailSignLinkNotification(DocumentSignatureRequestNotification notification) {
        var dto = signatureRequestNotificationMailDtoConverter.convert(notification);
        return exchangeMailService.sendSignatureRequestNotificationAndWait(dto);
    }

    private String generatePinCodeNotificationMessage(DocumentSignatureRequestNotification notification) {

        var client = notification.getDocumentSignatureRequest().getClient();

        return String.format(
                SMS_PIN_CODE_NOTIFICATION_TEMPLATE,
                client.getCommunity().getName(),
                notification.getDocumentSignatureRequest().getPdcflowPinCode()
        );
    }

    private String generateReviewAndSignNotificationMessage(DocumentSignatureRequestNotification notification) {

        var signatureRequest = notification.getDocumentSignatureRequest();

        var requestedFromFullName = signatureRequest.getRequestedFromEmployee() != null
                ? signatureRequest.getRequestedFromEmployee().getFullName()
                : signatureRequest.getRequestedFromClient().getFullName();

        var client = signatureRequest.getClient();

        var signatureTemplate = signatureRequest.getSignatureTemplate();

        String url;
        if (notification.getType() == SIGN_LINK_SMS_TO_CLIENT) {
            url = signatureRequest.getPdcflowSignatureUrl();
        } else if (notification.getType() == SIGN_LINK_SMS_TO_CONTACT) {
            url = urlService.signatureRequestUrl(client.getId(), signatureRequest.getId());
        } else {
            throw new IllegalArgumentException("Unexpected notification type");
        }

        return String.format(
                SMS_REVIEW_AND_SIGN_NOTIFICATION_TEMPLATE,
                requestedFromFullName,
                client.getCommunity().getName(),
                signatureTemplate.getTitle(),
                urlShortenerService.getShortUrl(url)
        );
    }
}
