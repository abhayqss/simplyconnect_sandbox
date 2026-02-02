package com.scnsoft.eldermark.shared.palatiumcare.location;

import com.scnsoft.eldermark.shared.palatiumcare.building.NotifyBuildingDto;

public class PalCareLocationInDto {

    private Long palCareId;

    private String room;

    private NotifyBuildingDto building;

    public PalCareLocationInDto () {}

    public PalCareLocationInDto(Long palCareId, String room, NotifyBuildingDto building) {
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

    public NotifyBuildingDto getBuilding() {
        return building;
    }

    public void setBuilding(NotifyBuildingDto building) {
        this.building = building;
    }

    @Override
    public String toString() {
        return "PalCareLocationInDto{" +
                "palCareId=" + palCareId +
                ", room='" + room + '\'' +
                ", building='" + building + '\'' +
                '}';
    }
}
