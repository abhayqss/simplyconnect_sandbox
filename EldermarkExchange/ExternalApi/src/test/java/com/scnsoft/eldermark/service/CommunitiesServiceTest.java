package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationCommunityDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.CommunityDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 2/1/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommunitiesServiceTest {
    @Mock
    private CareCoordinationCommunityDao ccCommunityDao;
    @Mock
    private PrivilegesService privilegesService;

    @InjectMocks
    private CommunitiesService communitiesService;

    @Test
    public void testListByOrganization() {
        final Database database = BaseServiceTest.createDatabase();
        final Long databaseId = database.getId();
        final Organization organization = new Organization();
        organization.setDatabaseId(databaseId);
        organization.setDatabase(database);

        when(ccCommunityDao.findByDatabaseId(databaseId)).thenReturn(Arrays.asList(organization));
        when(privilegesService.canReadOrganization(databaseId)).thenReturn(Boolean.TRUE);

        List<CommunityDto> result = communitiesService.listByOrganization(databaseId);

        assertThat(result, hasSize(1));
        verify(ccCommunityDao).findByDatabaseId(databaseId);
        verify(privilegesService).canReadOrganization(databaseId);
    }

    @Test
    public void testListAllAccessible() {
        final Database database = BaseServiceTest.createDatabase();
        final Long databaseId = database.getId();
        final Long organizationId = TestDataGenerator.randomId();
        final Organization organization = new Organization();
        organization.setDatabaseId(databaseId);
        organization.setDatabase(database);
        organization.setId(organizationId);
        organization.setName("test 1");
        final Database database2 = BaseServiceTest.createDatabase();
        final Long databaseId2 = database2.getId();
        final Long organizationId2 = TestDataGenerator.randomIdExceptOf(organizationId);
        final Organization organization2 = new Organization();
        organization2.setDatabaseId(databaseId2);
        organization2.setDatabase(database2);
        organization2.setId(organizationId2);
        organization2.setName("test 1");    // Note: mock organization 2 has the same name, but different id and database

        final List<Long> databaseIds = Arrays.asList(databaseId);

        when(privilegesService.listOrganizationIdsWithReadAccess()).thenReturn(databaseIds);
        when(privilegesService.listCommunitiesWithReadAccess()).thenReturn(Arrays.asList(organization2));
        when(ccCommunityDao.findByDatabaseIdIn(databaseIds)).thenReturn(Arrays.asList(organization));

        List<CommunityDto> result = communitiesService.listAllAccessible();

        assertThat(result, hasSize(2));
        verify(ccCommunityDao).findByDatabaseIdIn(databaseIds);
        verify(privilegesService).listOrganizationIdsWithReadAccess();
        verify(privilegesService).listCommunitiesWithReadAccess();
    }

    @Test
    public void testGet() {
        final Database database = BaseServiceTest.createDatabase();
        final Long organizationId = TestDataGenerator.randomId();
        final Organization organization = new Organization();
        organization.setDatabaseId(database.getId());
        organization.setDatabase(database);
        organization.setName("test");
        organization.setId(organizationId);

        final CommunityDto expected = new CommunityDto();
        expected.setId(organizationId);
        expected.setName("test");
        expected.setOrgId(database.getId());
        expected.setOrgName(database.getName());

        when(privilegesService.canReadCommunity(organizationId)).thenReturn(Boolean.TRUE);
        when(ccCommunityDao.getOne(organizationId)).thenReturn(organization);

        CommunityDto result = communitiesService.get(organizationId);

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canReadCommunity(organizationId);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme