package com.scnsoft.eldermark.service.healthpartners.ctx;

import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;

import java.util.HashSet;
import java.util.Set;

public class ClaimProcessingContext {

    private boolean clientIsNewHint;
    private Set<ResidentUpdateType> updateTypes = new HashSet<>();

    public boolean isClientIsNewHint() {
        return clientIsNewHint;
    }

    public void setClientIsNewHint(boolean clientIsNewHint) {
        this.clientIsNewHint = clientIsNewHint;
    }

    public Set<ResidentUpdateType> getUpdateTypes() {
        return updateTypes;
    }

    public void setUpdateTypes(Set<ResidentUpdateType> updateTypes) {
        this.updateTypes = updateTypes;
    }
}
