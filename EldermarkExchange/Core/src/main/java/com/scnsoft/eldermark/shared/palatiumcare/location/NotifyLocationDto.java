package com.scnsoft.eldermark.shared.palatiumcare.location;


import com.scnsoft.eldermark.shared.palatiumcare.building.NotifyBuildingDto;

public class NotifyLocationDto {

    private Long id;

    private String room;

    private NotifyBuildingDto building;

    public NotifyLocationDto() {}

    public NotifyLocationDto(String room, NotifyBuildingDto building) {
        this.room = room;
        this.building = building;
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


    @Override
    public String toString() {
        return "PalCareLocationDto{" +
                "id=" + id +
                ", room='" + room + '\'' +
                ", building='" + building + '\'' +
                '}';
    }

}
