package com.scnsoft.eldermark.entity.palatiumcare;

import com.scnsoft.eldermark.shared.palatiumcare.EntityAction;

import javax.persistence.*;

@Entity
@Table(name = "Notify_FacilityLastChange")
@NamedQueries({
        @NamedQuery(
                name = "LocationLastFacility.findRecordByFacilityId",
                query = "from FacilityLastChange record where record.facilityId = :facilityId"
        ),
        @NamedQuery(
                name = "LocationLastFacility.findRecordsByFacilityIds",
                query = "delete from FacilityLastChange record where record.facilityId in (:facilityIds)"
        )
})
public class FacilityLastChange extends BasicEntity {

    @Column(name = "facility_id", nullable = false)
    private Long facilityId;

    @Column(name = "json_facility")
    private String jsonFacility;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityAction action;

    public Long getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(Long facilityId) {
        this.facilityId = facilityId;
    }

    public String getJsonFacility() {
        return jsonFacility;
    }

    public void setJsonFacility(String jsonFacility) {
        this.jsonFacility = jsonFacility;
    }

    public EntityAction getAction() {
        return action;
    }

    public void setAction(EntityAction action) {
        this.action = action;
    }
}
