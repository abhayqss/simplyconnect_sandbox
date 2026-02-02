package com.scnsoft.eldermark.mobile.controller;

import com.scnsoft.eldermark.dto.docutrack.DocutrackSupportedFileListItemDto;
import com.scnsoft.eldermark.mobile.dto.docutrack.BusinessUnitCodeListItemDto;
import com.scnsoft.eldermark.mobile.facade.DocutrackFacade;
import com.scnsoft.eldermark.web.commons.dto.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/docutrack")
@RestController
public class DocutrackController {

    @Autowired
    private DocutrackFacade docutrackFacade;

    @GetMapping(value = "/business-unit-codes", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<BusinessUnitCodeListItemDto>> getBusinessUnitCodes() {
        return Response.successResponse(docutrackFacade.getBusinessUnitCodes());
    }

    @GetMapping(value = "/supported-file-types", produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<List<DocutrackSupportedFileListItemDto>> getSupportedFileTypes() {
        return Response.successResponse(docutrackFacade.getSupportedFileTypes());
    }
}
