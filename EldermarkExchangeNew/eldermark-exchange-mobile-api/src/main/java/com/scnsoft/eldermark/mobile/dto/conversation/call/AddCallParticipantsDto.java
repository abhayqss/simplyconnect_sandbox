package com.scnsoft.eldermark.mobile.dto.conversation.call;

public class AddCallParticipantsDto extends AddAlreadyInChatCallParticipantsDto {

    private Long participatingClientId;

    public Long getParticipatingClientId() {
        return participatingClientId;
    }

    public void setParticipatingClientId(Long participatingClientId) {
        this.participatingClientId = participatingClientId;
    }
}
