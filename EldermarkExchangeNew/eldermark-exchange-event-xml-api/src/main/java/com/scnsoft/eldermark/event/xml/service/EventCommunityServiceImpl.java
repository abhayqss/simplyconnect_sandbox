package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.event.xml.dao.EventCommunityDao;
import com.scnsoft.eldermark.event.xml.schema.Community;
import com.scnsoft.eldermark.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NonUniqueResultException;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class EventCommunityServiceImpl implements EventCommunityService {

    private final EventCommunityDao eventCommunityDao;

    private final CommunityService communityService;

    private final Converter<Community, com.scnsoft.eldermark.entity.community.Community> communityEntityConverter;

    @Autowired
    public EventCommunityServiceImpl(EventCommunityDao eventCommunityDao, CommunityService communityService, Converter<Community, com.scnsoft.eldermark.entity.community.Community> communityEntityConverter) {
        this.eventCommunityDao = eventCommunityDao;
        this.communityService = communityService;
        this.communityEntityConverter = communityEntityConverter;
    }

    @Override
    public com.scnsoft.eldermark.entity.community.Community getOrCreateCommunityFromSchema(com.scnsoft.eldermark.entity.Organization organization, Community source) {
        List<com.scnsoft.eldermark.entity.community.Community> communities = eventCommunityDao.findByOidAndOrganizationId(source.getID(), organization.getId());
        if (communities.size() == 0) {
            var community = communityEntityConverter.convert(source);
            addOrganization(community, organization);
            return communityService.save(community);
        }
        if (communities.size() == 1) {
            return communities.get(0);
        } else {
            throw new NonUniqueResultException("Found more than one community by OID " + source.getID());
        }
    }

    private void addOrganization(com.scnsoft.eldermark.entity.community.Community community, com.scnsoft.eldermark.entity.Organization organization) {
        Objects.requireNonNull(community).setOrganization(organization);
        community.setOrganizationId(organization.getId());
        community.getAddresses().forEach(address -> {
            address.setOrganization(organization);
            address.setOrganizationId(organization.getId());
        });
    }
}
