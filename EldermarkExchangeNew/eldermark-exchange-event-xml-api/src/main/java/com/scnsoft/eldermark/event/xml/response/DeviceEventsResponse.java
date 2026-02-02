package com.scnsoft.eldermark.event.xml.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class DeviceEventsResponse implements Serializable {

    @XmlElement
    private int code;
    @XmlElement(required = true)
    private List<DeviceEventResponseDetails> details;

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
