package com.scnsoft.eldermark.entity.marketplace;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class DisplayableNamedKeyEntity extends DisplayableNamedEntity {

    @Column(name = "code")
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

}
