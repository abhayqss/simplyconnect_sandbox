package com.scnsoft.eldermark.service.document.signature.notification;

import com.scnsoft.eldermark.dao.signature.DocumentSignatureRequestNotificationDao;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequest;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotificationType;
import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;
import com.scnsoft.eldermark.exception.InternalServerException;
import com.scnsoft.eldermark.exception.InternalServerExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.notification.sender.SignatureNotificationSender;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.TemporalAmount;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SignatureNotificationServiceImpl implements SignatureNotificationService {

    private final static TemporalAmount RESEND_PIN_TIMEOUT = Duration.ofMinutes(5);

    @Autowired
    private DocumentSignatureRequestNotificationDao signatureRequestNotificationDao;

    @Autowired
    private SignatureNotificationSender signatureNotificationSender;

    @Override
    public void sendRequestSignatureNotification(DocumentSignatureRequest documentSignatureRequest) {
        if (documentSignatureRequest.getNotificationMethod() != null) {
            createNotifications(documentSignatureRequest).stream()
                    .map(signatureRequestNotificationDao::save)
                    .forEach(this::send);
        }
    }

    @Override
    public void sendRequestSignaturePinCodeSmsNotification(DocumentSignatureRequest request) {
        if (!isRecipientHasScAccount(request) && request.getPdcflowPinCode() != null) {
            var smsNotification = createPinCodeSmsNotification(request);
            signatureRequestNotificationDao.save(smsNotification);
            send(smsNotification);
        }
    }

    private DocumentSignatureRequestNotification createPinCodeSmsNotification(DocumentSignatureRequest request) {
        var pinNotification = createBaseNotification(request, SignatureRequestNotificationMethod.SMS);
        pinNotification.setType(DocumentSignatureRequestNotificationType.PIN_CODE_SMS);
        return pinNotification;
    }

    @Override
    public Optional<Instant> getCanResendPinTimeAt(DocumentSignatureRequest request) {

        if (isRecipientHasScAccount(request)) return Optional.empty();

        var previousPinSentTime = request.getNotifications().stream()
                .filter(it -> it.getType() == DocumentSignatureRequestNotificationType.PIN_CODE_SMS)
                .map(DocumentSignatureRequestNotification::getCreatedDatetime)
                .max(Comparator.naturalOrder());

        return Optional.of(
                previousPinSentTime.map(it -> it.plus(RESEND_PIN_TIMEOUT))
                        .orElse(Instant.EPOCH)
        );
    }

    @Override
    public Instant getCanResendPinTimeAt(DocumentSignatureRequestNotification previousNotification) {
        return previousNotification.getCreatedDatetime().plus(RESEND_PIN_TIMEOUT);
    }

    @Override
    public DocumentSignatureRequestNotification resendPin(DocumentSignatureRequest request) {

        if (isRecipientHasScAccount(request)) {
            throw new ValidationException("Signature should be requested from client without SimplyConnect account");
        }

        var timePassedSinceLastAttempt = getCanResendPinTimeAt(request)
                .map(it -> it.isBefore(Instant.now()))
                .orElse(false);

        if (timePassedSinceLastAttempt) {
            var notification = createBaseNotification(request, SignatureRequestNotificationMethod.SMS);
            notification.setType(DocumentSignatureRequestNotificationType.PIN_CODE_SMS);
            notification = signatureRequestNotificationDao.save(notification);
            send(notification.getId());
            return notification;
        } else {
            throw new ValidationException("Please allow 5 minutes for this code to arrive. Then you can request another code");
        }
    }

    private boolean isRecipientHasScAccount(DocumentSignatureRequest request) {
        return request.getRequestedFromClient() == null ||
                CollectionUtils.isNotEmpty(request.getRequestedFromClient().getAssociatedEmployeeIds());
    }

    private List<DocumentSignatureRequestNotification> createNotifications(DocumentSignatureRequest request) {

        var notificationMethod = request.getNotificationMethod();

        if (notificationMethod == SignatureRequestNotificationMethod.SIGN_NOW) {
            return List.of();
        }

        var signLinkNotification = createBaseNotification(request, request.getNotificationMethod());

        if (isRecipientHasScAccount(request)) {
            if (notificationMethod == SignatureRequestNotificationMethod.EMAIL) {
                signLinkNotification.setType(DocumentSignatureRequestNotificationType.SIGN_LINK_EMAIL_TO_CONTACT);
            } else if (notificationMethod == SignatureRequestNotificationMethod.SMS) {
                signLinkNotification.setType(DocumentSignatureRequestNotificationType.SIGN_LINK_SMS_TO_CONTACT);
            } else if (notificationMethod == SignatureRequestNotificationMethod.CHAT) {
                signLinkNotification.setType(DocumentSignatureRequestNotificationType.SIGN_LINK_CHAT);
            } else {
                throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
            }
            return List.of(signLinkNotification);
        } else {
            if (notificationMethod == SignatureRequestNotificationMethod.EMAIL) {
                signLinkNotification.setType(DocumentSignatureRequestNotificationType.SIGN_LINK_EMAIL_TO_CLIENT);
            } else if (notificationMethod == SignatureRequestNotificationMethod.SMS) {
                signLinkNotification.setType(DocumentSignatureRequestNotificationType.SIGN_LINK_SMS_TO_CLIENT);
            } else if (notificationMethod == SignatureRequestNotificationMethod.CHAT) {
                signLinkNotification.setType(DocumentSignatureRequestNotificationType.SIGN_LINK_CHAT);
            } else {
                throw new InternalServerException(InternalServerExceptionType.NOT_IMPLEMENTED);
            }
            return List.of(signLinkNotification);
        }
    }

    private DocumentSignatureRequestNotification createBaseNotification(
            DocumentSignatureRequest request,
            SignatureRequestNotificationMethod notificationMethod
    ) {
        var notification = new DocumentSignatureRequestNotification();
        notification.setCreatedDatetime(Instant.now());
        notification.setDocumentSignatureRequest(request);
        notification.setNotificationMethod(notificationMethod);

        if (notificationMethod == SignatureRequestNotificationMethod.SMS) {
            if (StringUtils.isNotEmpty(request.getPhoneNumber())) {
                notification.setPhoneNumber(request.getPhoneNumber());
            } else {
                throw new ValidationException("Phone number is invalid");
            }
        } else if (notificationMethod == SignatureRequestNotificationMethod.EMAIL) {
            if (StringUtils.isNotEmpty(request.getEmail())) {
                notification.setEmail(request.getEmail());
            } else {
                throw new ValidationException("Email is invalid");
            }
        }

        return notification;
    }

    private void send(DocumentSignatureRequestNotification notification) {
        send(notification.getId());
    }

    private void send(Long id) {
        signatureNotificationSender.send(id);
    }
}
