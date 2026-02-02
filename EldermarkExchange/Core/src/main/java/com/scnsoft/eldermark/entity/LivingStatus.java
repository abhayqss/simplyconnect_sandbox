package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "LivingStatus",
       uniqueConstraints = @UniqueConstraint(columnNames = {"legacy_id", "database_id"}))
public class LivingStatus extends LegacyIdAwareEntity {

    @Column(name="description", length = 40)
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
