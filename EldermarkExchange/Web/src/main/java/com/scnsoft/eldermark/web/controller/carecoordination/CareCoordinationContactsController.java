package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.services.carecoordination.CareTeamRoleService;
import com.scnsoft.eldermark.services.carecoordination.ContactService;
import com.scnsoft.eldermark.services.carecoordination.OrganizationService;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactListItemDto;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by pzhurba on 13-Nov-15.
 */
//@Controller
//@RequestMapping(value = "/care-coordination/contacts")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationContactsController {

    @Autowired
    private ContactService contactService;

    @Autowired
    private CareTeamRoleService careTeamRoleService;

    @Autowired
    private OrganizationService organizationService;


    @RequestMapping(method = RequestMethod.GET)
    public String initContactsView(Model model) {
        final ContactFilterDto filter = new ContactFilterDto();
        model.addAttribute("contactFilter", filter);
        model.addAttribute("affiliatedView", SecurityUtils.isAffiliatedView());
        model.addAttribute("employeeStatuses", EmployeeStatus.values());
        return "care.coordination.contacts";
    }


    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Page<ContactListItemDto> getContacts(@ModelAttribute("contactFilter") ContactFilterDto contactFilter, Pageable pageRequest) {
        Page<ContactListItemDto> result;
        ExchangeUserDetails userDetails = SecurityUtils.getAuthenticatedUser();

        if (!SecurityUtils.isAffiliatedView()) {
            if (SecurityUtils.hasAnyRole(userDetails.getAuthoritiesForDatabase(userDetails.getCurrentDatabaseId()),CareTeamRoleCode.ROLES_CAN_VIEW_CONTACTS)) {
                result = contactService.list(contactFilter, pageRequest);
            } else {
                contactFilter.setEmployeeIds(SecurityUtils.getAuthenticatedUser().getEmployeeIdsForCurrentDatabase());
                result = contactService.list(contactFilter, pageRequest);
            }
        } else {
            boolean canViewContacts = SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR);
            Set<Long> affiliatedOrgIds = organizationService.getAffiliatedOrgIds(userDetails.getCurrentDatabaseId());
            if (CollectionUtils.isNotEmpty(affiliatedOrgIds)) {
                for (Long affiliatedOrgId : affiliatedOrgIds) {
                    canViewContacts = canViewContacts ||  SecurityUtils.hasAnyRole(userDetails.getAuthoritiesForDatabase(affiliatedOrgId), CareTeamRoleCode.ROLES_CAN_VIEW_CONTACTS);
                }
            }
            if (canViewContacts) {
                result = contactService.list(contactFilter, pageRequest);
            } else {
                return new PageImpl<ContactListItemDto>(new ArrayList<ContactListItemDto>());
            }
        }

        return result;
    }

    @ModelAttribute("careTeamRoles")
    public List<CareTeamRoleDto> getCareTeamRoles() {
        return careTeamRoleService.getAllCareTeamRoles();
    }

}
