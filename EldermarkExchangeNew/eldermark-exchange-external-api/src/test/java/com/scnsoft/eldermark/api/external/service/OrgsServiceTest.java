package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.OrgDto;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.service.OrganizationService;
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
public class OrgsServiceTest {
    @Mock
    private OrganizationService organizationService;
    @Mock
    private PrivilegesService privilegesService;

    @InjectMocks
    private OrgsServiceImpl orgsService;

    @Test
    public void testGet() {
        var organization = BaseServiceTest.createDatabase();
        final Long organizationId = organization.getId();

        final OrgDto expected = new OrgDto();
        expected.setId(organizationId);
        expected.setName(organization.getName());

        when(privilegesService.canReadOrganization(organizationId)).thenReturn(Boolean.TRUE);
        when(organizationService.findById(organizationId, IdNameAware.class)).thenReturn(organization);

        OrgDto result = orgsService.get(organizationId);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        verify(privilegesService).canReadOrganization(organizationId);
    }

    @Test
    public void testListAllAccessible() {
        final Organization database = BaseServiceTest.createDatabase();
        var ids = Collections.singletonList(database.getId());

        when(privilegesService.listOrganizationIdsWithReadAccess()).thenReturn(ids);
        when(organizationService.findAllById(ids, IdNameAware.class)).thenReturn(Arrays.asList(database));

        List<OrgDto> result = orgsService.listAllAccessible();

        assertThat(result).hasSize(1);
    }
}
