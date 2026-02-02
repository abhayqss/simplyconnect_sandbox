package com.scnsoft.eldermark.entity.palatiumcare;

import javax.persistence.*;

@Entity(name = "NotifyDevice")
@Table(name = "PalCare_Device")
public class Device extends BasicEntity  {

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "device_type_id", foreignKey = @ForeignKey(name = "FK_Device_DeviceType"))
    private DeviceType deviceType;

    @Column(name = "area")
    private String area;

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
