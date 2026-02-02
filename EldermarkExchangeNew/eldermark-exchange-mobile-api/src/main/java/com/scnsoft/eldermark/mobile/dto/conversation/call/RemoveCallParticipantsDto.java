package com.scnsoft.eldermark.mobile.dto.conversation.call;

import java.util.Set;

public class RemoveCallParticipantsDto extends CallRoomDto {

    private Set<String> identities;

    public Set<String> getIdentities() {
        return identities;
    }

    public void setIdentities(Set<String> identities) {
        this.identities = identities;
    }
}
