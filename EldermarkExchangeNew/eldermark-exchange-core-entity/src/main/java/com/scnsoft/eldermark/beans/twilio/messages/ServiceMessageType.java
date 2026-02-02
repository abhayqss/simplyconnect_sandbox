package com.scnsoft.eldermark.beans.twilio.messages;

public enum ServiceMessageType {
    INITIATE_CALL,
    CALL_MEMBER_DECLINED,
    CALL_MEMBERS_TIMEOUT,
    CALL_END,
    CALL_CONVERSATION_UPDATED,
    CALL_PENDING_MEMBERS,

    CALL_MEMBER_JOINED,
    CALL_MEMBER_LEFT,
    CALL_MEMBER_REMOVED,

    CALL_MEMBER_MUTE
}
