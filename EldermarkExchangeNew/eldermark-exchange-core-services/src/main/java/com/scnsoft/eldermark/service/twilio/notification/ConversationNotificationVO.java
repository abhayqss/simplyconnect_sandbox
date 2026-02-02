package com.scnsoft.eldermark.service.twilio.notification;

import com.scnsoft.eldermark.beans.twilio.chat.MediaMessageCallbackListItem;
import com.scnsoft.eldermark.dto.notification.PushNotificationVO;

import java.util.List;

public class ConversationNotificationVO {

    private Long notificationId;
    private List<MediaMessageCallbackListItem> messageMedia;
    private Boolean isVoiceMessage;

    private PushNotificationVO preparedPushNotificationVO;

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public List<MediaMessageCallbackListItem> getMessageMedia() {
        return messageMedia;
    }

    public void setMessageMedia(List<MediaMessageCallbackListItem> messageMedia) {
        this.messageMedia = messageMedia;
    }

    public PushNotificationVO getPreparedPushNotificationVO() {
        return preparedPushNotificationVO;
    }

    public void setPreparedPushNotificationVO(PushNotificationVO preparedPushNotificationVO) {
        this.preparedPushNotificationVO = preparedPushNotificationVO;
    }

    public Boolean getIsVoiceMessage() {
        return isVoiceMessage;
    }

    public void setIsVoiceMessage(Boolean isVoiceMessage) {
        this.isVoiceMessage = isVoiceMessage;
    }
}
