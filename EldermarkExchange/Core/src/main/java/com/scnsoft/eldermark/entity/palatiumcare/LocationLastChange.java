package com.scnsoft.eldermark.entity.palatiumcare;

import com.scnsoft.eldermark.shared.palatiumcare.EntityAction;
import javax.persistence.*;

@Entity
@Table(name = "Notify_ResidentLastChange")
@NamedQueries({
        @NamedQuery(
                name = "LocationLastResident.findRecordByLocationId",
                query = "from LocationLastChange record where record.locationId = :locationId"
        ),
        @NamedQuery(
                name = "LocationLastResident.findRecordsByLocationIds",
                query = "delete from LocationLastChange record where record.locationId in (:locationIds)"
        )
})
public class LocationLastChange extends BasicEntity {

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "json_location")
    private String jsonLocation;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.STRING)
    private EntityAction action;

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getJsonLocation() {
        return jsonLocation;
    }

    public void setJsonLocation(String jsonLocation) {
        this.jsonLocation = jsonLocation;
    }

    public EntityAction getAction() {
        return action;
    }

    public void setAction(EntityAction action) {
        this.action = action;
    }

    @Override
    public String toString() {
        return "LocationLastChange{" +
                "locationId=" + locationId +
                ", jsonLocation='" + jsonLocation + '\'' +
                ", action=" + action +
                '}';
    }
}
