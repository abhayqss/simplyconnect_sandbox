package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.specification.ClientExtApiSpecifications;
import com.scnsoft.eldermark.api.external.web.dto.ResidentDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.MPIDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static com.scnsoft.eldermark.api.external.service.BaseServiceTest.createResident;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResidentsServiceTest {
    @Mock
    private ClientDao clientDao;
    @Mock
    private ClientSpecificationGenerator clientSpecifications;
    @Mock
    private ClientExtApiSpecifications clientExtApiSpecifications;
    @Mock
    private MPIDao mpiDao;
    @Mock
    private PrivilegesService privilegesService;
    @Mock
    private OrganizationDao organizationDao;

    @InjectMocks
    private ResidentsServiceImpl residentsService;

    @Test
    public void testListByOrganization() {
        var organization = BaseServiceTest.createDatabase();
        var orgId = organization.getId();
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final Long residentId = TestDataGenerator.randomId();
        final Client resident = new Client(residentId);
        resident.setOrganizationId(orgId);
        resident.setOrganization(organization);
        resident.setCommunity(new Community());

        Specification<Client> byOrg = Mockito.mock(Specification.class);
        Specification<Client> visible = Mockito.mock(Specification.class);
        Specification<Client> spec = Mockito.mock(Specification.class);

        when(privilegesService.canReadOrganization(orgId)).thenReturn(Boolean.TRUE);
        when(clientExtApiSpecifications.isVisible()).thenReturn(visible);
        when(clientSpecifications.byOrganizationId(orgId)).thenReturn(byOrg);
        when(byOrg.and(visible)).thenReturn(spec);
        when(clientDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(resident)));

        var result = residentsService.listByOrganization(orgId, pageable);

        assertThat(result).hasSize(1);
        verify(clientDao).findAll(spec, pageable);
        verify(privilegesService).canReadOrganization(orgId);
    }

    @Test
    public void testListByCommunity() {
        final Organization organization = BaseServiceTest.createDatabase();
        final Long communityId = TestDataGenerator.randomId();
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final Long residentId = TestDataGenerator.randomId();
        final Client resident = new Client(residentId);
        resident.setOrganizationId(organization.getId());
        resident.setOrganization(organization);
        resident.setCommunity(new Community());

        Specification<Client> byCommunity = Mockito.mock(Specification.class);
        Specification<Client> visible = Mockito.mock(Specification.class);
        Specification<Client> spec = Mockito.mock(Specification.class);

        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.TRUE);
        when(clientExtApiSpecifications.isVisible()).thenReturn(visible);
        when(clientSpecifications.byCommunityId(communityId)).thenReturn(byCommunity);
        when(byCommunity.and(visible)).thenReturn(spec);
        when(clientDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(resident)));

        var result = residentsService.listByCommunity(communityId, pageable);

        assertThat(result).hasSize(1);
        verify(clientDao).findAll(spec, pageable);
        verify(privilegesService).canReadCommunity(communityId);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void testGet(boolean allowInactive) {
        final String email = TestDataGenerator.randomEmail();
        final Long communityId = TestDataGenerator.randomId();
        final String communityName = TestDataGenerator.randomName();
        var facility = new Community();
        facility.setId(communityId);
        facility.setName(communityName);
        final Client resident = createResident(email, null, null, null);
        resident.setCommunity(facility);
        resident.setCommunityId(facility.getId());
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
        expected.setOrgId(resident.getOrganizationId());
        expected.setOrgName(resident.getOrganization().getName());

        Specification<Client> byId = Mockito.mock(Specification.class);
        Specification<Client> visible = Mockito.mock(Specification.class);
        Specification<Client> spec = Mockito.mock(Specification.class);


        when(clientDao.findById(residentId, CommunityIdAware.class)).thenReturn(Optional.of(() -> communityId));
        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.TRUE);
        when(privilegesService.hasConsanaAccess()).thenReturn(allowInactive);

        when(clientSpecifications.byId(residentId)).thenReturn(byId);
        when(clientExtApiSpecifications.isVisible(allowInactive)).thenReturn(visible);
        when(byId.and(visible)).thenReturn(spec);
        when(clientDao.count(spec)).thenReturn(1L);

        when(clientDao.getOne(residentId)).thenReturn(resident);

        ResidentDto result = residentsService.get(residentId);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        verify(privilegesService).canReadCommunity(communityId);
    }

    @Test
    public void testGetThrowsAccessForbidden() {
        final String email = TestDataGenerator.randomEmail();
        final Long communityId = TestDataGenerator.randomId();
        var facility = new Community();
        facility.setId(communityId);
        final Client resident = createResident(email, null, null, null);
        resident.setCommunity(facility);
        final Long residentId = resident.getId();

        when(clientDao.findById(residentId, CommunityIdAware.class)).thenReturn(Optional.of(() -> communityId));
        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.FALSE);

        assertThrows(PhrException.class, () -> residentsService.get(residentId));

        verify(privilegesService).canReadCommunity(communityId);
        verify(clientDao).findById(residentId, CommunityIdAware.class);
        verifyNoMoreInteractions(clientDao);
    }

    @Test
    public void testGetEntity() {
        final Long residentId = TestDataGenerator.randomId();
        final Client resident = new Client(residentId);

        when(clientDao.getOne(residentId)).thenReturn(resident);

        var result = residentsService.getEntity(residentId);

        assertThat(result).usingRecursiveComparison().isEqualTo(resident);
        verifyNoInteractions(privilegesService);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme