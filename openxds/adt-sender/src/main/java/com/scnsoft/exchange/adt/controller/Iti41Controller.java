package com.scnsoft.exchange.adt.controller;


import com.scnsoft.exchange.adt.HttpSender;
import com.scnsoft.exchange.adt.entity.FileUploadDto;
import com.scnsoft.exchange.adt.entity.Iti41RequestDto;
import com.scnsoft.exchange.adt.entity.ResponseDto;
import com.scnsoft.exchange.adt.entity.Template;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping(value = "/iti41")
@PreAuthorize("isAuthenticated()")
public class Iti41Controller extends ItiController {

    public static Map<String, String> ITI41_HEADER;
    public static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    public static Resource TEMPLATE = new ClassPathResource("/xmltemplates/iti41.xml");

    @Autowired
    HttpSender httpSender;

    @ModelAttribute("itiHeaders")
    public Map<String, String> getIti41Headers() {
        return ITI41_HEADER;
    }

    static {
        ITI41_HEADER = new HashMap<String, String>();
        ITI41_HEADER.put("Content-Type", "application/xop+xml; charset=UTF-8; type=\"application/soap+xml\"");
        /*ITI41_HEADER.put("Content-Type", "multipart/related; boundary=MIMEBoundaryurn_uuid_76A2C3D9BCD3AECFF31217932910180;" +
                "type='application/xop+xml'; start='<0.urn:uuid76A2C3D9BCD3AECFF31217932910181@apache.org>';" +
                "start-info='application/soap+xml'; action='urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b'");*/
        ITI41_HEADER.put("SOAPAction", "urn:ihe:iti:2007:ProvideAndRegisterDocumentSet-b");
        ITI41_HEADER.put("User-Agent", "Axis2");
        ITI41_HEADER.put("Content-Transfer-Encoding", "Axis2");
    }


    @RequestMapping(method = RequestMethod.GET)
    public String root(Model model) {
        Iti41RequestDto request = getDefaultValues();

        model.addAttribute("requestDto", request);
        model.addAttribute("fileDto", new FileUploadDto());
        return "send-iti-41";
    }

    public Iti41RequestDto getDefaultValues() {
        Iti41RequestDto request = new Iti41RequestDto();

        request.setHost(defaultHost);
        request.setPort("8021");

        request.setRepositoryUniqueId("2.16.840.1.113883.3.6492.1");
        request.setCreationTime(df.format(new Date()));
        request.setSubmissionTime(df.format(new Date()));
/*


        request.setHash("02d92c580d4ede6c80a878bdd9f3142d8f757be8");
        request.setSize("9");
        request.setDocumentContentEncoded("U29tZSB0ZXh0");
        request.setFileName("IMG_8929.JPG");

*/

        request.setMimeType("text/plain");
        request.setLanguageCode("en-US");
        request.setSourcePatientId("109^^^&amp;2.16.840.1.113883.3.6492&amp;ISO");
        request.setDescription("");
        request.setDocumentUniqueId("2.16.840.1.113883.3.6492.110." + getRandom());
        request.setPatientId("109^^^&amp;2.16.840.1.113883.3.6492&amp;ISO");

        request.setSubmissionSetName("Submission Set");
        request.setSubmissionSetDescription("Submission of document uploaded to exchange");
        request.setSubmissionSetUniqueId("2.16.840.1.113883.3.6492.2." + getRandom());
        request.setDocumentEntryUuid("urn:uuid:" + UUID.randomUUID().toString());

        request.setSubmissionSetSourceId("2.16.840.1.113883.3.6492.1");

        request.setClassCodeValue("44943-9");
        request.setClassCodeCodingScheme("2.16.840.1.113883.6.1");
        request.setClassCodeValueLocalized("unobtainable");

        request.setFormatCodeValue("1.3.6.1.4.1.19376.1.5.3.1.1.2");
        request.setFormatCodeCodingScheme("Connect-a-thon formatCodes");
        request.setFormatCodeValueLocalized("8020");

        request.setPracticeCodeValue("General Medicine");
        request.setPracticeCodeCodingScheme("Connect-a-thon practiceSettingCodes");
        request.setPracticeCodeValueLocalized("General Medicine");

        request.setHealthcareFacilityCodeValue("Assisted Living");
        request.setHealthcareFacilityCodingScheme("Connect-a-thon healthcareFacilityTypeCodes");
        request.setHealthcareFacilityCodeValueLocalized("Assisted Living");

        request.setConfidentialityCodeValue("1.3.6.1.4.1.21367.2006.7.107");
        request.setConfidentialityCodeCodingScheme("Connect-a-thon confidentialityCodes");
        request.setConfidentialityCodeValueLocalized("Normal Sharing");

        request.setContentTypeCodeValue("Communication");
        request.setContentTypeCodeCodingScheme("Connect-a-thon contentTypeCodes");
        request.setContentTypeCodeValueLocalized("Communication");


        Resource template = TEMPLATE;
        request.setTemplate(getContentFromFile(template));

        return request;
    }


    @RequestMapping(value = "/applyTemplate", method = RequestMethod.POST)
    public @ResponseBody String applyTemplate(Model model,
                                              @ModelAttribute("requestDto") Iti41RequestDto requestDto) {
        String templateSrc = getContentFromFile(TEMPLATE);
        String messageId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();

        requestDto.setMessageId(messageId);

        Template template = new Template(templateSrc)
                .addReplacements(requestDto);
        return template.build();
    }

    /*, consumes = "multipart/form-data", produces = "application/json"*/
    /* FileUploadDto*/
    @RequestMapping(value = "/uploadDoc", method = RequestMethod.POST)
    public @ResponseBody FileUploadDto parseFile(@RequestParam("file") MultipartFile file) throws IOException, NoSuchAlgorithmException {

        FileUploadDto dto = new FileUploadDto();

        dto.setSize(String.valueOf(file.getSize()));
        dto.setFileName(file.getOriginalFilename());

        InputStream is = file.getInputStream();
        byte[] bytes = IOUtils.toByteArray(is);

        dto.setHash(verifyChecksum(bytes));
        dto.setBase64Content(new BASE64Encoder().encode(bytes));

        return dto;
    }


    public static String verifyChecksum(byte[] bytes) throws NoSuchAlgorithmException, IOException {
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");

        int from = 0;
        int len = 1024;

        while (from < bytes.length) {

            if (from + len > bytes.length) len = bytes.length - from;
            sha1.update(bytes, from, len);
            from += len;
        }


        byte[] hashBytes = sha1.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hashBytes.length; i++) {
            sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();

    }


    @RequestMapping(method = RequestMethod.POST)
    public String postRequest(@Valid @ModelAttribute("requestDto") Iti41RequestDto requestDto, Model model) throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        headers.putAll(ITI41_HEADER);
/*
        headers.put("Content-Type", requestDto.getContentType());
        headers.put("Content-Length", String.valueOf(requestDto.getTemplate().length()));
*/
        ResponseDto dto = httpSender.sendRequest("https://" + requestDto.getHost() + ":" + requestDto.getPort() + "/axis2/services/xdsrepositoryb", ITI41_HEADER, requestDto.getTemplate());
        model.addAttribute("responseDto", dto);

        return "send-iti-41";
    }


    private static long getRandom() {
        return new Double(Math.random() * 10000000000d).longValue();
    }

}
