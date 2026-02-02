package com.scnsoft.eldermark.web.controller;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.facades.DirectMessagesFacade;
import com.scnsoft.eldermark.facades.DocumentFacade;
import com.scnsoft.eldermark.facades.ResidentFacade;
import com.scnsoft.eldermark.facades.beans.DocumentBean;
import com.scnsoft.eldermark.facades.ccd.CcdFacade;
import com.scnsoft.eldermark.services.Report;
import com.scnsoft.eldermark.services.ReportGenerator;
import com.scnsoft.eldermark.services.ReportGeneratorFactory;
import com.scnsoft.eldermark.services.direct.DirectAccountDetails;
import com.scnsoft.eldermark.services.direct.DirectAttachment;
import com.scnsoft.eldermark.shared.AddressBookSource;
import com.scnsoft.eldermark.shared.SearchScope;
import com.scnsoft.eldermark.shared.exceptions.DocumentNotFoundException;
import com.scnsoft.eldermark.shared.exceptions.FacesheetGenerationException;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import com.scnsoft.eldermark.shared.exceptions.LocalizedException;
import com.scnsoft.eldermark.shared.form.AddressBookFilter;
import com.scnsoft.eldermark.shared.form.ComposeMessageForm;
import com.scnsoft.eldermark.shared.form.UploadDocumentForm;
import com.scnsoft.eldermark.web.resolvers.MessagingAccount;
import com.scnsoft.eldermark.web.session.SecureMessagingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//@Controller
//@RequestMapping(value = "/patient-info/{residentId}")
//@PreAuthorize(SecurityExpressions.IS_ELDERMARK_USER)
public class PatientInfoController {
    @Autowired
    private ResidentFacade residentFacade;

    @Autowired
    private DocumentFacade documentFacade;

    @Autowired
    private DirectMessagesFacade directMessagesFacade;

    @Autowired
    private SecureMessagingConfig messagingConfig;

    @Autowired
    private CcdFacade ccdFacade;

    @Autowired
    private ReportGeneratorFactory generatorFactory;

    @RequestMapping(method = RequestMethod.GET)
    public String getPatientInfo(@PathVariable(value = "residentId") Long residentId,
                                 @RequestParam("databaseId") String databaseId,
                                 @RequestParam(value = "ccdLink", required = false) Boolean ccdLink,
                                 Model model) {
        System.out.println("PatientInfoController getPatientInfo Started!!!");
        long startTime = System.currentTimeMillis();
        model.addAttribute("searchScope", SearchScope.ELDERMARK.getCode());

        model.addAttribute("residentId", residentId);
        model.addAttribute("databaseId", databaseId);
//        model.addAttribute("fullName", residentFacade.getResidentById(residentId).getFullName());
        model.addAttribute("patient", ccdFacade.getCcdHeaderPatient(residentId, false));

        model.addAttribute("uploadDocumentForm", new UploadDocumentForm());
        model.addAttribute("aggregated", false);
        model.addAttribute("ccdLink", ccdLink);
        model.addAttribute("showMessageCompose", SecurityUtils.isEldermarkUser());

        System.out.println("PatientInfoController getPatientInfo End!!!");
        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
        return "patient.patientInfo";
    }

    @RequestMapping(value = "/aggregated", method = RequestMethod.GET)
    public String getAggregatedPatientInfo(@PathVariable(value = "residentId") Long residentId,
                                           @RequestParam("databaseId") String databaseId,
                                           Model model) {
        model.addAttribute("searchScope", SearchScope.ELDERMARK.getCode());

        model.addAttribute("residentId", residentId);
        model.addAttribute("databaseId", databaseId);
        model.addAttribute("fullName", residentFacade.getResidentById(residentId).getFullName()); //TODO optimize
        model.addAttribute("patient", ccdFacade.getCcdHeaderPatient(residentId, false));

        model.addAttribute("uploadDocumentForm", new UploadDocumentForm());
        model.addAttribute("aggregated", true);

        return "patient.patientInfo";
    }

    @ModelAttribute("addressBookSourceValues")
    public AddressBookSource[] getAddressBookSources() {
        return AddressBookSource.values();
    }

