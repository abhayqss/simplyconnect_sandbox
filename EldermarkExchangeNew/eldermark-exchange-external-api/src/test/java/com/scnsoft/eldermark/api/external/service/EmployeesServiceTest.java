package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.web.dto.EmployeeDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.utils.PaginationUtils;
import com.scnsoft.eldermark.api.shared.utils.test.TestDataGenerator;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static com.scnsoft.eldermark.api.external.service.BaseServiceTest.createEmployee;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeesServiceTest {
    @Mock
    private EmployeeDao employeeDao;
    @Mock
    private PrivilegesService privilegesService;
    @Mock
    private EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @InjectMocks
    private EmployeesServiceImpl employeesService;

    @Test
    public void testListByOrganization() {
        final Long orgId = TestDataGenerator.randomId();
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final Employee employee = new Employee();
        employee.setId(TestDataGenerator.randomId());
        employee.setOrganizationId(orgId);

        Specification<Employee> byOrganizationId = Mockito.mock(Specification.class);
        Specification<Employee> active = Mockito.mock(Specification.class);
        Specification<Employee> spec = Mockito.mock(Specification.class);

        when(byOrganizationId.and(active)).thenReturn(spec);

        when(employeeSpecificationGenerator.active()).thenReturn(active);
        when(employeeSpecificationGenerator.byOrganizationId(orgId)).thenReturn(byOrganizationId);

        when(employeeDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Arrays.asList(employee)));
        when(privilegesService.canReadOrganization(orgId)).thenReturn(Boolean.TRUE);

        var result = employeesService.listByOrganization(orgId, pageable);

        assertThat(result).hasSize(1);
        verify(employeeDao).findAll(spec, pageable);
        verify(privilegesService).canReadOrganization(orgId);
    }

    @Test
    public void testListByCommunity() {
        final Long communityId = TestDataGenerator.randomId();
        final Pageable pageable = PaginationUtils.buildPageable(20, 0);
        final Employee employee = new Employee();
        employee.setId(TestDataGenerator.randomId());
        employee.setCommunityId(communityId);

        Specification<Employee> byCommunityId = Mockito.mock(Specification.class);
        Specification<Employee> active = Mockito.mock(Specification.class);
        Specification<Employee> spec = Mockito.mock(Specification.class);

        when(byCommunityId.and(active)).thenReturn(spec);

        when(employeeSpecificationGenerator.active()).thenReturn(active);
        when(employeeSpecificationGenerator.byCommunityId(communityId)).thenReturn(byCommunityId);

        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.TRUE);
        when(employeeDao.findAll(spec, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(employee)));

        var result = employeesService.listByCommunity(communityId, pageable);

        //todo test dto
        assertThat(result).hasSize(1);
        verify(employeeDao).findAll(spec, pageable);
        verify(privilegesService).canReadCommunity(communityId);
    }

    @Test
    public void testGet() {
        final String email = TestDataGenerator.randomEmail();
        final Long communityId = TestDataGenerator.randomId();
        final Employee employee = createEmployee(email, null, null, null);
        employee.setCommunityId(communityId);
        final Long employeeId = employee.getId();
        final Long orgId = employee.getOrganizationId();
        final CareTeamRole role = employee.getCareTeamRole();

        var expected = new EmployeeDto();
        expected.setId(employeeId);
        expected.setEmail(email);
        expected.setCommunityId(communityId);
        expected.setOrgId(orgId);
        expected.setRole(role.getName());
        expected.setRoleId(role.getId());

        when(employeeDao.findById(employeeId, CommunityIdAware.class)).thenReturn(Optional.of(() -> communityId));
        when(employeeDao.getOne(employeeId)).thenReturn(employee);
        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.TRUE);

        EmployeeDto result = employeesService.get(employeeId);

        assertThat(result).usingRecursiveComparison().isEqualTo(expected);
        verify(privilegesService).canReadCommunity(communityId);
    }

    @Test
    public void testGetThrowsAccessForbidden() {
        final String email = TestDataGenerator.randomEmail();
        final Long communityId = TestDataGenerator.randomId();
        final Employee employee = createEmployee(email, null, null, null);
        employee.setCommunityId(communityId);
        final Long employeeId = employee.getId();

        when(employeeDao.findById(employeeId, CommunityIdAware.class)).thenReturn(Optional.of(() -> communityId));
        when(privilegesService.canReadCommunity(communityId)).thenReturn(Boolean.FALSE);

        assertThrows(PhrException.class, () -> employeesService.get(employeeId));

        verify(privilegesService).canReadCommunity(communityId);
    }
}
