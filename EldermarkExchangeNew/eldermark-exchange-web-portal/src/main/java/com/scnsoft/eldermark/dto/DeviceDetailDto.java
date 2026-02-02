package com.scnsoft.eldermark.dto;

import java.util.Date;

public class DeviceDetailDto extends DeviceDto {
    private Date dateTime;
    
    private String deviceTypeName;;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }
}
