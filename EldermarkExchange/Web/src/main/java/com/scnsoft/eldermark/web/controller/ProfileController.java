package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.CareTeamRoleCode;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.security.ExtraParamAuthenticationFilter;
import com.scnsoft.eldermark.services.carecoordination.ContactService;
import com.scnsoft.eldermark.services.carecoordination.EmployeeRequestService;
import com.scnsoft.eldermark.services.exceptions.TimedLockedException;
import com.scnsoft.eldermark.services.password.DatabasePasswordSettingsService;
import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.shared.carecoordination.CredentialsDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.ContactDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.NewAccountLinkedDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//@Controller
//@RequestMapping(value = "/profile")
//@SessionAttributes({"linkedEmployees","contactDto"})
public class ProfileController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private ContactService contactService;

    @Autowired
    private EmployeeRequestService employeeRequestService;

    @Autowired
    private DatabasePasswordSettingsService databasePasswordSettingsService;

    @Autowired
    private EmployeePasswordSecurityService employeePasswordSecurityService;

    @RequestMapping(value = "/show", method = RequestMethod.GET)
    public String initView(Model model) {
        Long employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
        String newUserToken = SecurityUtils.getAuthenticatedUser().getNewAccountToLinkToken();
        Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);

        model.addAttribute("contactDto", contactService.getContact(employeeId));
        List<LinkedContactDto> linkedEmployees = contactService.getLinkedEmployees(employeeId);
        model.addAttribute("linkedEmployees", linkedEmployees != null ? linkedEmployees : new ArrayList<LinkedContactDto>());
        model.addAttribute("newUserToLink", !StringUtils.isEmpty(newUserToken));
        model.addAttribute("showLinkButton", !SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_ALL_ADMINISTRATORS));
        return "profile.view";
    }

    @RequestMapping(value = "/link",method = RequestMethod.GET)
    public String getLinkView(Model model) {
        model.addAttribute("credentialsDto", new CredentialsDto());
        return "profile.accounts.link";
    }

    @RequestMapping(value = "/linkedlist",method = RequestMethod.GET)
    @ResponseBody
    public Page<LinkedContactDto> getLinkedEmployeesList(Model model, @ModelAttribute("linkedEmployees") List<LinkedContactDto> linkedEmployees) {
        if (CollectionUtils.isEmpty(linkedEmployees)) {
            List<LinkedContactDto> result = contactService.getLinkedEmployees(SecurityUtils.getAuthenticatedUser().getEmployeeId());
            model.addAttribute("linkedEmployees", result != null ? result : new ArrayList<LinkedContactDto>());
            return new PageImpl<LinkedContactDto>(result);
        } else {
            return new PageImpl<LinkedContactDto>(linkedEmployees);
        }
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public String saveContact(Model model, @ModelAttribute("credentialsDto") CredentialsDto contactCredentials, @ModelAttribute("linkedEmployees") List<LinkedContactDto> linkedEmployees) {
        Long employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

        String companyAndUser = contactCredentials.getCompany() + ExtraParamAuthenticationFilter.getDelimiter() + contactCredentials.getUsername();
        UsernamePasswordAuthenticationToken authAttemptToken = new UsernamePasswordAuthenticationToken(companyAndUser, contactCredentials.getPassword());
        try {
            Authentication authResult = authenticationManager.authenticate(authAttemptToken);
            Employee employeeToLink = ((ExchangeUserDetails) authResult.getPrincipal()).getEmployee();
            Long linkedEmployeeId = employeeToLink.getId();
            //TODO response with statuses
            if (employeeId.equals(linkedEmployeeId)) {
                return "Current account can't be linked with itself.";
            }
            if (authResult.getAuthorities() != null && authResult.getAuthorities().contains(new SimpleGrantedAuthority(CareTeamRoleCode.SUPER_ADMINISTRATOR))) {
                return "User with Super Administrator privilege can't be linked.";
            }
            for (LinkedContactDto linkedContact : linkedEmployees) {
                if (linkedEmployeeId.equals(linkedContact.getId())) {
                    return "Account is already linked.";
                }
            }
            LinkedContactDto linkedEmployee = contactService.createLinkedEmployee(SecurityUtils.getAuthenticatedUser().getEmployee(), employeeToLink);
            //we should reload linked users since adding a single user may cause adding a chain of previously linked users
            List<LinkedContactDto> result = contactService.getLinkedEmployees(SecurityUtils.getAuthenticatedUser().getEmployeeId());
            model.addAttribute("linkedEmployees", result != null ? result : new ArrayList<LinkedContactDto>());
        } catch (BadCredentialsException e) {
            return "Account associated with entered credentials does not exist in the system.";
        } catch (TimedLockedException e) {
            final long durationMin = new Double(Math.ceil(e.getDurationMs() / (60.0 * 1000.0))).longValue();
            return "This account has been locked out because you have reached the maximum number of attempts to enter credentials. Please try again in " + durationMin + " minutes.";
        } catch (CredentialsExpiredException e) {
            return "Password for this account has been expired. Please log out and change the password.";
        }
        return "Success";
    }

    @RequestMapping(value = "/linkeddetails",method = RequestMethod.GET)
    public String getLinkedEmployeesDetails(Model model) {
        List<LinkedContactDto> result = contactService.getLinkedEmployees(SecurityUtils.getAuthenticatedUser().getEmployeeId());
        model.addAttribute("linkedEmployees", result != null ? result : new ArrayList<LinkedContactDto>());
        return "profile.accounts.linked.details";
    }

    @RequestMapping(value = "/common-profile-info",method = RequestMethod.GET)
    @ResponseBody
    public ContactDto getCommonProfileInfo(Model model) {
        Long employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();

        ContactDto currentContact = contactService.getContact(employeeId);

        model.addAttribute("contactDto", currentContact);
        return currentContact;
    }

    @RequestMapping(value = "/unlink/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public String getLinkedEmployeesList(Model model, @PathVariable Long id, @ModelAttribute("linkedEmployees") List<LinkedContactDto> linkedEmployees) {
        Long employeeId = SecurityUtils.getAuthenticatedUser().getEmployeeId();
        contactService.deleteLinkedEmployee(id, employeeId);
        List<LinkedContactDto> result = contactService.getLinkedEmployees(SecurityUtils.getAuthenticatedUser().getEmployeeId());
        model.addAttribute("linkedEmployees", result != null ? result : new ArrayList<LinkedContactDto>());
        return "{\"result\":\"Ok\"}";
    }

    //for new accounts
    @RequestMapping(value = "/linkcreated",method = RequestMethod.GET)
    public String getLinkCreatedView(Model model) {
        String newUserToken = SecurityUtils.getAuthenticatedUser().getNewAccountToLinkToken();
        NewAccountLinkedDto newAccountLinkedDto = employeeRequestService.createNewAccountLinkedDto(newUserToken);
        Long databaseId = newAccountLinkedDto.getDatabaseId();
        model.addAttribute("newAccountLinkedDto", newAccountLinkedDto);
        model.addAttribute("organizationPasswordSettings", databasePasswordSettingsService.getOrganizationPasswordSettingsDto(databaseId));
        return "profile.accounts.linkcreated";
    }

    @RequestMapping(method=RequestMethod.POST, value="/validatePasswordComplexity")
    public @ResponseBody Boolean validatePasswordComplexity(@ModelAttribute("newAccountLinkedDto") NewAccountLinkedDto newAccountLinkedDto) {
        return employeePasswordSecurityService.isComplexityValid(newAccountLinkedDto.getConfirmPassword(), newAccountLinkedDto.getDatabaseId());
    }

    @RequestMapping(value = "/createandlink", method = RequestMethod.POST)
    @ResponseBody
    public String createAndLink(Model model, @ModelAttribute("newAccountLinkedDto") NewAccountLinkedDto newAccountLinkedDto, @ModelAttribute("linkedEmployees") List<LinkedContactDto> linkedEmployees) {
        Employee employeeToLink = employeeRequestService.useInviteToken(newAccountLinkedDto);
        Long linkedEmployeeId = employeeToLink.getId();
        LinkedContactDto linkedEmployee = contactService.createLinkedEmployee(SecurityUtils.getAuthenticatedUser().getEmployee(), employeeToLink);
        List<LinkedContactDto> result = contactService.getLinkedEmployees(SecurityUtils.getAuthenticatedUser().getEmployeeId());
        model.addAttribute("linkedEmployees", result != null ? result : new ArrayList<LinkedContactDto>());
        return "Success";
    }
}
