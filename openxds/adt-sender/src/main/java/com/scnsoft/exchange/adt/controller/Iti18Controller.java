package com.scnsoft.exchange.adt.controller;


import com.scnsoft.exchange.adt.HttpSender;
import com.scnsoft.exchange.adt.entity.Iti18RequestDto;
import com.scnsoft.exchange.adt.entity.ResponseDto;
import com.scnsoft.exchange.adt.entity.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.*;

@Controller
@RequestMapping(value = "/iti18")
@PreAuthorize("isAuthenticated()")
public class Iti18Controller extends ItiController {


    @Autowired
    HttpSender httpSender;

    //HEADERS
    public static Map<String, String> ITI18_HEADER;
    public static Map<String, String> QUERY_ID_MAP_SAMPLES;
    public static List<String> RETURN_TYPES;
    public static Map<String, String> HEALTHCARE_TYPES;

    private static Map<String, Resource> xmlTemplates = null;

    @ModelAttribute("QUERY_ID_MAP_SAMPLES")
    public Map<String, String> getQueryIdMapSamples() {
        return QUERY_ID_MAP_SAMPLES;
    }

    @ModelAttribute("ITI18_HEADERS")
    public Map<String, String> getIti18Headers() {
        return ITI18_HEADER;
    }

    @ModelAttribute("RETURN_TYPES")
    public List<String> getReturnTypes() {
        return RETURN_TYPES;
    }

    @ModelAttribute("HEALTHCARE_TYPES")
    public Map<String, String> getHealthcareTypes() {
        return HEALTHCARE_TYPES;
    }

    static {
        ITI18_HEADER = new HashMap<String, String>();
        ITI18_HEADER.put("SOAPAction", "urn:ihe:iti:2007:RegistryStoredQuery");
        ITI18_HEADER.put("User-Agent", "Axis2");
        ITI18_HEADER.put("Content-Transfer-Encoding", "Axis2");

        QUERY_ID_MAP_SAMPLES = new HashMap<String, String>();

        QUERY_ID_MAP_SAMPLES.put("urn:uuid:f26abbcb-ac74-4422-8a30-edb644bbc1a9", "FindSubmissionSets");
        QUERY_ID_MAP_SAMPLES.put("urn:uuid:958f3006-baad-4929-a4de-ff1114824431", "FindFolders");
        QUERY_ID_MAP_SAMPLES.put("urn:uuid:14d4debf-8f97-4251-9a74-a90016b0af0d", "FindDocuments");

        QUERY_ID_MAP_SAMPLES.put("urn:uuid:e8e3cb2c-e39c-46b9-99e4-c12f57260b83", "GetSubmissionSetAndContents");
        QUERY_ID_MAP_SAMPLES.put("urn:uuid:5c4f972b-d56b-40ac-a5fc-c8ca9b40b9d4", "GetDocuments");
        QUERY_ID_MAP_SAMPLES.put("urn:uuid:b909a503-523d-4517-8acf-8e5834dfc4c7", "GetFolderAndContents");


        RETURN_TYPES = new ArrayList<String>();
        RETURN_TYPES.add("LeafClass");
        RETURN_TYPES.add("ObjectRef");

        HEALTHCARE_TYPES = new HashMap<String, String>();
        HEALTHCARE_TYPES.put("AMB^^2.16.840.1.113883.5.11", "Ambulance");
        HEALTHCARE_TYPES.put("CATH^^2.16.840.1.113883.5.11", "Cardiac catheterization lab");
        HEALTHCARE_TYPES.put("PEDNEPH^^2.16.840.1.113883.5.11", "Pediatric nephrology clinic");
        HEALTHCARE_TYPES.put("Assisted Living", "Assisted Living");
        HEALTHCARE_TYPES.put("Emergency Department", "Emergency Department");

        xmlTemplates = new HashMap<String, Resource>();
        xmlTemplates.put("Documents", new ClassPathResource("/xmltemplates/iti18documents.xml"));
        xmlTemplates.put("Folders", new ClassPathResource("/xmltemplates/iti18folders.xml"));
        xmlTemplates.put("Submission Sets", new ClassPathResource("/xmltemplates/iti18submissionSets.xml"));


    }

