package com.scnsoft.eldermark.entity.palatiumcare;

import com.scnsoft.eldermark.shared.palatiumcare.EntityAction;
import javax.persistence.*;

@Entity
@Table(name = "Notify_ResidentLastChange")
@NamedQueries({
        @NamedQuery(
                name = "ResidentLastChange.findRecordByResidentId",
                query = "from ResidentLastChange record where record.residentId = :residentId"
        ),
        @NamedQuery(
                name = "ResidentLastChange.removeRecordsByResidentIds",
                query = "delete from ResidentLastChange record where record.residentId in (:residentIds)"
        )
})
public class ResidentLastChange extends BasicEntity {

    @Column(name = "resident_id", nullable = false)
    private Long residentId;

    @Column(name = "json_resident")
    private String jsonResident;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityAction action;

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public String getJsonResident() {
        return jsonResident;
    }

    public void setJsonResident(String jsonResident) {
        this.jsonResident = jsonResident;
    }

    public EntityAction getAction() {
        return action;
    }

    public void setAction(EntityAction action) {
        this.action = action;
    }
}
