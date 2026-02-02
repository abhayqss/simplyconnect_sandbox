package com.scnsoft.eldermark.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Immutable
@Entity
@Table(name = "Resident", uniqueConstraints = @UniqueConstraint(columnNames = {"legacy_id", "database_id"}))
@AttributeOverrides({
        @AttributeOverride(name = "legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25)),
        @AttributeOverride(name = "legacyTable", column = @Column(name = "legacy_table", nullable = true, length = 100))
})
public class ResidentLight extends StringLegacyTableAwareEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "facility_id")
    private Organization facility;

    public ResidentLight() {
    }

    public Organization getFacility() {
        return facility;
    }

    public void setFacility(Organization facility) {
        this.facility = facility;
    }

}
