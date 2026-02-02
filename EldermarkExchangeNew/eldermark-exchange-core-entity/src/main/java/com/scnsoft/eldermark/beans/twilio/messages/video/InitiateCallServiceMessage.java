package com.scnsoft.eldermark.beans.twilio.messages.video;


import com.scnsoft.eldermark.beans.projection.NamesAware;
import com.scnsoft.eldermark.beans.twilio.messages.ServiceMessageType;
import com.scnsoft.eldermark.beans.twilio.user.IdentityListItemDto;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InitiateCallServiceMessage extends VideoCallServiceMessage {

    private static final int MAX_CALL_NAME_LEN = 256;

    private IdentityListItemDto caller;
    private List<IdentityListItemDto> callees;
    private String conversationSid;
    private String conversationFriendlyName;

    private String roomAccessToken;

    public InitiateCallServiceMessage(String roomSid) {
        super(ServiceMessageType.INITIATE_CALL, roomSid);
    }

    public IdentityListItemDto getCaller() {
        return caller;
    }

    public void setCaller(IdentityListItemDto caller) {
        this.caller = caller;
    }

    public List<IdentityListItemDto> getCallees() {
        return callees;
    }

    public void setCallees(List<IdentityListItemDto> callees) {
        this.callees = callees;
    }

    public String getConversationSid() {
        return conversationSid;
    }

    public void setConversationSid(String conversationSid) {
        this.conversationSid = conversationSid;
    }

    public String getConversationFriendlyName() {
        return conversationFriendlyName;
    }

    public void setConversationFriendlyName(String conversationFriendlyName) {
        this.conversationFriendlyName = conversationFriendlyName;
    }

    public String getRoomAccessToken() {
        return roomAccessToken;
    }

    public void setRoomAccessToken(String roomAccessToken) {
        this.roomAccessToken = roomAccessToken;
    }

    @Override
    public String toString() {
        return "InitiateCallServiceMessage{" +
                "caller='" + caller + '\'' +
                ", callees=" + callees +
                ", conversationSid='" + conversationSid + '\'' +
                ", roomAccessToken='" + Optional.ofNullable(roomAccessToken).map(x -> "###PROTECTED###").orElse(null) + '\'' +
                ", roomSid='" + roomSid + '\'' +
                ", type=" + type +
                ", conversationFriendlyName='" + conversationFriendlyName + '\'' +
                '}';
    }

    @Override
    public Map<String, String> toPushNotificationData(String recipientIdentity) {
        if (recipientIdentity.equals(caller.getIdentity())) {
            return Collections.emptyMap();
        }

        var map = super.toPushNotificationData(recipientIdentity);
        map.put("roomAccessToken", roomAccessToken);
        map.put("conversationSid", conversationSid);
        String callName;

        if (StringUtils.isNotEmpty(conversationFriendlyName)) {
            callName = conversationFriendlyName;
        } else {
            if (callees.size() == 1) {
                callName = caller.getFullName();
            } else {
                callName = Stream.concat(Stream.of(caller), callees.stream())
                        .filter(it -> !Objects.equals(recipientIdentity, it.getIdentity()))
                        .filter(IdentityListItemDto::getCanCall)
                        .map(NamesAware::getFullName)
                        .sorted()
                        .collect(Collectors.joining(", "));
                callName = callName.substring(0, Math.min(MAX_CALL_NAME_LEN, callName.length()));
            }
        }
        map.put("callName", callName);
        return map;
    }
}
