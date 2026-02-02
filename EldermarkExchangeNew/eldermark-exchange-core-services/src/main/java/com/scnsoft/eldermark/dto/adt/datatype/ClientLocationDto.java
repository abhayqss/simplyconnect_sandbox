package com.scnsoft.eldermark.dto.adt.datatype;

//todo rename to PatientLocationDto
public class ClientLocationDto {

    //if renaming of any field is needed - please make sure to do the same changes to #displayPL in eventNotificationSecureEmail.vm

    private String pointOfCare;
    private String room;
    private String bed;
    private HDHierarchicDesignatorDto facility;
    private String locationStatus;
    private String personLocationStatus;
    private String building;
    private String floor;
    private String locationDescription;

    public String getPointOfCare() {
        return pointOfCare;
    }

    public void setPointOfCare(String pointOfCare) {
        this.pointOfCare = pointOfCare;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getBed() {
        return bed;
    }

    public void setBed(String bed) {
        this.bed = bed;
    }

    public HDHierarchicDesignatorDto getFacility() {
        return facility;
    }

    public void setFacility(HDHierarchicDesignatorDto facility) {
        this.facility = facility;
    }

    public String getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(String locationStatus) {
        this.locationStatus = locationStatus;
    }

    public String getPersonLocationStatus() {
        return personLocationStatus;
    }

    public void setPersonLocationStatus(String personLocationStatus) {
        this.personLocationStatus = personLocationStatus;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }
}
