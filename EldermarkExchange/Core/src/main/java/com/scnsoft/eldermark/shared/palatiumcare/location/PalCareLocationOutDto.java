package com.scnsoft.eldermark.shared.palatiumcare.location;

import com.scnsoft.eldermark.shared.palatiumcare.EntityAction;
import com.scnsoft.eldermark.shared.palatiumcare.building.NotifyBuildingDto;

public class PalCareLocationOutDto {

    private Long id;

    private String room;

    private NotifyBuildingDto building;

    private EntityAction entityAction;

    public PalCareLocationOutDto () {}

    public PalCareLocationOutDto(Long id, String room, NotifyBuildingDto building) {
        this.id = id;
        this.room = room;
        this.building = building;
    }

    public PalCareLocationOutDto(Long id, String room, NotifyBuildingDto building, EntityAction entityAction) {
        this.id = id;
        this.room = room;
        this.building = building;
        this.entityAction = entityAction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public NotifyBuildingDto getBuilding() {
        return building;
    }

    public void setBuilding(NotifyBuildingDto building) {
        this.building = building;
    }

    public EntityAction getEntityAction() {
        return entityAction;
    }

    public void setEntityAction(EntityAction entityAction) {
        this.entityAction = entityAction;
    }

    @Override
    public String toString() {
        return "PalCareLocationOutDto{" +
                "id=" + id +
                ", room='" + room + '\'' +
                ", building='" + building + '\'' +
                ", entityAction=" + entityAction +
                '}';
    }
}
