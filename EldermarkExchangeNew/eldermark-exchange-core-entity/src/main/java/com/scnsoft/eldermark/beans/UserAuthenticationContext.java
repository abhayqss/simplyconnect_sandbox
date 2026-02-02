package com.scnsoft.eldermark.beans;

import java.util.Set;

public class UserAuthenticationContext {
    private Long id;
    private Set<Long> clientRecordSearchFoundIds;
    private String accessibleRoomSid;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getClientRecordSearchFoundIds() {
        return clientRecordSearchFoundIds;
    }

    public void setClientRecordSearchFoundIds(Set<Long> clientRecordSearchFoundIds) {
        this.clientRecordSearchFoundIds = clientRecordSearchFoundIds;
    }

    public String getAccessibleRoomSid() {
        return accessibleRoomSid;
    }

    public void setAccessibleRoomSid(String accessibleRoomSid) {
        this.accessibleRoomSid = accessibleRoomSid;
    }
}
