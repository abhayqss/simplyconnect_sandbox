package com.scnsoft.eldermark.openxds.api.controller;

import com.scnsoft.eldermark.openxds.api.dto.XdsDocumentDto;
import com.scnsoft.eldermark.openxds.api.dto.XdsDocumentRegistrySyncResult;
import com.scnsoft.eldermark.openxds.api.facade.XdsDocumentFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

@RestController
@RequestMapping("/documents/xds")
public class XdsDocumentController {

    @Autowired
    private XdsDocumentFacade xdsDocumentFacade;

    @GetMapping("/synchronize_all")
    public XdsDocumentRegistrySyncResult syncAll(@RequestParam(value =  "fromTime", required = false) Long fromTime) throws IOException {
        //todo user friendly param fromTime
        var result = xdsDocumentFacade.synchronizeAllDocumentsWithXdsRegistry(Optional.ofNullable(fromTime).map(Instant::ofEpochMilli).orElse(Instant.now()));
        return result;
    }

    @PostMapping(value = "/upload")
    @ResponseBody
    public Long uploadDocument (@RequestBody XdsDocumentDto uploadDocument) {
        return xdsDocumentFacade.uploadDocument(uploadDocument);
    }

    @GetMapping(value = "/download", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public XdsDocumentDto getDocument(@RequestParam(value = "documentUniqueId") String documentUniqueId) {
        return xdsDocumentFacade.getDocument(documentUniqueId);
    }

    @DeleteMapping(value = "/delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void deleteDocument (@RequestParam(value = "documentUniqueId") String documentUniqueId) {
        xdsDocumentFacade.deleteDocument(documentUniqueId);
    }

}
