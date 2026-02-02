package com.scnsoft.eldermark.entity.palatiumcare;

import javax.persistence.*;

@Entity(name = "NotifyLocation")
@Table(name = "PalCare_Location")
@NamedQueries({
        @NamedQuery(
                name = "NotifyLocation.findLocationByPalCareId",
                query = "from NotifyLocation ntfLocation where ntfLocation.palCareId = :palCareId"
        )
})
public class Location extends BasicEntity  {

    @Column(name = "pal_care_id", unique = true)
    private Long palCareId;

    @Column(name = "name", nullable = false)
    private String room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id", referencedColumnName = "id")
    private Building building;

    public Location() {}

    public Location(String room, Building building) {
        this.room = room;
        this.building = building;
    }

    public Location(Long palCareId, String room, Building building) {
        this.palCareId = palCareId;
        this.room = room;
        this.building = building;
    }

    public Long getPalCareId() {
        return palCareId;
    }

    public void setPalCareId(Long palCareId) {
        this.palCareId = palCareId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

}