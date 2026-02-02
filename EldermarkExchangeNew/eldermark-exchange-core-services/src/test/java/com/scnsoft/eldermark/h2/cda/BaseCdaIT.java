package com.scnsoft.eldermark.h2.cda;

import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.Community_;
import com.scnsoft.eldermark.h2.BaseH2IT;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public abstract class BaseCdaIT extends BaseH2IT {

    @Autowired
    protected OrganizationDao organizationDao;

    @Autowired
    protected CommunityDao communityDao;

    protected Community findCommunity(String targetOrganizationAlternativeId, String targetCommunityName) {
        var organizations = organizationDao.findAll((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Organization_.alternativeId), targetOrganizationAlternativeId));
        assertEquals(1, organizations.size());
        final Organization targetOrganization = organizations.get(0);

        var communities = communityDao.findAll((root, query, criteriaBuilder) ->
                criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(Community_.name), targetCommunityName),
                        criteriaBuilder.equal(root.get(Community_.organizationId), targetOrganization.getId())
                ));
        assertEquals(1, communities.size());

        return communities.get(0);
    }
}