    @RequestMapping(method = RequestMethod.GET)
    public String root(Model model) {
        Iti18RequestDto request = new Iti18RequestDto();
        request.setHost(defaultHost);
        request.setPort("8011");
        request.setCreateTimeFrom("200412252300");
        request.setCreateTimeTo("202501010800");
        request.setPatientId("109^^^&amp;2.16.840.1.113883.3.6492&amp;ISO");
        //request.setHealthCareFacilityTypeCode("healthCareFacilityTypeCode");

        request.setDocumentEntryUniqueId("2.16.840.1.113883.3.6492.105.18");
        request.setDocumentEntryEntryUUID("2.16.840.1.113883.3.6492.1");
        request.setTemplateFor("Documents");

        request.setTemplate(getContentFromFile(xmlTemplates.get(request.getTemplateFor())));

        request.setCreateTimeFromCriteria(true);
        request.setCreateTimeToCriteria(true);
        request.setDocStatusCriteria(true);
        request.setDocumentEntryEntryUUIDCriteria(true);
        request.setDocumentEntryUniqueIdCriteria(true);
        request.setHealthcareFacilityTypeCodeCriteria(true);
        request.setPatientCriteria(true);


        model.addAttribute("requestDto", request);

        return "send-iti-18";
    }

    @RequestMapping(value = "/applyTemplate", method = RequestMethod.POST)
    public @ResponseBody String applyTemplate(Model model,
                                              @ModelAttribute("requestDto") Iti18RequestDto requestDto) {
        String templateSrc = getContentFromFile(xmlTemplates.get(requestDto.getTemplateFor()));
        String messageId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        requestDto.setDocStatusCriteria(true);
        Template template = new Template(templateSrc)
                .addReplacement("messageId", messageId)
                .addReplacement("host", requestDto.getHost())
                .addReplacement("port", requestDto.getPort())
                .addReplacement("returnType", requestDto.getReturnType())
                .addReplacement("storedQueryId", requestDto.getStoredQueryId())
                .addReplacement("patientId", requestDto.getPatientId())
                .addReplacement("createTimeFrom", requestDto.getCreateTimeFrom())
                .addReplacement("createTimeTo", requestDto.getCreateTimeTo())
                .addReplacement("healthCareFacilityTypeCode", requestDto.getHealthCareFacilityTypeCode())
                .addReplacement("documentEntryUniqueId", requestDto.getDocumentEntryUniqueId())
                .addReplacement("documentEntryEntryUUID", requestDto.getDocumentEntryEntryUUID())
                //
                .includeBlock("patientCriteria", requestDto.getPatientCriteria())
                .includeBlock("docStatusCriteria", requestDto.getDocStatusCriteria())
                .includeBlock("createTimeFromCriteria", requestDto.getCreateTimeFromCriteria())
                .includeBlock("createTimeToCriteria", requestDto.getCreateTimeToCriteria())
                .includeBlock("healthcareFacilityTypeCodeCriteria", requestDto.getHealthcareFacilityTypeCodeCriteria())
                .includeBlock("documentEntryUniqueIdCriteria", requestDto.getDocumentEntryUniqueIdCriteria())
                .includeBlock("documentEntryEntryUUIDCriteria", requestDto.getDocumentEntryEntryUUIDCriteria());


        return template.build();
    }


    @RequestMapping(method = RequestMethod.POST)
    public String postRequest(@Valid @ModelAttribute("requestDto") Iti18RequestDto requestDto, Model model) throws Exception {

        ResponseDto dto = httpSender.sendRequest("https://" + requestDto.getHost() + ":" + requestDto.getPort() + "/axis2/services/xdsregistryb", ITI18_HEADER, requestDto.getTemplate());
        model.addAttribute("responseDto", dto);

        return "send-iti-18";
    }

}
