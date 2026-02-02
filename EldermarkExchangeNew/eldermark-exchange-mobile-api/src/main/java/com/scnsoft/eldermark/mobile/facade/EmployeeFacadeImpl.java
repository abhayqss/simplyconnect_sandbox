package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.conversation.EmployeeSearchWithFavouriteFilter;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.dto.employee.EmployeeUpdates;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeDto;
import com.scnsoft.eldermark.mobile.dto.employee.EmployeeUpdateRequestDto;
import com.scnsoft.eldermark.projection.EmployeeIdNameFavouriteOrgDetails;
import com.scnsoft.eldermark.projection.IsFavouriteEvaluatedAware;
import com.scnsoft.eldermark.service.ContactService;
import com.scnsoft.eldermark.service.security.EmployeeMobileSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.function.BiFunction;

@Service
@Transactional
public class EmployeeFacadeImpl implements EmployeeFacade {

    private static final Sort DEFAULT_SORT = Sort.by(
            Sort.Order.desc(IsFavouriteEvaluatedAware.IS_FAVOURITE_PROPERTY_NAME),
            Sort.Order.asc(Employee_.ORGANIZATION + "." + Organization_.NAME),
            Sort.Order.asc(Employee_.FIRST_NAME),
            Sort.Order.asc(Employee_.LAST_NAME));

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ContactService contactService;

    @Autowired
    private EmployeeMobileSecurityService employeeSecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private BiFunction<EmployeeIdNameFavouriteOrgDetails, PermissionFilter, EmployeeDto> employeeListItemDtoConverter;

    @Autowired
    private Converter<Employee, EmployeeDto> employeeDtoConverter;

    @Autowired
    private Converter<EmployeeUpdateRequestDto, EmployeeUpdates> employeeUpdateDtoConverter;

    @Override
    @Transactional(readOnly = true)
    public Page<EmployeeDto> find(EmployeeSearchWithFavouriteFilter filter, Pageable pageRequest) {
        adjustFilterForList(filter);
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return contactService.findChatAccessible(permissionFilter, filter, PaginationUtils.sortByDefault(pageRequest, DEFAULT_SORT))
                .map(item -> employeeListItemDtoConverter.apply(item, permissionFilter));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exists(EmployeeSearchWithFavouriteFilter filter) {
        adjustFilterForList(filter);
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        return contactService.existsChatAccessible(permissionFilter, filter);
    }

    private void adjustFilterForList(EmployeeSearchWithFavouriteFilter filter) {
        var requestedByEmployeeId = loggedUserService.getCurrentEmployeeId();

        filter.setExcludedEmployeeId(requestedByEmployeeId);
        filter.setFavouriteOfEmployeeIdHint(requestedByEmployeeId);
        filter.setExcludeSystemRole(CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@chatSecurityService.canStart({#employeeId}) || @contactSecurityService.canView(#employeeId)")
    public EmployeeDto findById(@P("employeeId") Long employeeId) {
        return employeeDtoConverter.convert(contactService.findById(employeeId));
    }

    @Override
    @PreAuthorize("@chatSecurityService.canStart({#employeeId})")
    public void setFavourite(@P("employeeId") Long employeeId, boolean favourite) {
        var requestedByEmployeeId = loggedUserService.getCurrentEmployeeId();
        contactService.setFavourite(employeeId, favourite, requestedByEmployeeId);
    }

    @Override
    @PreAuthorize("@contactSecurityService.canEdit(#employee.id, T(com.scnsoft.eldermark.service.CareTeamRoleService).ANY_TARGET_ROLE)")
    public void update(EmployeeUpdateRequestDto employee) {
        var updates = Objects.requireNonNull(employeeUpdateDtoConverter.convert(employee));
        updates.setStatus(EmployeeStatus.ACTIVE);
        contactService.update(updates, loggedUserService.getCurrentEmployeeId());
    }

    @Override
    @PreAuthorize("@contactSecurityService.canView(#employeeId)")
    public boolean canEdit(Long employeeId) {
        return employeeSecurityService.canEdit(employeeId);
    }
}
