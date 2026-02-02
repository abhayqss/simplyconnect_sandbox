package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.AppointmentContactFilter;
import com.scnsoft.eldermark.beans.ContactFilter;
import com.scnsoft.eldermark.beans.ContactNameFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatContactFilter;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeBasic;
import com.scnsoft.eldermark.entity.careteam.CareTeamRole;
import com.scnsoft.eldermark.service.ContactService;
import com.scnsoft.eldermark.service.InvitationService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactFacadeImpl implements ContactFacade {

    @Autowired
    private ContactService contactService;

    @Autowired
    private Converter<Employee, ContactDto> contactItemDtoConverter;

    @Autowired
    private ListAndItemConverter<EmployeeBasic, ContactListItemDto> contactListDtoConverter;

    @Autowired
    private InvitationService invitationService;

    @Autowired
    private Converter<ContactDto, Employee> contactDtoToEmployeeConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private Converter<CareTeamRole, RoleDto> careTeamRoleDtoConverter;

    @Override
    @Transactional
    @PreAuthorize("@contactSecurityService.canAdd(#contactDto)")
    public Long add(@P("contactDto") ContactDto contactDto) {
        var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
        final Employee employee = contactService.save(contactDtoToEmployeeConverter.convert(contactDto), currentEmployeeId);
        invitationService.invite(employee.getId());
        return employee.getId();
    }

    @Override
    @Transactional
    @PreAuthorize("@contactSecurityService.canEdit(#contactDto.id, #contactDto.systemRoleId)")
    public Long edit(@P("contactDto") ContactDto contactDto) {
        var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
        return contactService.save(contactDtoToEmployeeConverter.convert(contactDto), currentEmployeeId).getId();
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@contactSecurityService.canViewList()")
    public Page<ContactListItemDto> find(ContactFilter contactFilter, Pageable pageRequest) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return contactService.find(
                contactFilter,
                permissionFilter,
                PaginationUtils.applyEntitySort(pageRequest, ContactListItemDto.class)
        )
                .map(contactListDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@contactSecurityService.canView(#contactId)")
    public ContactDto findById(@P("contactId") Long contactId) {
        return contactItemDtoConverter.convert(contactService.findById(contactId));
    }

    @Override
    @Transactional
    @PreAuthorize("@contactSecurityService.canEdit(#contactId, T(com.scnsoft.eldermark.service.CareTeamRoleService).ANY_TARGET_ROLE)")
    public void invite(@P("contactId") Long contactId) {
        invitationService.invite(contactId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@contactSecurityService.canViewList()")
    public Long count(ContactFilter contactFilter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return contactService.count(contactFilter, permissionFilter);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@contactSecurityService.canAddAnyRole(#organizationId, @contactSecurityService.ANY_TARGET_COMMUNITY)")
    public Boolean validateUnique(String login, @P("organizationId") Long organizationId) {
        return !contactService.existsByLoginInOrganization(login, organizationId);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@contactSecurityService.canViewDirectoryList(#filter.organizationIds)")
    public List<IdentifiedNamedEntityDto> findNames(@P("filter") ContactNameFilter filter) {
        return contactService.findNames(filter).stream()
                .map(employeeBasic -> new IdentifiedNamedEntityDto(employeeBasic.getId(), employeeBasic.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<IdentifiedNamedEntityDto> findChatAccessibleNamesWithChatEnabled(AccessibleChatContactFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());
        return contactService.findChatAccessibleNames(permissionFilter, filter).stream()
                .map(employeeBasic -> new IdentifiedNamedEntityDto(employeeBasic.getId(), employeeBasic.getFullName())).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@contactSecurityService.canViewDirectoryList(#filter.organizationIds)")
    public List<ContactNameRoleDto> findNamesWithRoles(@P("filter") ContactNameFilter filter) {
        return contactService.findNamesWithRoles(filter).stream()
                .map(employeeBasic -> new ContactNameRoleDto(employeeBasic.getId(), employeeBasic.getFullName(), employeeBasic.getCareTeamRoleName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@contactSecurityService.canViewDirectoryList(#filter.organizationId)")
    public List<NameRoleStatusCommunityDto> findAppointmentContacts(AppointmentContactFilter filter) {
        return contactService.findAppointmentContacts(filter).stream()
                .map(e -> new NameRoleStatusCommunityDto(
                        e.getId(),
                        e.getFullName(),
                        e.getCareTeamRoleName(),
                        e.getStatus().name(),
                        e.getCommunityName()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@contactSecurityService.canView(#contactId)")
    public LocationDto findAddressLocationById(@P("contactId") Long contactId) {
        var coordinates = contactService.findAddressCoordinatesById(contactId);
        var addressLocation = new LocationDto();
        addressLocation.setLatitude(coordinates.getFirst());
        addressLocation.setLongitude(coordinates.getSecond());
        return addressLocation;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleDto> getQaUnavailableRoles() {
        return contactService.getQaUnavailableRoles().stream()
                .map(careTeamRoleDtoConverter::convert)
                .collect(Collectors.toList());
    }
}
