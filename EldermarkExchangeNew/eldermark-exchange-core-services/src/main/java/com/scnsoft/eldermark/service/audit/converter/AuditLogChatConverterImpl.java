package com.scnsoft.eldermark.service.audit.converter;

import com.scnsoft.eldermark.beans.audit.AuditLogActivity;
import com.scnsoft.eldermark.entity.audit.AuditLog;
import com.scnsoft.eldermark.entity.audit.AuditLogType;
import com.scnsoft.eldermark.service.audit.AuditLogBaseConverter;
import com.scnsoft.eldermark.service.twilio.ChatService;
import com.scnsoft.eldermark.util.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.util.Collections;
import java.util.List;

@Component
@Transactional(readOnly = true)
public class AuditLogChatConverterImpl implements AuditLogBaseConverter<String> {

    private static final List<AuditLogActivity> chatActivitiesWithNote = List.of(
            AuditLogActivity.CHAT_CREATE,
            AuditLogActivity.CHAT_PARTICIPANT_UPDATE,
            AuditLogActivity.CALL_START
    );

    @Autowired
    private ChatService chatService;

    @Override
    public List<String> convertNotes(AuditLogActivity activity, AuditLog auditLog, List<String> relatedIds, List<String> relatedAdditionalFields, ZoneId zoneId) {
        if (!CollectionUtils.isEmpty(relatedIds)) {
            var relatedId = relatedIds.get(0);

            if (auditLog.isMobile()) {
                long participantsCount = getParticipantsCount(relatedId);

                if (participantsCount > 2) {
                    if (AuditLogActivity.CALL_START.equals(activity)) {
                        return List.of("Group call\nMobile app");
                    }
                    return List.of("Group chat\nMobile app");
                }
                return List.of("Mobile app");
            }

            if (chatActivitiesWithNote.contains(activity)) {
                long participantsCount = getParticipantsCount(relatedId);

                if (participantsCount > 2) {
                    if (AuditLogActivity.CALL_START.equals(activity)) {
                        return List.of("Group call");
                    }
                    return List.of("Group chat");
                }
            }
        }

        return Collections.emptyList();
    }

    private long getParticipantsCount(String relatedId) {
        var chatParticipants = chatService.getChatParticipants(relatedId);
        return StreamUtils.stream(chatParticipants).count();
    }

    @Override
    public AuditLogType getConverterType() {
        return AuditLogType.CHAT;
    }
}
