package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.services.carecoordination.ContactService;
import com.scnsoft.eldermark.services.carecoordination.EmployeeRequestService;
import com.scnsoft.eldermark.services.password.DatabasePasswordSettingsService;
import com.scnsoft.eldermark.shared.carecoordination.service.ResetPasswordDto;
import com.scnsoft.eldermark.web.exception.TokenNotExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;

/**
 * Created by pzhurba on 04-Nov-15.
 */
//@RequestMapping("/service/invite")
//@Controller
public class InviteController {
    private static final Logger logger = LoggerFactory.getLogger(InviteController.class);

    @Value("${portal.url}")
    private String loginUrl;

    @Autowired
    private EmployeeRequestService employeeRequestService;
    @Autowired
    private ContactService contactService;

    @Autowired
    private DatabasePasswordSettingsService databasePasswordSettingsService;

    @RequestMapping(method = RequestMethod.GET)
    public String getView(@RequestParam("token") String token, Model model) {
        try {
            model.addAttribute("resetPasswordDto", employeeRequestService.createInviteDto(token));
            EmployeeRequest employeeRequest = employeeRequestService.getInviteToken(token);
            Long databaseId = employeeRequest.getTargetEmployee().getDatabase().getId();
            model.addAttribute("organizationPasswordSettings", databasePasswordSettingsService.getOrganizationPasswordSettingsDto(databaseId));
            model.addAttribute("expired",employeeRequest.getTargetEmployee().getStatus().equals(EmployeeStatus.EXPIRED));
            model.addAttribute("validContact", contactService.isValidContact(employeeRequest.getTargetEmployee()));

        } catch (NoResultException e) {
            throw new TokenNotExistsException("The link has been already redeemed.");
        }
        return "care.coordination.invite";
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public void useToken(@ModelAttribute("resetPasswordDto") ResetPasswordDto dto) {
        employeeRequestService.useInviteToken(dto);
    }

    @RequestMapping(value = "/{token}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteRequest(@PathVariable("token") String token) {
        employeeRequestService.declineInviteRequest(token);
    }


//    @ExceptionHandler(value = Exception.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    ModelAndView handleException(Exception e) {
//        logger.info("Some error", e);
//        ModelAndView model = new ModelAndView();
//        model.addObject("errMsg", e.getMessage());
//        model.setViewName("care.coordination.error");
//        return model;
//    }

    @ModelAttribute(value = "loginUrl") String getLoginUrl(){
        return loginUrl + "login";
    }

}
