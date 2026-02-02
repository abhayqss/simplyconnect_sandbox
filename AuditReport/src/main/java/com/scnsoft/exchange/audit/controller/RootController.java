package com.scnsoft.exchange.audit.controller;


import com.scnsoft.exchange.audit.security.SecurityUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_SUPER_MANAGER')")
@RequestMapping(value = "/")
public class RootController {

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView root() {
        if(SecurityUtils.hasRole("ROLE_SUPER_MANAGER")) {
            return new ModelAndView("home");
        } else {
            return new ModelAndView("redirect:/logs");
        }
    }

    @RequestMapping(value="/home", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_SUPER_MANAGER')")
    public String home() {
        return "home";
    }
}
