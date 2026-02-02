package com.scnsoft.eldermark.ws.server.exceptions;

import javax.xml.ws.WebFault;

@WebFault(messageName = "ResidentOptedOutFault")
public class ResidentOptedOutException extends Exception {
    public ResidentOptedOutException() {
        super("Resident has been excluded from HIE. No operations can be performed on this resident.");
    }
}
