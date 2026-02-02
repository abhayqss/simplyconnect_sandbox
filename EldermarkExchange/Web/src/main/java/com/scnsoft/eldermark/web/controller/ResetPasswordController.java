package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.services.EmployeeService;
import com.scnsoft.eldermark.services.carecoordination.ContactService;
import com.scnsoft.eldermark.services.carecoordination.EmployeeRequestService;
import com.scnsoft.eldermark.services.password.DatabasePasswordSettingsService;
import com.scnsoft.eldermark.services.password.EmployeePasswordSecurityService;
import com.scnsoft.eldermark.shared.carecoordination.ChangePasswordDto;
import com.scnsoft.eldermark.shared.carecoordination.service.ResetPasswordDto;
import com.scnsoft.eldermark.shared.carecoordination.service.ResetPasswordRequestDto;
import com.scnsoft.eldermark.web.exception.TokenNotExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.NoResultException;

/**
 * Created by pzhurba on 09-Nov-15.
 */
//@RequestMapping("/service")
//@Controller
public class ResetPasswordController {
    private static final Logger logger = LoggerFactory.getLogger(ResetPasswordController.class);

    @Value("${portal.url}")
    private String loginUrl;

    @Autowired
    private ContactService contactService;
    @Autowired
    private EmployeeRequestService employeeRequestService;
    @Autowired
    private EmployeePasswordSecurityService employeePasswordSecurityService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DatabasePasswordSettingsService databasePasswordSettingsService;


    @RequestMapping(value = "/resetRequest", method = RequestMethod.GET)
    public String getResetRequestView(final Model model) {
        logger.info("Request to reset password view");
        model.addAttribute("dto", new ResetPasswordRequestDto());

        return "care.coordination.reset.request";
    }

    @RequestMapping(value = "/resetRequest", method = RequestMethod.POST)
    @ResponseBody
    public void createResetPasswordRequest(@ModelAttribute("dto") ResetPasswordRequestDto dto) {
        logger.info("Request to reset password");
        contactService.createResetPasswordToken(dto.getEmail(), dto.getCompanyCode());
    }


    @RequestMapping(value= "/reset", method = RequestMethod.GET)
    public String getResetView(@RequestParam("token") String token, Model model) {
        try {
            model.addAttribute("resetPasswordDto", employeeRequestService.createResetPasswordDto(token));
            EmployeeRequest employeeRequest = employeeRequestService.getResetToken(token);
            Long databaseId = employeeRequest.getTargetEmployee().getDatabaseId();
            model.addAttribute("organizationPasswordSettings", databasePasswordSettingsService.getOrganizationPasswordSettingsDto(databaseId));
        } catch (NoResultException e) {
            throw new TokenNotExistsException("The link has been already redeemed.");
        }
        return "care.coordination.reset";
    }

    @RequestMapping(value= "/reset",method = RequestMethod.POST)
    @ResponseBody
    public void useToken(@ModelAttribute("resetPasswordDto") ResetPasswordDto dto) {
        employeeRequestService.useResetPasswordToken(dto);
    }

    @RequestMapping(value = "/reset/{token}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteRequest(@PathVariable("token") String token) {
        employeeRequestService.declineResetPasswordToken(token);
    }

    @RequestMapping(value= "/change",method = RequestMethod.POST)
    public String changePassword(@ModelAttribute("changePasswordDto") ChangePasswordDto dto, Model model, RedirectAttributes redirectAttributes) {
        Boolean valid = employeePasswordSecurityService.isPasswordValid(dto.getCompany(), dto.getUsername(), dto.getNewPassword());
        if (!valid) {
            redirectAttributes.addFlashAttribute("errorMsg", "New password is not valid.");
            return "redirect:/change-password";
        }
        Employee employee = employeeService.getActiveEmployee(dto.getUsername(), dto.getCompany());
        Long employeeId = employee.getId();
        try {
            employeeService.updatePassword(employeeId, dto.getPassword(), dto.getNewPassword());
        } catch (BadCredentialsException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Incorrect current password.");
            return "redirect:/change-password";
        }
        return "redirect:/login-new-password";
    }

    @RequestMapping(method=RequestMethod.POST, value="/validatePasswordHistory")
    public @ResponseBody Boolean validatePasswordHistory(@ModelAttribute ChangePasswordDto dto) {
        Employee employee = employeeService.getActiveEmployee(dto.getUsername(), dto.getCompany());
        return employeePasswordSecurityService.isHistoryValid(dto.getNewPassword(), employee);
    }

    @RequestMapping(method=RequestMethod.POST, value="/validatePasswordComplexity")
    public @ResponseBody Boolean validatePasswordComplexity(@ModelAttribute ChangePasswordDto dto) {
        Employee employee = employeeService.getActiveEmployee(dto.getUsername(), dto.getCompany());
        return employeePasswordSecurityService.isComplexityValid(dto.getNewPassword(), employee.getDatabaseId());
    }

    @RequestMapping(method=RequestMethod.POST, value="/validatePasswordHistoryReset")
    public @ResponseBody Boolean validatePasswordHistoryReset(@ModelAttribute ResetPasswordDto dto) {
        EmployeeRequest employeeRequest = employeeRequestService.getResetToken(dto.getToken());
        Employee employee = employeeRequest.getTargetEmployee();
        return employeePasswordSecurityService.isHistoryValid(dto.getPassword(), employee);
    }

    @RequestMapping(method=RequestMethod.POST, value="/validatePasswordComplexityReset")
    public @ResponseBody Boolean validatePasswordComplexityReset(@ModelAttribute ResetPasswordDto dto) {
        EmployeeRequest employeeRequest = employeeRequestService.getResetToken(dto.getToken());
        Employee employee = employeeRequest.getTargetEmployee();
        return employeePasswordSecurityService.isComplexityValid(dto.getPassword(), employee.getDatabaseId());
    }

    @RequestMapping(method=RequestMethod.POST, value="/validatePasswordHistoryInvite")
    public @ResponseBody Boolean validatePasswordHistoryInvite(@ModelAttribute ResetPasswordDto dto) {
        EmployeeRequest employeeRequest = employeeRequestService.getInviteToken(dto.getToken());
        Employee employee = employeeRequest.getTargetEmployee();
        return employeePasswordSecurityService.isHistoryValid(dto.getPassword(), employee);
    }

    @RequestMapping(method=RequestMethod.POST, value="/validatePasswordComplexityInvite")
    public @ResponseBody Boolean validatePasswordComplexityInvite(@ModelAttribute ResetPasswordDto dto) {
        EmployeeRequest employeeRequest = employeeRequestService.getInviteToken(dto.getToken());
        Employee employee = employeeRequest.getTargetEmployee();
        return employeePasswordSecurityService.isComplexityValid(dto.getPassword(), employee.getDatabaseId());
    }

    @RequestMapping(method=RequestMethod.POST, value="/sendNewInvitation")
    public @ResponseBody void sendNewInvitation(@ModelAttribute ResetPasswordDto dto) {

        employeeRequestService.sendNewInvitation(dto);
//        EmployeeRequest employeeRequest = employeeRequestService.getInviteToken(dto.getToken());
//        Employee employee = employeeRequest.getTargetEmployee();
//        return employeePasswordSecurityService.isComplexityValid(dto.getPassword(), employee.getDatabaseId());
    }

//    @ExceptionHandler(value = Exception.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    void handleException(Exception e) {
//        logger.info("Some error", e);
//    }

    @ModelAttribute(value = "loginUrl")
    String getLoginUrl() {
        return loginUrl + "login";
    }

}
