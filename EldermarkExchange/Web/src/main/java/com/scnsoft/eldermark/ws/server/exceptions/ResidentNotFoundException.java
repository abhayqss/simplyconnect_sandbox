package com.scnsoft.eldermark.ws.server.exceptions;

import javax.xml.ws.WebFault;

@WebFault(messageName = "ResidentNotFoundFault")
public class ResidentNotFoundException extends Exception {
    public ResidentNotFoundException(long residentId) {
        this("Resident #" + residentId + " does not exist.");
    }

    public ResidentNotFoundException(String message) {
        super(message);
    }
}
