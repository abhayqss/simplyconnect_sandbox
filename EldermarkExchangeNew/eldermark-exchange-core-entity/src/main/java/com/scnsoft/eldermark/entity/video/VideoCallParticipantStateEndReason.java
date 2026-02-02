package com.scnsoft.eldermark.entity.video;

public enum VideoCallParticipantStateEndReason {
    //end reasons for OUTGOING_CALL INCOMING_CALL NEW_MEMBER_INCOMING_CALL
    CALL_ACCEPTED,
    CALL_DECLINED,
    CALL_CANCELLED,
    BUSY_CALLEE,

    //end reasons for OUTGOING_CALL
    CALL_TIMEOUT,

    //end reasons for INCOMING_CALL NEW_MEMBER_INCOMING_CALL
    CALL_MISSED,

    //end reasons for ON_CALL
    PARTICIPANT_REMOVED,    //end caused by - who removed
    PARTICIPANT_LEFT,       //end caused by - self
    CALL_END,               //end caused by - null

}
