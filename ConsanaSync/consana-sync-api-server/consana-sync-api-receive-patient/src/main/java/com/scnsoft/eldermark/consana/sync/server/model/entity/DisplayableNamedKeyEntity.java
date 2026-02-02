package com.scnsoft.eldermark.consana.sync.server.model.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DisplayableNamedKeyEntity {

    @Column(name = "code")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
