package com.scnsoft.eldermark.entity.xds.datatype;

import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "PL_PatientLocation")
public class PLPatientLocation implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    //@Nationalized
    @Column(name = "point_of_care")
    private String pointOfCare;

    //@Nationalized
    @Column(name = "room")
    private String room;

    //@Nationalized
    @Column(name = "bed")
    private String bed;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id", referencedColumnName = "id")
    private HDHierarchicDesignator facility;

    //@Nationalized
    @Column(name = "location_status")
    private String locationStatus;

    //@Nationalized
    @Column(name = "person_location_type")
    private String personLocationType;

    //@Nationalized
    @Column(name = "building")
    private String building;

    //@Nationalized
    @Column(name = "floor")
    private String floor;

    //@Nationalized
    @Column(name = "location_description")
    private String locationDescription;


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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
