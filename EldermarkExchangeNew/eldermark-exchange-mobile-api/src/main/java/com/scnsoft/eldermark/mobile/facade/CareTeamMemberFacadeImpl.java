package com.scnsoft.eldermark.mobile.facade;

import com.scnsoft.eldermark.beans.CareTeamFilter;
import com.scnsoft.eldermark.entity.careteam.CareTeamMember;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamMemberDto;
import com.scnsoft.eldermark.mobile.dto.careteam.CareTeamMemberListItemDto;
import com.scnsoft.eldermark.service.CareTeamMemberService;
import com.scnsoft.eldermark.service.ClientCareTeamMemberModifiedService;
import com.scnsoft.eldermark.service.security.CareTeamSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@Transactional
public class CareTeamMemberFacadeImpl implements CareTeamMemberFacade {

    @Autowired
    private BiFunction<CareTeamMember, Boolean, CareTeamMemberListItemDto> careTeamMemberListItemDtoConverter;

    @Autowired
    private Converter<CareTeamMember, CareTeamMemberDto> careTeamMemberDtoConverter;

    @Autowired
    private CareTeamMemberService careTeamMemberService;

    @Autowired
    private ClientCareTeamMemberModifiedService clientCareTeamMemberModifiedService;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private CareTeamSecurityService careTeamSecurityService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canViewList(#filter.clientId)")
    public Page<CareTeamMemberListItemDto> find(@P("filter") CareTeamFilter filter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        Page<CareTeamMember> ctmPage;

        if (filter.getCanDeleteOnly()) {
            //1. create the same pageable with max elements
            var newPageable = PageRequest.of(0, Integer.MAX_VALUE, pageable.getSort());

            var careTeamMembers = careTeamMemberService.find(filter,
                    permissionFilter,
                    PaginationUtils.applyEntitySort(newPageable, CareTeamMemberListItemDto.class)
            ).toList();

            //2. fetch and filter out
            var filteredCtm = careTeamMembers.stream().filter(ctm -> careTeamSecurityService.canDelete(ctm, permissionFilter))
                    .collect(Collectors.toList());

            var filteredCtmPageContent = filteredCtm.stream()
                    .skip(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .collect(Collectors.toList());

            //3. build new page with result
            ctmPage = new PageImpl<>(filteredCtmPageContent, pageable, filteredCtm.size());
        } else {
            ctmPage = careTeamMemberService.find(filter,
                    permissionFilter,
                    PaginationUtils.applyEntitySort(pageable, CareTeamMemberListItemDto.class)
            );
        }

        var page = ctmPage.map(ctm -> careTeamMemberListItemDtoConverter.apply(ctm, filter.getCanDeleteOnly()));

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
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canViewList(#filter.clientId)")
    public Long count(@P("filter") CareTeamFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        if (filter.getCanDeleteOnly()) {
            var careTeamMembers = careTeamMemberService.find(filter, permissionFilter);
            return careTeamMembers.stream().filter(ctm -> careTeamSecurityService.canDelete(ctm, permissionFilter))
                    .count();
        } else {
            return careTeamMemberService.count(filter, permissionFilter);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@careTeamSecurityService.canViewList(#filter.clientId)")
    public boolean exists(CareTeamFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();

        if (filter.getCanDeleteOnly()) {
            var careTeamMembers = careTeamMemberService.find(filter, permissionFilter);
            return careTeamMembers.stream().anyMatch(ctm -> careTeamSecurityService.canDelete(ctm, permissionFilter));
        } else {
            return careTeamMemberService.exists(filter, permissionFilter);
        }
    }

    @Override
    @PreAuthorize("@careTeamSecurityService.canDelete(#careTeamMemberId)")
    public void deleteById(@P("careTeamMemberId") Long careTeamMemberId) {
        careTeamMemberService.deleteById(careTeamMemberId, loggedUserService.getCurrentEmployeeId());
    }
}
