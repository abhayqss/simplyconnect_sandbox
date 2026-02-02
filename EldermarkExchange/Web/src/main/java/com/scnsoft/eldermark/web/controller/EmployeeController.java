package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.facades.DatabasesFacade;
import com.scnsoft.eldermark.facades.EmployeeFacade;
import com.scnsoft.eldermark.shared.EmployeeDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

//@Controller
//@RequestMapping(value = "/employee")
//@PreAuthorize("isAuthenticated()")
public class EmployeeController {
    @Autowired
    private EmployeeFacade employeeFacade;

    @Autowired
    private DatabasesFacade databasesFacade;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public EmployeeDto getEmployee() {
        return employeeFacade.getLoggedInEmployee();
    }

    @RequestMapping(value = "/company", method = RequestMethod.GET)
    @ResponseBody
    public String getCompany() {
        return databasesFacade.getDatabaseById(employeeFacade.getLoggedInEmployee().getDatabaseId()).getName();
    }

}
