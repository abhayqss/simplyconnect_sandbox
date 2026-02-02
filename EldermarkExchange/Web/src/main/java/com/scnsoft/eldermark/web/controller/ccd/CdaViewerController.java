package com.scnsoft.eldermark.web.controller.ccd;

import com.scnsoft.eldermark.service.DocumentFacadeWeb;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//@Controller
public class CdaViewerController {

    @Autowired
    @Lazy
    private DocumentFacadeWeb documentFacadeWeb;

    @RequestMapping(method = RequestMethod.GET, value = "/documents/{documentId}/cda-view")
    public String processl(@PathVariable("documentId") Long documentId, final Model model) {
        model.addAttribute("content", documentFacadeWeb.getCdaHtmlViewForDocument(documentId));
        return "document.cda-viewer";
    }

}
