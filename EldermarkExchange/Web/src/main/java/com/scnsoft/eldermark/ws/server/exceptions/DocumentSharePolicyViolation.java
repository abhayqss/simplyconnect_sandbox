package com.scnsoft.eldermark.ws.server.exceptions;

import javax.xml.ws.WebFault;

@WebFault(messageName = "DocumentSharePolicyViolation")
public class DocumentSharePolicyViolation extends Exception {
    public DocumentSharePolicyViolation(long databaseId) {
        super("Documents share policy violated: current user cannot share documents with organization #" + databaseId);
    }
}
