package com.scnsoft.eldermark.event.xml.service;


import com.scnsoft.eldermark.event.xml.dao.EventOrganizationDao;
import com.scnsoft.eldermark.event.xml.schema.Organization;
import com.scnsoft.eldermark.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NonUniqueResultException;
import java.util.List;

@Service
@Transactional
public class EventOrganizationServiceImpl implements EventOrganizationService {

    private final OrganizationService organizationService;

    private final EventOrganizationDao eventOrganizationDao;

    private final Converter<Organization, com.scnsoft.eldermark.entity.Organization> organizationEntityConverter;

    @Autowired
    public EventOrganizationServiceImpl(OrganizationService organizationService, EventOrganizationDao eventOrganizationDao, Converter<Organization, com.scnsoft.eldermark.entity.Organization> organizationEntityConverter) {
        this.organizationService = organizationService;
        this.eventOrganizationDao = eventOrganizationDao;
        this.organizationEntityConverter = organizationEntityConverter;
    }

    @Override
    public com.scnsoft.eldermark.entity.Organization getOrCreateOrganizationFromSchema(Organization source) {
        List<com.scnsoft.eldermark.entity.Organization> organizations = eventOrganizationDao.findByOid(source.getID());
        if (organizations.size() == 0) {
            var organization = organizationEntityConverter.convert(source);
            return organizationService.save(organization, true);
        } else if (organizations.size() == 1) {
            return organizations.get(0);
        } else {
            throw new NonUniqueResultException("Found more than one organization by OID " + source.getID());
        }
    }
}
