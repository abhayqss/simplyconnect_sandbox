package com.scnsoft.eldermark.ws.server.exceptions;

import javax.xml.ws.WebFault;

@WebFault(messageName = "OrganizationNotFoundException")
public class OrganizationNotFoundException extends Exception {
    private Long organizationId;

    public OrganizationNotFoundException(Long organizationId) {
        super("Organization #" + organizationId + " not found");
        this.organizationId = organizationId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }
}
