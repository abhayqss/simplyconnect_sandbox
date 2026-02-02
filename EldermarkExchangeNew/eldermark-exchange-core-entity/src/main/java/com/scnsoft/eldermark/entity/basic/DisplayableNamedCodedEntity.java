package com.scnsoft.eldermark.entity.basic;

import javax.persistence.*;

@MappedSuperclass
public class DisplayableNamedCodedEntity extends DisplayableNamedEntity {

    @Column(name = "code", nullable = false)
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
