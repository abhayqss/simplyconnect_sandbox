package com.scnsoft.eldermark.shared.carecoordination;

import java.io.Serializable;

public class DeviceDto implements Serializable {

    private String deviceID;
    private String patientName;

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }
}
