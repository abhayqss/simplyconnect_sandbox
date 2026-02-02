package com.scnsoft.eldermark.web.controller.carecoordination;

import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.entity.Employee;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;

//@Controller
//@RequestMapping(value = "/reports")
public class ReportsController {

    @Value("${kyubitUrl}")
    private String kyubitUrl;

    @Value("${microsoftUrl}")
    private String microsoftUrl;

    @Value("${domain}")
    private String txtDomain;

    @Value("${password}")
    private String txtUserPassword;

    @Value("${view.state}")
    private String viewState;

    @Value("${view.state.generator}")
    private String viewStateGenerator;

    @Value("${event.validation}")
    private String eventValidation;

    @Value("classpath:reports-menu.json")
    private Resource reportsMenu;

    @RequestMapping(method = RequestMethod.GET)
    public String initKyubitView(Model model) {
        Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
        model.addAttribute("kyubitUrl", kyubitUrl);
        model.addAttribute("microsoftUrl", microsoftUrl);
        model.addAttribute("txtDomain", txtDomain);
        model.addAttribute("txtUserName", "SC_USER-" + employee.getDatabaseId() + "-" + employee.getId());
        model.addAttribute("txtUserPassword", txtUserPassword);
        model.addAttribute("viewState", viewState);
        model.addAttribute("viewStateGenerator", viewStateGenerator);
        model.addAttribute("eventValidation", eventValidation);
        return "reports";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/get-menu/")
    public @ResponseBody
    String getMenu() throws IOException {
        InputStream is = reportsMenu.getInputStream();
        return IOUtils.toString(is);
    }
}
