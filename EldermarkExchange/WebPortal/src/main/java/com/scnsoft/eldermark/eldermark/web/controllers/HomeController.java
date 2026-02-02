package com.scnsoft.eldermark.eldermark.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * @author stsiushkevich
 */
@RestController
public class HomeController {
    @GetMapping("/home")
    public String home() {
        return "Hello, the time at the server is now " + new Date() + "\n";
    }
}