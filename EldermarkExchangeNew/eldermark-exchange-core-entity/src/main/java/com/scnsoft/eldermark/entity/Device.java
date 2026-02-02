package com.scnsoft.eldermark.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column(name = "device_id")
    private String deviceId;

    @ManyToOne
    @JoinColumn(name = "resident_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "device_type_id")
    private MedicalDeviceType deviceTypeId;

    @Column(name = "created_on")
    private Date createdOn;

    @Column(name = "updated_on")
    private Date updatedOn;

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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public MedicalDeviceType getDeviceTypeId() {
        return deviceTypeId;
    }

    public void setDeviceTypeId(MedicalDeviceType deviceTypeId) {
        this.deviceTypeId = deviceTypeId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getUpdatedOn() {
        return updatedOn;
    }

    public void setUpdatedOn(Date updatedOn) {
        this.updatedOn = updatedOn;
    }
}
