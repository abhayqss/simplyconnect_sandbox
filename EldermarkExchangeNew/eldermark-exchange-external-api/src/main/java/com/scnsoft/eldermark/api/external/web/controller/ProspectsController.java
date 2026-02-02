package com.scnsoft.eldermark.api.external.web.controller;

import com.scnsoft.eldermark.api.external.facade.ProspectFacade;
import com.scnsoft.eldermark.api.shared.dto.prospect.ProspectDto;
import com.scnsoft.eldermark.api.shared.web.dto.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Generated;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2022-12-08T13:58:54.417+02:00")
@Api(value = "prospects")
@RestController
@RequestMapping("/prospects")
public class ProspectsController {

    @Autowired
    private ProspectFacade prospectFacade;

    @ApiOperation(value = "Create/Update Prospect", notes = "Create/Update Prospect")
    @RequestMapping(method = {RequestMethod.PUT, RequestMethod.POST}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Response<Long> saveProspect(
            @ApiParam(value = "prospectDto" ,required=true ) @RequestBody ProspectDto prospectDto
    ) {
        return Response.successResponse(prospectFacade.save(prospectDto));
    }

}