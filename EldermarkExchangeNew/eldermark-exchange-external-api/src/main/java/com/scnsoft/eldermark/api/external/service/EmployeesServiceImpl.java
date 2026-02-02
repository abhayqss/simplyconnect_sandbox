package com.scnsoft.eldermark.api.external.service;

import com.scnsoft.eldermark.api.external.utils.PersonUtils;
import com.scnsoft.eldermark.api.external.web.dto.EmployeeDto;
import com.scnsoft.eldermark.api.external.web.dto.EmployeeListItemDto;
import com.scnsoft.eldermark.api.shared.exception.PhrException;
import com.scnsoft.eldermark.api.shared.exception.PhrExceptionType;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.specification.EmployeeSpecificationGenerator;
import com.scnsoft.eldermark.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EmployeesServiceImpl implements EmployeesService {

    private final EmployeeDao employeeDao;
    private final PrivilegesService privilegesService;
    private final EmployeeSpecificationGenerator employeeSpecificationGenerator;

    @Autowired
    public EmployeesServiceImpl(EmployeeDao employeeDao, PrivilegesService privilegesService, EmployeeSpecificationGenerator employeeSpecificationGenerator) {
        this.employeeDao = employeeDao;
        this.privilegesService = privilegesService;
        this.employeeSpecificationGenerator = employeeSpecificationGenerator;
    }

    @Override
    public Page<EmployeeListItemDto> listByOrganization(long orgId, Pageable pageable) {
        if (!privilegesService.canReadOrganization(orgId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        var byOrg = employeeSpecificationGenerator.byOrganizationId(orgId);
        var active = employeeSpecificationGenerator.active();

        //todo - use projection
        var employees = employeeDao.findAll(byOrg.and(active), pageable);

        return convert(employees);
    }

    @Override
    public Page<EmployeeListItemDto> listByCommunity(long communityId, Pageable pageable) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
        var byCommunityId = employeeSpecificationGenerator.byCommunityId(communityId);
        var active = employeeSpecificationGenerator.active();
        final Page<Employee> employees = employeeDao.findAll(byCommunityId.and(active), pageable);

        return convert(employees);
    }

    @Override
    @Transactional(readOnly = false)
    public EmployeeDto create(Long communityId, String phone, String email, String login, String firstName, String lastName, String nucleusUserId) {
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }

        // TODO implement
        return new EmployeeDto();
    }

    void checkAccessOrThrow(Long employeeId) {
        final Long communityId = employeeDao.findById(employeeId, CommunityIdAware.class).map(CommunityIdAware::getCommunityId).orElse(null);
        if (!privilegesService.canReadCommunity(communityId)) {
            throw new PhrException(PhrExceptionType.ACCESS_FORBIDDEN);
        }
    }

    @Override
    public EmployeeDto get(Long employeeId) {
        checkAccessOrThrow(employeeId);
        return convertDetailed(employeeDao.getOne(employeeId));
    }

    private static Page<EmployeeListItemDto> convert(Page<Employee> employees) {
        return employees.map(EmployeesServiceImpl::convert);
    }

    private static EmployeeListItemDto convert(Employee employee) {
        //todo - use projection
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
        dto.setEmail(PersonUtils.getPersonEmailValue(employee.getPerson()));
        dto.setPhone(PersonUtils.getPersonPhoneValue(employee.getPerson()));
        dto.setRole(employee.getCareTeamRole().getName());
        dto.setRoleId(employee.getCareTeamRole().getId());
        dto.setCommunityId(employee.getCommunityId());
        dto.setOrgId(employee.getOrganizationId());

        return dto;
    }

}
