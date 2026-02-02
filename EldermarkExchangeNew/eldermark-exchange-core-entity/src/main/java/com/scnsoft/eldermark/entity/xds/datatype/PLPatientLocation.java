package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PL_PatientLocation")
public class PLPatientLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "point_of_care")
    private String pointOfCare;

    @Column(name = "room")
    private String room;

    @Column(name = "bed")
    private String bed;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    private HDHierarchicDesignator facility;

    @Column(name = "location_status")
    private String locationStatus;

    @Column(name = "person_location_type")
    private String personLocationType;

    @Column(name = "building")
    private String building;

    @Column(name = "floor")
    private String floor;

    @Column(name = "location_description")
    private String locationDescription;

    public PLPatientLocation() {
    }

    public PLPatientLocation(String pointOfCare, String room, String bed, HDHierarchicDesignator facility, String locationStatus, String personLocationType, String building, String floor, String locationDescription) {
        this.pointOfCare = pointOfCare;
        this.room = room;
        this.bed = bed;
        this.facility = facility;
        this.locationStatus = locationStatus;
        this.personLocationType = personLocationType;
        this.building = building;
        this.floor = floor;
        this.locationDescription = locationDescription;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public HDHierarchicDesignator getFacility() {
        return facility;
    }

    public void setFacility(HDHierarchicDesignator facility) {
        this.facility = facility;
    }

    public String getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(String locationStatus) {
        this.locationStatus = locationStatus;
    }

    public String getPersonLocationType() {
        return personLocationType;
    }

    public void setPersonLocationType(String personLocationType) {
        this.personLocationType = personLocationType;
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
