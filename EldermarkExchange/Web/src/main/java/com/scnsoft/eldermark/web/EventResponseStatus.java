package com.scnsoft.eldermark.web;

import java.io.Serializable;

/**
 * Created by pzhurba on 02-Nov-15.
 */

public class EventResponseStatus implements Serializable {
    private int code;
    private String details;

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
