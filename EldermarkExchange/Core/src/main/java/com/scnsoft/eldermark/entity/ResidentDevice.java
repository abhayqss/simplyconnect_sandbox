package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "Device")
public class ResidentDevice implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name="device_id")
    private String deviceId;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private CareCoordinationResident resident;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public CareCoordinationResident getResident() {
        return resident;
    }

    public void setResident(CareCoordinationResident resident) {
        this.resident = resident;
    }
}