    @RequestMapping(value = "/compose", method = RequestMethod.GET)
    public String initComposeNewView(Model model,
                                     @PathVariable(value = "residentId") Long residentId,
                                     @RequestParam(value = "hashKey") String hashKey,
                                     @RequestParam(value = "documentIds") List<String> documentIds,
                                     @RequestParam(value = "clinical") List<String> reportTypes,
                                     @RequestParam(value = "isCCModule") Boolean isCCModule,
                                     @AuthenticationPrincipal ExchangeUserDetails userDetails,
                                     @MessagingAccount DirectAccountDetails messagingAccount) {
        if (!messagingConfig.isAccountRegistered(userDetails)) {
            return "forward:/secure-messaging/config-warning";
        }

        model.addAttribute("msgDetails", new ComposeMessageForm());

        AddressBookFilter addressBookFilter = new AddressBookFilter();
        addressBookFilter.setAddressBookSource(AddressBookSource.ELDERMARK_EXCHANGE_DIRECTORY);
        model.addAttribute("addressBookFilter", addressBookFilter);

        model.addAttribute("residentId", residentId);
        model.addAttribute("fullName", residentFacade.getResidentById(residentId).getFullName());   //TODO optimize

        String sendUrl = String.format("/patient-info/%s/send?hashKey=%s", residentId, hashKey);
        model.addAttribute("sendMessageUrlTemplate", sendUrl);

        if (isCCModule) {
            return "carecoordination.messaging.compose";
        } else {
            return "patient-info.messaging.compose";
        }
    }

    @ResponseBody
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public List<String> sendMessage(@Valid @ModelAttribute(value = "msgDetails") ComposeMessageForm msgDetails,
                                    @PathVariable(value = "residentId") Long residentId,
                                    @RequestParam(value = "hashKey") String hashKey,
                                    @MessagingAccount DirectAccountDetails messagingAccount) {
        List<DirectAttachment> attachments = new ArrayList<DirectAttachment>();
        List<String> errorList = new ArrayList<String>();

        if (msgDetails.getReportTypes() != null) {
            for (String reportType : msgDetails.getReportTypes()) {
                try {
                    ReportGenerator generator = generatorFactory.getGenerator(reportType);
                    // TODO : build attached report from aggregated data?
                    Report document = generator.generate(residentId, false);

                    DirectAttachment attachment = new DirectAttachment();
                    attachment.setData(FileCopyUtils.copyToByteArray(document.getInputStream()));
                    attachment.setFileName(document.getDocumentTitle());
                    attachment.setContentType(document.getMimeType());
                    attachments.add(attachment);
                } catch (Exception e) {
                    errorList.add(new FacesheetGenerationException().getLocalizedMessage());
                }
            }
        }

        if (msgDetails.getCustomDocumentIds() != null) {
            for (String documentId : msgDetails.getCustomDocumentIds()) {
                try {
                    if (documentFacade.isAttachedToResident(residentId, Long.parseLong(documentId))) {
                        DocumentBean document = documentFacade.findDocument(Long.parseLong(documentId));

                        DirectAttachment attachment = new DirectAttachment();
                        attachment.setContentType(document.getMimeType());
                        attachment.setFileName(document.getOriginalFileName());
                        attachment.setData(FileCopyUtils.copyToByteArray(document.getFile()));
                        attachments.add(attachment);
                    } else {
                        errorList.add(new DocumentNotFoundException(documentId).getLocalizedMessage());
                    }
                } catch (IOException e) {
                    errorList.add(new FileIOException(documentId).getLocalizedMessage());
                } catch (LocalizedException e) {
                    errorList.add(e.getLocalizedMessage());
                }
            }
        }

        if (msgDetails.getFiles() != null) {
            for (CommonsMultipartFile file : msgDetails.getFiles()) {
                DirectAttachment attachment = new DirectAttachment();
                attachment.setContentType(file.getContentType());
                attachment.setData(file.getBytes());
                attachment.setFileName(file.getOriginalFilename());
                attachments.add(attachment);
            }
        }

        if (errorList.isEmpty()) {
            String allRecipients = msgDetails.getTo();
            String subject = msgDetails.getSubject();
            String body = msgDetails.getBody();
            try {
                directMessagesFacade.sendMessage(allRecipients, subject, body, attachments, messagingAccount);
            } catch (Exception e) {
                errorList.add(e.getLocalizedMessage());
            }
        }

        return errorList;
    }
}
