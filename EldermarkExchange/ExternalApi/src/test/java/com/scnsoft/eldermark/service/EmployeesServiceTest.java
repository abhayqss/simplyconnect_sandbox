package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.EmployeeDao;
import com.scnsoft.eldermark.entity.CareTeamRole;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.web.entity.EmployeeDto;
import com.scnsoft.eldermark.web.entity.EmployeeListItemDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;

import static com.scnsoft.eldermark.service.BaseServiceTest.createEmployee;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.*;

/**
 * @author phomal
 * Created on 2/1/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class EmployeesServiceTest {
    @Mock
    private EmployeeDao employeeDao;
    @Mock
    private PrivilegesService privilegesService;

    @InjectMocks
    private EmployeesService employeesService;

    @Test
    public void testListByOrganization() {
        final Long orgId = TestDataGenerator.randomId();
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final Employee employee = new Employee();
        employee.setId(TestDataGenerator.randomId());
        employee.setDatabaseId(orgId);

        when(employeeDao.findByDatabaseIdAndStatus(orgId, EmployeeStatus.ACTIVE, pageable)).thenReturn(new PageImpl<>(Arrays.asList(employee)));
        when(privilegesService.canReadOrganization(orgId)).thenReturn(Boolean.TRUE);

        Page<EmployeeListItemDto> result = employeesService.listByOrganization(orgId, pageable);

        assertThat(result.getContent(), hasSize(1));
        verify(employeeDao).findByDatabaseIdAndStatus(orgId, EmployeeStatus.ACTIVE, pageable);
        verify(privilegesService).canReadOrganization(orgId);
    }

    @Test
    public void testListByCommunity() {
        final Long communityId = TestDataGenerator.randomId();
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final Employee employee = new Employee();
        employee.setId(TestDataGenerator.randomId());
        employee.setCommunityId(communityId);

        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.TRUE);
        when(employeeDao.findByCommunityIdAndStatus(communityId, EmployeeStatus.ACTIVE, pageable)).thenReturn(new PageImpl<>(Arrays.asList(employee)));

        Page<EmployeeListItemDto> result = employeesService.listByCommunity(communityId, pageable);

        assertThat(result.getContent(), hasSize(1));
        verify(employeeDao).findByCommunityIdAndStatus(communityId, EmployeeStatus.ACTIVE, pageable);
        verify(privilegesService).canReadCommunity(communityId);
    }

    @Test
    public void testGet() throws Exception {
        final String email = TestDataGenerator.randomEmail();
        final Long communityId = TestDataGenerator.randomId();
        final Employee employee = createEmployee(email, null, null, null);
        employee.setCommunityId(communityId);
        final Long employeeId = employee.getId();
        final Long orgId = employee.getDatabaseId();
        final CareTeamRole role = employee.getCareTeamRole();

        final EmployeeDto expected = new EmployeeDto();
        expected.setId(employeeId);
        expected.setEmail(email);
        expected.setCommunityId(communityId);
        expected.setOrgId(orgId);
        expected.setRole(role.getName());
        expected.setRoleId(role.getId());

        when(employeeDao.getCommunityIdById(employeeId)).thenReturn(communityId);
        when(employeeDao.getOne(employeeId)).thenReturn(employee);
        when(employeeDao.findOne(employeeId)).thenReturn(employee);
        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.TRUE);

        EmployeeDto result = employeesService.get(employeeId);

        assertThat(result, sameBeanAs(expected));
        verify(privilegesService).canReadCommunity(communityId);
    }

    @Test(expected = PhrException.class)
    public void testGetThrowsAccessForbidden() throws Exception {
        final String email = TestDataGenerator.randomEmail();
        final Long communityId = TestDataGenerator.randomId();
        final Employee employee = createEmployee(email, null, null, null);
        employee.setCommunityId(communityId);
        final Long employeeId = employee.getId();

        when(employeeDao.getCommunityIdById(employeeId)).thenReturn(communityId);
        when(employeeDao.getOne(employeeId)).thenReturn(employee);
        when(employeeDao.findOne(employeeId)).thenReturn(employee);
        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.FALSE);

        employeesService.get(employeeId);

        verify(privilegesService).canReadCommunity(communityId);
    }

    @Test
    public void testGetEntity() throws Exception {
        final Long employeeId = TestDataGenerator.randomId();
        final Employee employee = new Employee();
        employee.setId(employeeId);

        when(employeeDao.getOne(employeeId)).thenReturn(employee);
        when(employeeDao.findOne(employeeId)).thenReturn(employee);

        Employee result = employeesService.getEntity(employeeId);

        assertThat(result, sameBeanAs(employee));
        verifyZeroInteractions(privilegesService);
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme