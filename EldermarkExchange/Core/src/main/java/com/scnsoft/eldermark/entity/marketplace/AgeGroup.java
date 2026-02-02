package com.scnsoft.eldermark.entity.marketplace;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author phomal
 * Created on 11/28/2017.
 */
@Immutable
@Entity
public class AgeGroup extends DisplayableNamedEntity {

    @Column(name = "display_order", nullable = false)
    private Long displayOrder;

    public Long getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Long displayOrder) {
        this.displayOrder = displayOrder;
    }

}
