package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.facades.ccd.CcdFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//@Controller
//@RequestMapping(value = "/narrative/ccd/{sectionName}")
public class FreeTextController {

    @Autowired
    private CcdFacade ccdFacade;

    @RequestMapping(value = "/{id}/{fileName}", method = RequestMethod.GET)
    public String showDocDetails(final Model model, @PathVariable("sectionName") String sectionName, @PathVariable("id") Long id,
                                 @PathVariable("fileName") String fileName){
        String freeText = ccdFacade.findFreeTextBySectionAndId(sectionName, id);
        model.addAttribute("title", "Plan Of Care");
        model.addAttribute("content", freeText);
        model.addAttribute("fileName", fileName);

        return "patient.ccdPlanOfCareDetails.view";
    }

}
