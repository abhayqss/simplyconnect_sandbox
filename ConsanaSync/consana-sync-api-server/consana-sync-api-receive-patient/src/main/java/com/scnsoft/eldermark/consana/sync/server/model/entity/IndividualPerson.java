package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.AllArgsConstructor;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.Address;

import java.util.List;

@AllArgsConstructor
public class IndividualPerson {

    private DomainResource person;

    public HumanName getName() {
        if (person instanceof Practitioner) {
            return ((Practitioner) person).getName();
        }
        if (person instanceof RelatedPerson) {
            return ((RelatedPerson) person).getName();
        }
        return null;
    }

    public boolean hasName() {
        var name = getName();
        return name != null && !name.isEmpty();
    }

    public List<ContactPoint> getTelecom() {
        if (person instanceof Practitioner) {
            return ((Practitioner) person).getTelecom();
        }
        if (person instanceof RelatedPerson) {
            return ((RelatedPerson) person).getTelecom();
        }
        return null;
    }

    public boolean hasTelecom() {
        var telecom = getTelecom();
        if (telecom == null) {
            return false;
        }
        for (ContactPoint item : telecom) {
            if (!item.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public List<Address> getAddress() {
        if (person instanceof Practitioner) {
            return ((Practitioner) person).getAddress();
        }
        if (person instanceof RelatedPerson) {
            return ((RelatedPerson) person).getAddress();
        }
        return null;
    }

    public boolean hasAddress() {
        var address = getAddress();
        if (address == null) {
            return false;
        }
        for (Address item : address) {
            if (!item.isEmpty()) {
                return true;
            }
        }
        return false;
    }
}
