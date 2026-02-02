package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.exceptions.EmailAlreadyExistsException;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.CareTeamRoleDto;
import com.scnsoft.eldermark.shared.carecoordination.KeyValueDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.OrganizationListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactDto;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.web.session.SecureMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pzhurba on 29-Oct-15.
 */
//@Controller
//@RequestMapping(value = "/care-coordination/contacts/contact/{contactId}")
//@PreAuthorize(SecurityExpressions.IS_CC_USER)
public class CareCoordinationContactController {
    @Autowired
    private ContactService contactService;
    @Autowired
    private CareTeamRoleService careTeamRoleService;
    @Autowired
    private StateService stateService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private CommunityCrudService communityCrudService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRequestService employeeRequestService;
    @Autowired
    private SecureMessagingConfig messagingConfig;
    @Autowired
    private DatabasesService databasesService;

    @RequestMapping(method = RequestMethod.GET)
    public String initContactsNewView(@PathVariable("contactId") Long contactId, Model model) {
        //TODO security check
        List<CareTeamRoleDto> getCareTeamRolesToEdit;
        if (contactId == null || contactId == 0L) {
            ContactDto dto = new ContactDto();
            dto.setOrganization(new KeyValueDto(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId(),SecurityUtils.getAuthenticatedUser().getCurrentDatabaseName()));
            model.addAttribute("contactDto", dto);
            getCareTeamRolesToEdit = contactService.getAllCareTeamRolesToEdit();
        } else {
            ContactDto dto = contactService.getContact(contactId);
            model.addAttribute("contactDto", dto);
            model.addAttribute("unaffiliatedOrg", SecurityUtils.isUnAffiliatedOrg());
            getCareTeamRolesToEdit = contactService.getCareTeamRolesToEdit(dto);
            if (contactService.isValidContact(dto)) {
                model.addAttribute("expired", dto.getExpired());
            }

        }
        model.addAttribute("careTeamRolesToEdit", getCareTeamRolesToEdit);

        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR) && (contactId == null || contactId == 0L)) {
            List<OrganizationListItemDto> orgsSource = organizationService.list(new OrganizationFilterDto(), new PageRequest(0,1000)).getContent();
            List<KeyValueDto> orgs = new ArrayList<KeyValueDto>();
            for (OrganizationListItemDto dto : orgsSource) {
                orgs.add(new KeyValueDto(dto.getId(), dto.getName()));
            }
            model.addAttribute("organizations", orgs);
        }
        if (SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_VIEW_ALL_COMMUNITIES)) {
            List<CommunityListItemDto> comsSource = communityCrudService.listDto().getContent();
            List<KeyValueDto> coms = new ArrayList<KeyValueDto>();
            for (CommunityListItemDto dto : comsSource) {
                coms.add(new KeyValueDto(dto.getId(), dto.getName()));
            }
            model.addAttribute("communities", coms);
//            model.addAttribute("communities",communityCrudService.listDto(new PageRequest(0, 1000)).getContent());
        }
        Long currentDatabaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        Long unafilliatedDatabaseId = databasesService.getUnaffiliatedDatabase() != null ? databasesService.getUnaffiliatedDatabase().getId() : null;
        model.addAttribute("showPhrSM", currentDatabaseId != unafilliatedDatabaseId);
        model.addAttribute("unafilliatedDatabaseId", unafilliatedDatabaseId);

        return "care.coordination.contacts.edit";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Long saveContact(@PathVariable("contactId") Long contactId, @ModelAttribute("contactDto") ContactDto contact, final HttpServletRequest request) {

        Long contactDatabaseId = isUserCanEditContact(contact);
        Database contactDatabase = databasesService.getDatabaseById(contactDatabaseId);
        final ContactDto result = contactService.createOrUpdate(SecurityUtils.getAuthenticatedUser().getEmployee(), contact, contactDatabase, false);
        if (result.getOldRoleCode() != null && !result.getOldRoleCode().equals(result.getRoleCode()) && result.getId().equals(SecurityUtils.getAuthenticatedUser().getEmployeeId())) {
            request.getSession().invalidate();
        }
        if (result.getOldSecureMessaging() != null && !result.getOldSecureMessaging().equals(result.getSecureMessaging()) && result.getId().equals(SecurityUtils.getAuthenticatedUser().getEmployeeId())) {
            SecurityUtils.updatePrincipal(employeeService.getEmployee(SecurityUtils.getAuthenticatedUser().getEmployeeId()));
            messagingConfig.resetCache();
        }

        return result.getId();
    }

    private Long isUserCanEditContact(ContactDto contact) {
        if (!CareTeamRoleCode.isRoleAbleForEditing(SecurityUtils.getCareTeamRoleCodes(), careTeamRoleService.get(contact.getRole().getId()).getCode())) {
            throw new BusinessAccessDeniedException("You can't create / edit role for this user.");
        }
        Long contactDatabaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        if (contact.getId() == null && SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            contactDatabaseId = contact.getOrganization().getId();
        }
        if (contact.getId()!=null) {
            ContactDto contactOld = contactService.getContact(contact.getId());
            CareTeamRoleCode oldRoleCode = contactOld.getRoleCode();
            if ((oldRoleCode!=null) && (!CareTeamRoleCode.isRoleAbleForEditing(SecurityUtils.getCareTeamRoleCodes(), oldRoleCode))) {
                throw new BusinessAccessDeniedException("You can't create / edit role for this user.");
            }

            if (!SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_ADD_EDIT_ALL_CONTACTS)
                    && !(SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_EDIT_COMMUNITY_CONTACTS) && SecurityUtils.getAuthenticatedUser().getEmployeeCommunitiesForCurrentDatabase().contains(contact.getCommunityId()))
                    && !SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds().contains(contact.getId())) {
                throw new BusinessAccessDeniedException("You can't create / edit role for this user.");
            }
            contactDatabaseId = contactOld.getOrganization().getId();
        }
        return contactDatabaseId;
    }

    @RequestMapping(method=RequestMethod.POST, value="/sendNewInvitation")
    public @ResponseBody void sendNewInvitation(@PathVariable("contactId") Long contactId) {
//        if (!SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR) && !SecurityUtils.hasRole(CareTeamRoleCode.ADMINISTRATOR)) {
//            throw new BusinessAccessDeniedException("You can't create / edit role for this user.");
//        }
        ContactDto contact = contactService.getContact(contactId);
        isUserCanEditContact(contact);

        employeeRequestService.sendNewInvitation(contactId);
//        EmployeeRequest employeeRequest = employeeRequestService.getInviteToken(dto.getToken());
//        Employee employee = employeeRequest.getTargetEmployee();
//        return employeePasswordSecurityService.isComplexityValid(dto.getPassword(), employee.getDatabaseId());
    }

    @ExceptionHandler(value = EmailAlreadyExistsException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        return e.getMessage();
    }

    @ModelAttribute("careTeamRoles")
    public List<CareTeamRoleDto> getCareTeamRoles() {
        return contactService.getAllCareTeamRolesToEdit();
    }

//    @RequestMapping(value = "/communities/", method = RequestMethod.GET)
//    public String getCreateCareTeamMemberTemplate(@PathVariable("contactId") Long contactId, Model model) {
//        if (SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_VIEW_ALL_COMMUNITIES)) {
//            List<CommunityListItemDto> comsSource = communityCrudService.listDto().getContent();
//            List<KeyValueDto> coms = new ArrayList<KeyValueDto>();
//            for (CommunityListItemDto dto : comsSource) {
//                coms.add(new KeyValueDto(dto.getId(), dto.getName()));
//            }
//            model.addAttribute("communities", coms);
////            model.addAttribute("communities",communityCrudService.listDto(new PageRequest(0, 1000)).getContent());
//        }
//    }

    @ModelAttribute("states")
    public List<KeyValueDto> getStates() {
        return stateService.getStates();
    }
}
