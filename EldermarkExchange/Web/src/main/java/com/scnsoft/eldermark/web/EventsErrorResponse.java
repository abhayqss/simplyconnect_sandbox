package com.scnsoft.eldermark.web;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

/**
 * Created by pzhurba on 02-Nov-15.
 */
@XmlRootElement(name = "errorResponse")
public class EventsErrorResponse implements Serializable {

    private  EventResponseStatus status;

    public EventResponseStatus getStatus() {
        return status;
    }

    public void setStatus(EventResponseStatus status) {
        this.status = status;
    }
}
