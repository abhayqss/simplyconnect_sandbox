package com.scnsoft.eldermark.web.controller;


import com.scnsoft.eldermark.entity.EmployeeRequest;
import com.scnsoft.eldermark.entity.EmployeeStatus;
import com.scnsoft.eldermark.services.carecoordination.EmployeeRequestService;
import com.scnsoft.eldermark.services.password.DatabasePasswordSettingsService;
import com.scnsoft.eldermark.shared.carecoordination.ChangePasswordDto;
import com.scnsoft.eldermark.shared.form.LoginForm;
import com.scnsoft.eldermark.web.exception.TokenNotExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

//@Controller
public class LoginController {

    @Autowired
    private EmployeeRequestService employeeRequestService;

    @Autowired
    private DatabasePasswordSettingsService databasePasswordSettingsService;

    @RequestMapping(value = "/login")
    public String initView(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("linkExisting", Boolean.FALSE);
        return "login.loginPage";
    }

    @RequestMapping(value = "/login-new-password")
    public String loginNewPassword(Model model) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("linkExisting", Boolean.FALSE);
        model.addAttribute("pwdChanged", Boolean.TRUE);
        return "login.loginPage";
    }

    @RequestMapping(value = "/change-password")
    public String changePassword(HttpSession session, Model model) {
        String orgName = session.getAttribute("last_company").toString();
        String username = session.getAttribute("last_username").toString();
        model.addAttribute("changePasswordDto", new ChangePasswordDto());
        model.addAttribute("organizationPasswordSettings", databasePasswordSettingsService.getOrganizationPasswordSettingsDtoForEmployee(username, orgName));
        return "login.changePassword";
    }

    @RequestMapping(value = "/loginlink/{token}")
    public ModelAndView loginLinkExisting(Model model, @PathVariable("token") String token, HttpServletResponse response) {
        model.addAttribute("loginForm", new LoginForm());
        model.addAttribute("linkExisting", Boolean.TRUE);
        model.addAttribute("token", token);
        try {
            EmployeeRequest employeeRequest = employeeRequestService.getInviteToken(token);
            if (employeeRequest.getTargetEmployee().getStatus().equals(EmployeeStatus.EXPIRED)) {
                //throw new TokenNotExistsException("The link has been already redeemed.");
                return  new ModelAndView("redirect:/service/invite","token",token);
            }
        } catch (NoResultException e) {
            throw new TokenNotExistsException("The link has been already redeemed.");
        }
        return new ModelAndView("login.loginPage");
    }

    @RequestMapping(value = "/access-denied")
    public String accessDeniedView() {
        return "accessDenied.view";
    }

    @RequestMapping(value = "/session-active")
    @ResponseBody
    public Boolean sessionActive() {
        return true;
    }
}
