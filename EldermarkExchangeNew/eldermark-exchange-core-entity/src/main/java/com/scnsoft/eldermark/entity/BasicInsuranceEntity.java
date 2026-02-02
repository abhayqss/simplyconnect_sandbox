package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedKeyEntity;

@MappedSuperclass
public class BasicInsuranceEntity extends DisplayableNamedKeyEntity {

    @Column(name = "is_popular")
    private Boolean isPopular;

    public Boolean getPopular() {
        return isPopular;
    }

    public void setPopular(Boolean popular) {
        isPopular = popular;
    }
}
