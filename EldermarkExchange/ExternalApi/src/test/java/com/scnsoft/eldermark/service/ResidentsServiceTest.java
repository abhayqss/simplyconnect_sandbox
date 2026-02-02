package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.ResidentDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.ResidentDto;
import com.scnsoft.eldermark.web.entity.ResidentListItemDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static com.scnsoft.eldermark.service.BaseServiceTest.createResident;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 2/1/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class ResidentsServiceTest {
    @Mock
    private ResidentDao residentDao;
    @Mock
    private PrivilegesService privilegesService;

    @InjectMocks
    private ResidentsService residentsService;


    @Test
    public void testListByOrganization() {
        final Database database = BaseServiceTest.createDatabase();
        final Long orgId = database.getId();
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident(residentId);
        resident.setDatabaseId(orgId);
        resident.setDatabase(database);
        resident.setFacility(new Organization());

        when(privilegesService.canReadOrganization(orgId)).thenReturn(Boolean.TRUE);
        when(residentDao.findVisibleByDatabaseId(orgId, pageable)).thenReturn(new PageImpl<>(Arrays.asList(resident)));

        Page<ResidentListItemDto> result = residentsService.listByOrganization(orgId, pageable);

        assertThat(result.getContent(), hasSize(1));
        verify(residentDao).findVisibleByDatabaseId(orgId, pageable);
        verify(privilegesService).canReadOrganization(orgId);
    }

    @Test
    public void testListByCommunity() {
        final Database database = BaseServiceTest.createDatabase();
        final Long communityId = TestDataGenerator.randomId();
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident(residentId);
        resident.setDatabaseId(database.getId());
        resident.setDatabase(database);
        resident.setFacility(new Organization());

        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.TRUE);
        when(residentDao.findVisibleByFacilityId(communityId, pageable)).thenReturn(new PageImpl<>(Arrays.asList(resident)));

        Page<ResidentListItemDto> result = residentsService.listByCommunity(communityId, pageable);

        assertThat(result.getContent(), hasSize(1));
        verify(residentDao).findVisibleByFacilityId(communityId, pageable);
        verify(privilegesService).canReadCommunity(communityId);
    }

    @Test
    public void testGet() throws Exception {
        final String email = TestDataGenerator.randomEmail();
        final Long communityId = TestDataGenerator.randomId();
        final String communityName = TestDataGenerator.randomName();
        final Organization facility = new Organization();
        facility.setId(communityId);
        facility.setName(communityName);
        final Resident resident = createResident(email, null, null, null);
        resident.setFacility(facility);
        final Long residentId = resident.getId();

        final ResidentDto expected = new ResidentDto();
        expected.setId(residentId);
        expected.setEmail(email);
        expected.setPhone(null);
        expected.setFirstName(resident.getFirstName());
        expected.setLastName(resident.getLastName());
        expected.setMiddleName(resident.getMiddleName());
        expected.setCommunityId(communityId);
        expected.setCommunityName(facility.getName());
        expected.setOrgId(resident.getDatabaseId());
        expected.setOrgName(resident.getDatabase().getName());

        when(residentDao.getFacilityIdById(residentId)).thenReturn(communityId);
        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.TRUE);
        when(residentDao.getOne(residentId)).thenReturn(resident);
        when(residentDao.findOne(residentId)).thenReturn(resident);
        when(residentDao.isVisible(residentId)).thenReturn(true);

        ResidentDto result = residentsService.get(residentId);

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canReadCommunity(communityId);
    }

    @Test(expected = PhrException.class)
    public void testGetThrowsAccessForbidden() throws Exception {
        final String email = TestDataGenerator.randomEmail();
        final Long communityId = TestDataGenerator.randomId();
        final Organization facility = new Organization();
        facility.setId(communityId);
        final Resident resident = createResident(email, null, null, null);
        resident.setFacility(facility);
        final Long residentId = resident.getId();

        when(residentDao.getFacilityIdById(residentId)).thenReturn(communityId);
        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.FALSE);
        when(residentDao.getOne(residentId)).thenReturn(resident);
        when(residentDao.findOne(residentId)).thenReturn(resident);

        residentsService.get(residentId);

        verify(privilegesService).canReadCommunity(communityId);
    }

    @Test
    public void testGetEntity() throws Exception {
        final Long residentId = TestDataGenerator.randomId();
        final Resident resident = new Resident(residentId);

        when(residentDao.getOne(residentId)).thenReturn(resident);
        when(residentDao.findOne(residentId)).thenReturn(resident);

        Resident result = residentsService.getEntity(residentId);

        assertThat(result, sameBeanAs(resident));
        verifyZeroInteractions(privilegesService);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme