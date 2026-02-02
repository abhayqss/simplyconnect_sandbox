package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatClientCareTeamFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatCommunityCareTeamFilter;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.dto.CareTeamMemberDto;
import com.scnsoft.eldermark.dto.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.dto.CareTeamMemberRoleAvatarAwareDto;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedEntityDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@Transactional
public class CareTeamMemberFacadeImpl implements CareTeamMemberFacade {

    @Autowired
    private BiFunction<CareTeamMember, CareTeamFilter, CareTeamMemberListItemDto> careTeamMemberListItemDtoConverter;

    @Autowired
    private Converter<CareTeamMember, CareTeamMemberDto> careTeamMemberDtoConverter;

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Autowired
    private ClientCareTeamMemberModifiedService clientCareTeamMemberModifiedService;

    @Autowired
    private Converter<CareTeamMemberDto, ClientCareTeamMember> clientCareTeamMemberDtoToEntityConveter;

    @Autowired
    private Converter<CareTeamMemberDto, CommunityCareTeamMember> communityCareTeamMemberDtoToEntityConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private CommunityCareTeamMemberService communityCareTeamMemberService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientService clientService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canViewList(#filter.clientId)")
    public Page<CareTeamMemberListItemDto> find(@P("filter") CareTeamFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        var page = careTeamMemberService.find(
                        filter,
                        permissionFilter,
                        PaginationUtils.applyEntitySort(pageable, CareTeamMemberListItemDto.class)
                )
                .map(ctm -> careTeamMemberListItemDtoConverter.apply(ctm, filter));

        if (filter.getClientId() != null) {
            clientCareTeamMemberModifiedService.careTeamMemberListViewed(loggedUserService.getCurrentEmployeeId(), filter.getClientId());
        }

        return page;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canView(#careTeamMemberId)")
    public CareTeamMemberDto findById(@P("careTeamMemberId") Long careTeamMemberId) {
        clientCareTeamMemberModifiedService.careTeamMemberViewed(careTeamMemberId, loggedUserService.getCurrentEmployeeId());
        return careTeamMemberDtoConverter.convert(careTeamMemberService.findById(careTeamMemberId));
    }

    @Override
    @PreAuthorize("@careTeamSecurityService.canDelete(#careTeamMemberId)")
    public void deleteById(@P("careTeamMemberId") Long careTeamMemberId) {
        careTeamMemberService.deleteById(careTeamMemberId, loggedUserService.getCurrentEmployeeId());
    }

    @Override
    @PreAuthorize("@careTeamSecurityService.canAdd(#careTeamMemberDto)")
    public Long add(@P("careTeamMemberDto") CareTeamMemberDto careTeamMemberDto) {
        return save(careTeamMemberDto);
    }

    @Override
    @PreAuthorize("@careTeamSecurityService.canEdit(#careTeamMemberDto.id, #careTeamMemberDto.careTeamRoleId)")
    public Long edit(@P("careTeamMemberDto") CareTeamMemberDto careTeamMemberDto) {
        return save(careTeamMemberDto);
    }

    public Long save(CareTeamMemberDto dto) {
        if (dto.getClientId() != null) {
            clientService.validateActive(dto.getClientId());
            return careTeamMemberService.save(
                    clientCareTeamMemberDtoToEntityConveter.convert(dto),
                    loggedUserService.getCurrentEmployeeId()
            ).getId();
        }
        if (dto.getCommunityId() != null) {
            return careTeamMemberService.save(
                    communityCareTeamMemberDtoToEntityConverter.convert(dto),
                    loggedUserService.getCurrentEmployeeId()
            ).getId();
        }
        throw new ValidationException("Either clientId or communityId must be provided");
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canListContactsForCareTeamInOrganization(#organizationId, #clientId, #communityId)")
    public List<IdentifiedNamedEntityDto> getContacts(@P("organizationId") Long organizationId,
                                                      @P("clientId") Long clientId,
                                                      @P("communityId") Long communityId) {
        if (!CareCoordinationUtils.isOnlyOneIsPresent(clientId, communityId)) {
            throw new ValidationException("Either clientId or communityId must be provided");
        }

        List<IdNamesAware> employees = new ArrayList<>();
        var sort = Sort.by(Sort.Order.asc(Employee_.FIRST_NAME), Sort.Order.asc(Employee_.LAST_NAME));
        if (clientId != null) {
            employees = careTeamMemberService.getContactsSuitableForClientCareTeam(organizationId, clientId, sort);
        } else if (communityId != null) {
            employees = careTeamMemberService.getContactsSuitableForCommunityCareTeam(organizationId, sort);
        }

        return employees.stream()
                .map(employeeBasic -> new IdentifiedNamedEntityDto(employeeBasic.getId(), employeeBasic.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canAddAnyRole(#clientId, #communityId, #affiliation.toType())")
    public List<IdentifiedNamedEntityDto> getContactsOrganizations(@P("clientId") Long clientId, @P("communityId") Long communityId,
                                                                   @P("affiliation") CareTeamFilter.Affiliation affiliation) {
        if (!CareCoordinationUtils.isOnlyOneIsPresent(clientId, communityId)) {
            throw new ValidationException("Either clientId or communityId must be provided");
        }

        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        List<IdNameAware> organizations;
        var sort = Sort.by(Organization_.NAME);
        if (clientId != null) {
            organizations = careTeamMemberService.getContactsOrganizationsSuitableForClientCareTeam(permissionFilter,
                    clientId, affiliation.toType(), sort, com.scnsoft.eldermark.beans.projection.IdNameAware.class);
        } else {
            organizations = careTeamMemberService.getContactsOrganizationsSuitableForCommunityCareTeam(permissionFilter,
                    communityId, affiliation.toType(), sort, com.scnsoft.eldermark.beans.projection.IdNameAware.class);
        }

        return organizations.stream()
                .map(IdentifiedNamedEntityDto::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canViewList(#filter.clientId)")
    public Long count(@P("filter") CareTeamFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return careTeamMemberService.count(filter, permissionFilter);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canViewList(null)")
    public List<CareTeamMemberRoleAvatarAwareDto> findChatAccessibleCommunityCareTeamMembers(
            AccessibleChatCommunityCareTeamFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());

        var communityCareTeamMembers = communityCareTeamMemberService.findChatAccessibleCommunityCareTeamMembers(
                permissionFilter, filter);
        return deduplicateAndMap(communityCareTeamMembers);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canViewList(#filter.clientId)")
    public List<CareTeamMemberRoleAvatarAwareDto> findChatAccessibleClientCareTeamMembers(
            @P("filter") AccessibleChatClientCareTeamFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());

        var clientCareTeamMembers = clientCareTeamMemberService.findChatAccessibleClientCareTeamMembers(permissionFilter,
                filter);
        return deduplicateAndMap(clientCareTeamMembers);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canViewList(#filter.clientId)")
    public List<CareTeamMemberRoleAvatarAwareDto> findVideoCallAccessibleClientCareTeamMembers(
            @P("filter") AccessibleChatClientCareTeamFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());

        var clientCareTeamMembers = clientCareTeamMemberService.findVideoCallAccessibleClientCareTeamMembers(permissionFilter,
                filter);
        return deduplicateAndMap(clientCareTeamMembers);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canViewList(null)")
    public List<CareTeamMemberRoleAvatarAwareDto> findVideoCallAccessibleCommunityCareTeamMembers(AccessibleChatCommunityCareTeamFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());

        var communityCareTeamMembers = communityCareTeamMemberService.findVideoCallAccessibleCommunityCareTeamMembers(
                permissionFilter, filter);
        return deduplicateAndMap(communityCareTeamMembers);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canListContactsForCareTeamInOrganization(#organizationId, #clientId, #communityId)")
    public Long getContactsCount(Long organizationId, Long clientId, Long communityId) {
        if (!CareCoordinationUtils.isOnlyOneIsPresent(clientId, communityId)) {
            throw new ValidationException("Either clientId or communityId must be provided");
        }
        if (clientId != null) {
            return careTeamMemberService.getContactsSuitableForClientCareTeamCount(organizationId);
        } else {
            return careTeamMemberService.getContactsSuitableForCommunityCareTeamCount(organizationId);
        }
    }

    private List<CareTeamMemberRoleAvatarAwareDto> deduplicateAndMap(List<CareTeamMemberIdNameRoleAvatarAware> careTeamMembers) {
        var groupedCtmRolesMapByEmployeeId = getGroupedCtmRolesMapByEmployeeId(careTeamMembers);
        return careTeamMembers.stream()
                .filter(StreamUtils.distinctByKey(CareTeamMemberIdNameRoleAvatarAware::getEmployeeId))
                .map(ctm -> createDto(ctm, groupedCtmRolesMapByEmployeeId.get(ctm.getEmployeeId())))
                .collect(Collectors.toList());
    }

    //In case single member was added to care team multiple times, all his roles separated by comma will be added to map
    private Map<Long, String> getGroupedCtmRolesMapByEmployeeId(List<CareTeamMemberIdNameRoleAvatarAware> communityCareTeamMembers) {
        return communityCareTeamMembers.stream()
                .collect(Collectors.groupingBy(CareTeamMemberIdNameRoleAvatarAware::getEmployeeId,
                        Collectors.mapping(CareTeamMemberIdNameRoleAvatarAware::getCareTeamRoleName, Collectors.joining(", "))));
    }

    private CareTeamMemberRoleAvatarAwareDto createDto(CareTeamMemberIdNameRoleAvatarAware ctm, String role) {
        return new CareTeamMemberRoleAvatarAwareDto(ctm.getEmployeeId(),
                ctm.getEmployeeFirstName(), ctm.getEmployeeLastName(), ctm.getEmployeeFullName(),
                ctm.getEmployeeAvatarId(), role);
    }

}
