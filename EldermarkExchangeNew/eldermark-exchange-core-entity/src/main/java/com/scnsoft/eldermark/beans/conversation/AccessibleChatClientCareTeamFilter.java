package com.scnsoft.eldermark.beans.conversation;

import javax.validation.constraints.NotNull;

public class AccessibleChatClientCareTeamFilter extends BaseConversationAccessibilityFilter {

    @NotNull
    private Long clientId;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
