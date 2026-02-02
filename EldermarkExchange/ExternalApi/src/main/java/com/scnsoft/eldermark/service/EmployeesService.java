package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.dao.phr.EmployeeDao;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.shared.exception.PhrException;
import com.scnsoft.eldermark.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.web.entity.EmployeeDto;
import com.scnsoft.eldermark.web.entity.EmployeeListItemDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * @author phomal
 * Created on 1/30/2018.
 */
@Service
@Transactional(readOnly = true)
public class EmployeesService {

    private final EmployeeDao employeeDao;
    private final PrivilegesService privilegesService;

    @Autowired
    public EmployeesService(EmployeeDao employeeDao, PrivilegesService privilegesService) {
        this.employeeDao = employeeDao;
        this.privilegesService = privilegesService;
    }

    public Page<EmployeeListItemDto> listByOrganization(long orgId, Pageable pageable) {
        if (!privilegesService.canReadOrganization(orgId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        final Page<Employee> employees = employeeDao.findByDatabaseIdAndStatus(orgId, EmployeeStatus.ACTIVE, pageable);

        return convert(employees);
    }

    public Page<EmployeeListItemDto> listByCommunity(long communityId, Pageable pageable) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        final Page<Employee> employees = employeeDao.findByCommunityIdAndStatus(communityId, EmployeeStatus.ACTIVE, pageable);

        return convert(employees);
    }

    @Transactional(readOnly = false)
    public EmployeeDto create(Long communityId, String phone, String email, String login, String firstName, String lastName, String nucleusUserId) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        // TODO implement
        return new EmployeeDto();
    }

    void checkAccessOrThrow(Long employeeId) {
        final Long communityId = employeeDao.getCommunityIdById(employeeId);
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    Employee getEntity(Long employeeId) {
        return employeeDao.getOne(employeeId);
    }

    public EmployeeDto get(Long employeeId) {
        checkAccessOrThrow(employeeId);
        return convertDetailed(employeeDao.getOne(employeeId));
    }

    public Page<EmployeeListItemDto> listAllAccessible(Pageable pageable) {
        final List<Long> ids = privilegesService.listOrganizationIdsWithReadAccess();
        if (CollectionUtils.isNotEmpty(ids)) {
            final Page<Employee> employees = employeeDao.findByDatabaseIdInAndStatus(ids, EmployeeStatus.ACTIVE, pageable);
            return convert(employees);
        }
        return new PageImpl<>(Collections.<EmployeeListItemDto>emptyList());
    }


    private static Page<EmployeeListItemDto> convert(Page<Employee> employees) {
        return employees.map(new Converter<Employee, EmployeeListItemDto>() {
            @Override
            public EmployeeListItemDto convert(Employee employee) {
                return EmployeesService.convert(employee);
            }
        });
    }

    private static EmployeeListItemDto convert(Employee employee) {
        EmployeeListItemDto dto = new EmployeeListItemDto();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        return dto;
    }

    private static EmployeeDto convertDetailed(Employee employee) {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setDisplayName(employee.getFirstName());
        dto.setEmail(PersonService.getPersonEmailValue(employee.getPerson()));
        dto.setPhone(PersonService.getPersonPhoneValue(employee.getPerson()));
        dto.setRole(employee.getCareTeamRole().getName());
        dto.setRoleId(employee.getCareTeamRole().getId());
        dto.setCommunityId(employee.getCommunityId());
        dto.setOrgId(employee.getDatabaseId());

        return dto;
    }

}
