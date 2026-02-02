package com.scnsoft.eldermark.event.xml.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "errorResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class EventsErrorResponse {

    @XmlElement
    private EventResponseStatus status;

    public EventsErrorResponse() {
    }

    public EventsErrorResponse(EventResponseStatus status) {
        this.status = status;
    }

    public EventResponseStatus getStatus() {
        return status;
    }

    public void setStatus(EventResponseStatus status) {
        this.status = status;
    }
}
