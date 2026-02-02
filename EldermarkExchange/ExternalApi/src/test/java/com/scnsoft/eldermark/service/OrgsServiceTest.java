package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.web.entity.OrgDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author phomal
 * Created on 2/12/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrgsServiceTest {
    @Mock
    private DatabasesService databasesService;
    @Mock
    private PrivilegesService privilegesService;

    @InjectMocks
    private OrgsService orgsService;

    @Test
    public void testGet() throws Exception {
        final Database database = BaseServiceTest.createDatabase();
        final Long databaseId = database.getId();

        final OrgDto expected = new OrgDto();
        expected.setId(databaseId);
        expected.setName(database.getName());

        when(databasesService.getDatabaseById(databaseId)).thenReturn(database);
        when(privilegesService.canReadOrganization(databaseId)).thenReturn(Boolean.TRUE);

        OrgDto result = orgsService.get(databaseId);

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canReadOrganization(databaseId);
    }

    @Test
    public void testListAllAccessible() throws Exception {
        final Database database = BaseServiceTest.createDatabase();

        when(privilegesService.listOrganizationsWithReadAccess()).thenReturn(Arrays.asList(database));

        List<OrgDto> result = orgsService.listAllAccessible();

        assertThat(result, hasSize(1));
        verify(privilegesService).listOrganizationsWithReadAccess();
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme