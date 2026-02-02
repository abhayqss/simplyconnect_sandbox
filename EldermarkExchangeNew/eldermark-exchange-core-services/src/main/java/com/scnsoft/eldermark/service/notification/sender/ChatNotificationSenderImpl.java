package com.scnsoft.eldermark.service.notification.sender;

import com.scnsoft.eldermark.converter.signature.SignatureRequestNotificationChatDtoConverter;
import com.scnsoft.eldermark.dto.notification.signature.SignatureRequestNotificationChatDto;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureRequestNotification;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.UrlService;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.service.twilio.ConversationUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class ChatNotificationSenderImpl implements ChatNotificationSender {

    private final static Logger logger = LoggerFactory.getLogger(ChatNotificationSenderImpl.class);

    @Autowired
    private SignatureRequestNotificationChatDtoConverter signatureRequestNotificationChatDtoConverter;

    @Autowired
    @Lazy
    private ChatService chatService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UrlService urlService;

    private static final String CHAT_NOTIFICATION_TEMPLATE = "Dear %s,\n\n" +
            "Your document %s %s is ready for review.\n" +
            "For your convenience, this document is being delivered in a format that allows for you " +
            "to review/sign the document electronically.\n\n" +
            "%s" +
            "Review Document: %s";

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean sendDocumentSignatureRequestNotificationAndWait(DocumentSignatureRequestNotification signatureRequestNotification) {
        try {
            var documentSignatureRequest = signatureRequestNotification.getDocumentSignatureRequest();

            var authorId = documentSignatureRequest.getRequestedById();
            var recipientId = documentSignatureRequest.getRequestedFromEmployeeId();
            if (recipientId == null) {
                recipientId = resolveAssociatedContact(documentSignatureRequest.getRequestedFromClientId());
            }
            var conversationSid = chatService.getPersonalOrCreateConversation(
                    Set.of(authorId, recipientId),
                    authorId,
                    documentSignatureRequest.getRequestedFromClientId()
            );

            signatureRequestNotification.setConversationSid(conversationSid);

            var signatureRequestNotificationChatDto =
                    signatureRequestNotificationChatDtoConverter.convert(signatureRequestNotification);

            var message = chatService.sendTextMessageWithLinks(
                    conversationSid,
                    ConversationUtils.employeeIdToIdentity(authorId),
                    generateMessageText(signatureRequestNotificationChatDto)
            );

            return StringUtils.isNotEmpty(message.getSid());
        } catch (Exception e) {
            logger.error("Error send chat notification", e);
            return false;
        }
    }

    private Long resolveAssociatedContact(Long requestedFromClientId) {
        return clientService.findById(requestedFromClientId).getAssociatedEmployee().getId();
    }

    private String generateMessageText(SignatureRequestNotificationChatDto dto) {
        return String.format(CHAT_NOTIFICATION_TEMPLATE,
            dto.getRecipientName(),
            dto.getCommunityName(),
            dto.getTemplateName(),
            StringUtils.isBlank(dto.getMessage()) ? "" : dto.getMessage() + "\n\n",
            urlService.signatureRequestUrl(dto.getClientId(), dto.getRequestId())
        );
    }
}
