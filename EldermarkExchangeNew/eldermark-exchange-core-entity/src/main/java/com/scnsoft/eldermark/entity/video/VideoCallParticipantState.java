package com.scnsoft.eldermark.entity.video;

public enum VideoCallParticipantState {
    //added to who is calling.
    OUTGOING_CALL,

    //in this state user sees 'incoming call' screen
    INCOMING_CALL,  //start caused by - who calls

    //when user is added to existing call. logic is the same as INCOMING_CALL.
    //Added just to distinguish initial people on call with added
    NEW_MEMBER_INCOMING_CALL, //start caused by - who calls

    //user is on the call
    ON_CALL,

}
