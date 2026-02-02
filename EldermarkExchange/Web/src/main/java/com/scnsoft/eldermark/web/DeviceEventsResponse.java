package com.scnsoft.eldermark.web;

import com.scnsoft.eldermark.schema.DeviceEvent;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class DeviceEventsResponse {
    private int code;
    @XmlElement(name = "details", required = true)
    protected List<DeviceEventResponseDetails> details;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DeviceEventResponseDetails> getDetails() {
        return details;
    }

    public void setDetails(List<DeviceEventResponseDetails> details) {
        this.details = details;
    }
}
