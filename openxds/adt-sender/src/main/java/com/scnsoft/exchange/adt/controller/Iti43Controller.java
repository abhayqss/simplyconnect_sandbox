package com.scnsoft.exchange.adt.controller;


import com.scnsoft.exchange.adt.HttpSender;
import com.scnsoft.exchange.adt.entity.Iti43RequestDto;
import com.scnsoft.exchange.adt.entity.ResponseDto;
import com.scnsoft.exchange.adt.entity.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/iti43")
@PreAuthorize("isAuthenticated()")
public class Iti43Controller extends ItiController {

    //HEADERS
    public static Map<String, String> ITI43_HEADERS;
    private static Resource xmlTemplate = null;

    @Autowired
    HttpSender httpSender;

    static {
        ITI43_HEADERS = new HashMap<String, String>();
        ITI43_HEADERS.put("Content-Type", "application/xop+xml; charset=UTF-8; type=\"application/soap+xml\"");
        ITI43_HEADERS.put("SOAPAction", "urn:ihe:iti:2007:RetrieveDocumentSet");
        ITI43_HEADERS.put("User-Agent", "Axis2");
        ITI43_HEADERS.put("Content-Transfer-Encoding", "Axis2");

        xmlTemplate = new ClassPathResource("/xmltemplates/iti43.xml");

    }

    @RequestMapping(method = RequestMethod.GET)
    public String root(Model model) {
        Iti43RequestDto request = new Iti43RequestDto();
        request.setHost(defaultHost);
        request.setPort("8021");
        request.setDocumentUniqueId("Example: 2.16.840.1.113883.3.6492.105.18");
        request.setRepositoryUniqueId("2.16.840.1.113883.3.6492.1");
        request.setTemplate(getContentFromFile(xmlTemplate));


        model.addAttribute("itiHeaders", ITI43_HEADERS);
        model.addAttribute("requestDto", request);

        return "send-iti-43";
    }

    @RequestMapping(value = "/applyTemplate", method = RequestMethod.GET)
    public @ResponseBody String applyTemplate(Model model,
                                              @RequestParam String host,
                                              @RequestParam String port,
                                              @RequestParam String repositoryUniqueId,
                                              @RequestParam String documentUniqueId) {
        String templateStr = getContentFromFile(xmlTemplate);

        String messageId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

        Template template = new Template(templateStr)
                .addReplacement("messageId", messageId)
                .addReplacement("host", host)
                .addReplacement("port", port)
                .addReplacement("repositoryUniqueId", repositoryUniqueId)
                .addReplacement("documentUniqueId", documentUniqueId);
        return template.build();
    }


    @RequestMapping(method = RequestMethod.POST)
    public String postRequest(@Valid @ModelAttribute("requestDto") Iti43RequestDto requestDto, Model model) throws Exception {

        model.addAttribute("itiHeaders", ITI43_HEADERS);

        ResponseDto dto = httpSender.sendRequest("https://" + requestDto.getHost() + ":" + requestDto.getPort() + "/axis2/services/xdsrepositoryb", ITI43_HEADERS, requestDto.getTemplate());
        model.addAttribute("responseDto", dto);

        return "send-iti-43";
    }

/*
    @RequestMapping(value="/download", method = RequestMethod.POST)
    public String postDownloadRequest(@Valid @ModelAttribute("requestDto") Iti43RequestDto requestDto, Model model, HttpResponse response) throws Exception {

        model.addAttribute("itiHeaders", ITI43_HEADERS);

        ResponseDto dto = httpSender.sendRequest("https://"+requestDto.getHost()+":"+requestDto.getPort()+"/axis2/services/xdsrepositoryb", ITI43_HEADERS,requestDto.getTemplate());

        OutputStream os = ((HttpServletResponse) response).getOutputStream();

        model.addAttribute("responseDto", dto);

        return "send-iti-43";
    }
*/


}
