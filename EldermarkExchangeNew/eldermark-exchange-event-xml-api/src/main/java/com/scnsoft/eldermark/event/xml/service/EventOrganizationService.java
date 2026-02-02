package com.scnsoft.eldermark.event.xml.service;


import com.scnsoft.eldermark.event.xml.schema.Organization;

public interface EventOrganizationService {

    com.scnsoft.eldermark.entity.Organization getOrCreateOrganizationFromSchema(Organization source);
}
