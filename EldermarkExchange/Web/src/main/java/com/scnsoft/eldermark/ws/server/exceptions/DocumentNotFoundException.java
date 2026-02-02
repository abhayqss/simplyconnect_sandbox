package com.scnsoft.eldermark.ws.server.exceptions;

import javax.xml.ws.WebFault;

@WebFault(messageName = "DocumentNotFoundFault")
public class DocumentNotFoundException extends Exception {
    public DocumentNotFoundException(long documentId) {
        super("Document #" + documentId + " not found");
    }

    public DocumentNotFoundException(String message) {
        super(message);
    }
}
