package com.scnsoft.eldermark.api.shared.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Care Team Member invitation status
 *
 * @author phomal
 * Created on 5/3/2017.
 */
public enum InvitationStatus {
    PENDING("PENDING"),
    ACTIVE("ACTIVE"),
    EXPIRED("EXPIRED"),
    DECLINED("DECLINED");

    private final String value;

    InvitationStatus(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return value;
    }

    @JsonCreator
    public static InvitationStatus fromValue(String text) {
        for (InvitationStatus b : InvitationStatus.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}


