package com.scnsoft.eldermark.event.xml.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class EventResponseStatus {

    @XmlElement
    private int code;
    @XmlElement
    private String details;

    public EventResponseStatus() {
    }

    public EventResponseStatus(int code, String details) {
        this.code = code;
        this.details = details;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
