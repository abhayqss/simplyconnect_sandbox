package com.scnsoft.eldermark.entity.careteam;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "LatestCareTeamMemberModified")
public class LatestCareTeamMemberModified extends CareTeamMemberModifiedBase {

    @Column(name = "read_by_employee_id", nullable = false, insertable = false, updatable = false)
    private Long readByEmployeeId;

    public Long getReadByEmployeeId() {
        return readByEmployeeId;
    }

    public void setReadByEmployeeId(Long readByEmployeeId) {
        this.readByEmployeeId = readByEmployeeId;
    }
}
