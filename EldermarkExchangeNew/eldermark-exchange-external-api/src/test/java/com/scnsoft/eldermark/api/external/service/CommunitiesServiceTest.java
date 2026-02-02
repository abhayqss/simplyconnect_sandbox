package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.CommunityDto;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommunitiesServiceTest {
    @Mock
    private CommunityDao communityDao;
    @Mock
    private PrivilegesService privilegesService;

    @InjectMocks
    private CommunitiesServiceImpl communitiesService;

    @Test
    public void testListByOrganization() {
        var organization = BaseServiceTest.createDatabase();
        final Long organizationId = organization.getId();
        final Community community = new Community();
        community.setOrganization(organization);
        community.setOrganizationId(organizationId);

        when(communityDao.findByOrganizationId(organizationId)).thenReturn(Collections.singletonList(community));
        when(privilegesService.canReadOrganization(organizationId)).thenReturn(Boolean.TRUE);

        var result = communitiesService.listByOrganization(organizationId);

        assertThat(result).hasSize(1);
        verify(communityDao).findByOrganizationId(organizationId);
        verify(privilegesService).canReadOrganization(organizationId);
    }

    @Test
    public void testListAllAccessible() {
        final Organization organization = BaseServiceTest.createDatabase();
        final Long organizationId = organization.getId();
        final Long communityId = TestDataGenerator.randomId();
        var community = new Community();
        community.setOrganizationId(organizationId);
        community.setOrganization(organization);
        community.setId(communityId);
        community.setName("test 1");
        var organization2 = BaseServiceTest.createDatabase();
        final Long organizationId2 = organization2.getId();
        final Long communityId2 = TestDataGenerator.randomIdExceptOf(communityId);
        var community2 = new Community();
        community2.setOrganizationId(organizationId2);
        community2.setOrganization(organization2);
        community2.setId(communityId2);
        community2.setName("test 1");    // Note: mock organization 2 has the same name, but different id and database

        final List<Long> databaseIds = Collections.singletonList(organizationId);

        when(privilegesService.listOrganizationIdsWithReadAccess()).thenReturn(databaseIds);
        when(privilegesService.listCommunitiesWithReadAccess()).thenReturn(Arrays.asList(community2));
        when(communityDao.findByOrganizationIdIn(databaseIds)).thenReturn(Arrays.asList(community));

        var result = communitiesService.listAllAccessible();

        assertThat(result).hasSize(2);
        verify(communityDao).findByOrganizationIdIn(databaseIds);
        verify(privilegesService).listOrganizationIdsWithReadAccess();
        verify(privilegesService).listCommunitiesWithReadAccess();
    }

    @Test
    public void testGet() {
        final Organization organization = BaseServiceTest.createDatabase();
        final Long communityId = TestDataGenerator.randomId();
        var community = new Community();
        community.setOrganizationId(organization.getId());
        community.setOrganization(organization);
        community.setName("test");
        community.setId(communityId);

        var expected = new CommunityDto();
        expected.setId(communityId);
        expected.setName("test");
        expected.setOrgId(organization.getId());
        expected.setOrgName(organization.getName());

        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.TRUE);
        when(communityDao.getOne(communityId)).thenReturn(community);

        CommunityDto result = communitiesService.get(communityId);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        verify(privilegesService).canReadCommunity(communityId);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme