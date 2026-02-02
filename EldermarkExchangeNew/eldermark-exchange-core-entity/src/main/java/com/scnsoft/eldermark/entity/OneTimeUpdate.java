package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "OneTimeUpdate")
public class OneTimeUpdate {

    @Id
    @Column(name = "update_name", insertable = false)
    private String updateName;

    @Column(name = "applied_at")
    private Instant appliedAt;

    @Column(name = "apply_ordering", insertable = false, updatable = false)
    private int applyOrdering;

    public String getUpdateName() {
        return updateName;
    }

    public void setUpdateName(String updateName) {
        this.updateName = updateName;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Instant appliedAt) {
        this.appliedAt = appliedAt;
    }

    public int getApplyOrdering() {
        return applyOrdering;
    }

    public void setApplyOrdering(int applyOrdering) {
        this.applyOrdering = applyOrdering;
    }
}
