package com.scnsoft.eldermark.beans.security.projection.entity;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;

public interface ClientMedicationSecurityAwareEntity extends ClientIdAware {
    Boolean getIsManuallyCreated();

    static ClientMedicationSecurityAwareEntity of(Long clientId) {
        return new ClientMedicationSecurityAwareEntity() {
            @Override
            public Boolean getIsManuallyCreated() {
                return null;
            }

            @Override
            public Long getClientId() {
                return clientId;
            }
        };
    }
}
