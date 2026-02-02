package com.scnsoft.eldermark.web.controller.administration;

import com.scnsoft.eldermark.authentication.SecurityExpressions;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author phomal
 * Created on 31-Mar-17.
 */
//@Controller
//@RequestMapping(value = "/administration")
//@PreAuthorize(SecurityExpressions.IS_CC_SUPERADMIN)
public class AdministrationController {

    @RequestMapping(method = RequestMethod.GET)
    public String initView() {
        return "administration";
    }

}
