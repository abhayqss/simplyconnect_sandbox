package com.scnsoft.eldermark.entity.basic;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class DisplayableNamedCodedOrderedEntity extends DisplayableNamedCodedEntity {

    @Column(name = "\"order\"", nullable = false, columnDefinition = "int")
    private long order;

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }
}
