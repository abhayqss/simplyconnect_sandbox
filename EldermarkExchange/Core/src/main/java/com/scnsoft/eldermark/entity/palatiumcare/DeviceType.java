package com.scnsoft.eldermark.entity.palatiumcare;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "NotifyDeviceType")
@Table(name = "PalCare_DeviceType")
public class DeviceType extends BasicEntity {

    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
