package com.scnsoft.eldermark.entity.basic;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class DisplayableNamedKeyEntity extends DisplayableNamedEntity{

	@Column(name = "code")
    private String key;

    public DisplayableNamedKeyEntity() {
    }

    public DisplayableNamedKeyEntity(Long id, String displayName, String key) {
        super(id, displayName);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
