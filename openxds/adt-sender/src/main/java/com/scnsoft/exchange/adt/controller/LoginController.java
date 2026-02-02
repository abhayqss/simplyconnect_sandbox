package com.scnsoft.exchange.adt.controller;


import com.scnsoft.exchange.adt.LoginForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @PreAuthorize("isAuthenticated()")
    public String authIndex() {
        return "home";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String index(@ModelAttribute("loginForm") LoginForm form, Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }
}
