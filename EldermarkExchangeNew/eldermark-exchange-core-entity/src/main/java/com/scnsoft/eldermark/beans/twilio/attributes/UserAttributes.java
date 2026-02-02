package com.scnsoft.eldermark.beans.twilio.attributes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UserAttributes {

    private Set<String> activeCallConversationSids;

    public Set<String> getActiveCallConversationSids() {
        return activeCallConversationSids;
    }

    public void setActiveCallConversationSids(Set<String> activeCallConversationSids) {
        this.activeCallConversationSids = activeCallConversationSids;
    }
}
