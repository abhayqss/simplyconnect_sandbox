package com.scnsoft.eldermark.ws.server.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DocumentShareOptions")
public class DocumentShareOptionsWsDto {
    @XmlElement(name = "isSharedWithAll", required = true, nillable = false)
    private Boolean isSharedWithAll;

    @XmlElement(name = "idOfOrganizationToShareWith", required = false)
    private List<Long> idsOfOrganizationsToShareWith;

    public Boolean getSharedWithAll() {
        return isSharedWithAll;
    }

    public void setSharedWithAll(Boolean sharedWithAll) {
        isSharedWithAll = sharedWithAll;
    }

    public List<Long> getIdsOfOrganizationsToShareWith() {
        return idsOfOrganizationsToShareWith;
    }

    public void setIdsOfOrganizationsToShareWith(List<Long> idsOfOrganizationsToShareWith) {
        this.idsOfOrganizationsToShareWith = idsOfOrganizationsToShareWith;
    }
}
