package com.scnsoft.eldermark.entity.inbound.healthpartners;

import com.fasterxml.jackson.annotation.JsonView;

public class HpTermedMemberProcessingSummary extends HpRecordProcessingSummary {

    @JsonView(LocalView.class)
    private Long clientId;

    @JsonView(LocalView.class)
    private Boolean clientIsNew;


    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Boolean getClientIsNew() {
        return clientIsNew;
    }

    public void setClientIsNew(Boolean clientIsNew) {
        this.clientIsNew = clientIsNew;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return clientId != null;
    }

    @Override
    protected String buildWarnMessage() {
        return "Could not set member as termed";
    }
}
