package com.scnsoft.eldermark.dao.specification;

import com.scnsoft.eldermark.entity.NotificationType;
import com.scnsoft.eldermark.entity.video.ConversationNotification;
import com.scnsoft.eldermark.entity.video.ConversationNotificationType;
import com.scnsoft.eldermark.entity.video.ConversationNotification_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class ConversationNotificationSpecificationGenerator {

    public Specification<ConversationNotification> byEmployeeIdAndConversation(Long employeeId, String conversationSid) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get(ConversationNotification_.employeeId), employeeId),
                criteriaBuilder.equal(root.get(ConversationNotification_.twilioConversationSid), conversationSid)
        );
    }

    public Specification<ConversationNotification> notFailed() {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.isFalse(root.get(ConversationNotification_.isFail));
    }

    public Specification<ConversationNotification> createdAfter(Instant when) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.greaterThan(root.get(ConversationNotification_.createdDatetime), when);
    }

    public Specification<ConversationNotification> byChannel(NotificationType channel) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(ConversationNotification_.channel), channel);
    }

    public Specification<ConversationNotification> byType(ConversationNotificationType type) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(ConversationNotification_.type), type);
    }

}

