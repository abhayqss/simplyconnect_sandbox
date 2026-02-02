package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityExpressions;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.services.*;
import com.scnsoft.eldermark.services.beans.DocumentMetadata;
import com.scnsoft.eldermark.shared.DocumentDto;
import com.scnsoft.eldermark.shared.SearchScope;
import com.scnsoft.eldermark.shared.SharingOption;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.eldermark.shared.form.DocumentFilter;
import com.scnsoft.eldermark.shared.form.UploadDocumentForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Boolean.TRUE;


//@Controller
//@RequestMapping(value = "/patient-info/{residentId}/documents")
//@PreAuthorize(SecurityExpressions.IS_ELDERMARK_USER)
public class DocumentController {

    @Autowired
    private DocumentFacade documentFacade;

    @RequestMapping(value = "/init/{aggregated}", method = RequestMethod.GET)
    public String initAggregatedView(@PathVariable(value = "residentId") Long residentId,
                                     @PathVariable(value = "aggregated") Boolean aggregated,
                           Model model, HttpServletResponse response) {
        response.addCookie(new Cookie("fileDownload", "true"));
        model.addAttribute("residentId", residentId);
        model.addAttribute("documentFilter", new DocumentFilter());
        model.addAttribute("searchScope", SearchScope.ELDERMARK.getCode());
        model.addAttribute("aggregated", aggregated);
        model.addAttribute("showMessageCompose", SecurityUtils.isEldermarkUser());

        return "documents.view";
    }

    @RequestMapping(value = "/{aggregated}/total", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Long total(@ModelAttribute("documentFilter") DocumentFilter documentFilter,
                      @PathVariable(value = "residentId") Long residentId,
                      @PathVariable(value = "aggregated") Boolean aggregated) {

        return documentFacade.getDocumentCount(residentId, null, TRUE.equals(aggregated));
    }

    @RequestMapping(value = "/{aggregated}/results", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Page<DocumentDto> search(@ModelAttribute("documentFilter") DocumentFilter documentFilter,
                                    @PathVariable(value = "residentId") Long residentId,
                                    @PathVariable(value = "aggregated") Boolean aggregated,
                                    Pageable pageRequest) {
        String documentTitle = documentFilter.getDocumentTitle();

        return documentFacade.queryForDocuments(residentId, documentTitle, pageRequest, TRUE.equals(aggregated));
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public void uploadDocument(@PathVariable(value = "residentId") Long residentId,
                               @ModelAttribute(value = "uploadDocumentForm") final UploadDocumentForm uplForm,
                               @AuthenticationPrincipal ExchangeUserDetails userDetails) {

        final CommonsMultipartFile doc = uplForm.getDocument();
        DocumentMetadata documentMetadata = new DocumentMetadata.Builder()
                .setDocumentTitle(doc.getOriginalFilename())
                .setFileName(doc.getOriginalFilename())
                .setMimeType(doc.getContentType())
                .build();

        List<Long> idsOfDatabasesToShareWith = new ArrayList<Long>();
        if (SharingOption.MY_COMPANY.equals(uplForm.getSharingOption())) {
            idsOfDatabasesToShareWith.add(userDetails.getCurrentDatabaseId());
        }

        long authorId = userDetails.getEmployeeId();

        documentFacade.saveDocument(
                documentMetadata,
                residentId,
                authorId,
                SharingOption.ALL.equals(uplForm.getSharingOption()),
                idsOfDatabasesToShareWith, new SaveDocumentCallbackImpl() {
            @Override
            public void saveToFile(File file) {
                try {
                    FileCopyUtils.copy(doc.getInputStream(), new FileOutputStream(file));
                } catch (IOException e) {
                    throw new FileIOException("Failed to save file " + doc.getOriginalFilename(), e);
                }
            }
        });
    }

    @RequestMapping(value = "/{reportType}", method = RequestMethod.GET)
    public void viewReport(@PathVariable(value = "residentId") Long residentId,
                           @PathVariable(value = "reportType") String reportType,
                           @RequestParam(value = "aggregated") Boolean aggregated,
                              HttpServletResponse response) {
        documentFacade.downloadOrViewReport(residentId, reportType, response, false, aggregated);
    }

    @RequestMapping(value = "/{reportType}/download", method = RequestMethod.GET)
    public void downloadReport(@PathVariable(value = "residentId") Long residentId,
                               @PathVariable(value = "reportType") String reportType,
                               @RequestParam(value = "aggregated") Boolean aggregated,
                               @RequestParam(value = "timeZoneOffset") Integer timeZoneOffset,
                               HttpServletResponse response) {
        documentFacade.downloadOrViewReport(residentId, reportType, response, false, aggregated, timeZoneOffset != null ? -timeZoneOffset : null);
    }

    @RequestMapping(value = "/custom/{documentId}", method = RequestMethod.GET)
    public void viewDocument(@PathVariable(value = "documentId") Long documentId,
                             HttpServletResponse response) {
        DocumentBean document = documentFacade.findDocument(documentId);
        documentFacade.downloadOrViewCustomDocument(document, response, true);
    }

    @RequestMapping(value = "/custom/{documentId}/meta", method = RequestMethod.GET)
    @ResponseBody
    public String documentMeta(@PathVariable(value = "documentId") Long documentId) {
        DocumentBean document = documentFacade.findDocument(documentId);
        return document.getOriginalFileName();
    }

    @RequestMapping(value = "/custom/{documentId}/download", method = RequestMethod.GET)
    public void downloadDocument(@PathVariable(value = "documentId") Long documentId,
                                 HttpServletResponse response) {
        DocumentBean document = documentFacade.findDocument(documentId);
        documentFacade.downloadOrViewCustomDocument(document, response, false);
    }

    @RequestMapping(value = "/custom/{documentId}/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteDocument(@PathVariable(value = "documentId") Long documentId) {
        documentFacade.deleteDocument(documentId);
    }

}
