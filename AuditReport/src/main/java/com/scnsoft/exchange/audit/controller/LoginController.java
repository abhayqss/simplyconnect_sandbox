package com.scnsoft.exchange.audit.controller;


import com.scnsoft.exchange.audit.model.LoginForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String index(@ModelAttribute("loginForm") LoginForm form, Model model) {
        model.addAttribute("loginForm", new LoginForm());
        return "login";
    }
}
