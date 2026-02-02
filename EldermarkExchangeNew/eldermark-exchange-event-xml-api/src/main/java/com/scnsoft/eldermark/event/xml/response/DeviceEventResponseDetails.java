package com.scnsoft.eldermark.event.xml.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceEventResponseDetails {

    @XmlElement
    private String deviceId;
    @XmlElement
    private String message;

    public DeviceEventResponseDetails() {
    }

    public DeviceEventResponseDetails(String deviceId, String message) {
        this.deviceId = deviceId;
        this.message = message;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
