package com.scnsoft.eldermark.api.external.entity;

import com.scnsoft.eldermark.api.shared.entity.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Physician category aka speciality (e.g. "Primary Care Doctor").
 */
@Entity
@Table(name = "PhysicianCategory")
public class PhysicianCategory extends BaseEntity {

    @Column(name = "display_name")
    private String displayName;

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        PhysicianCategory that = (PhysicianCategory) o;

        return getDisplayName() != null ? getDisplayName().equals(that.getDisplayName()) : that.getDisplayName() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getDisplayName() != null ? getDisplayName().hashCode() : 0);
        return result;
    }

}
