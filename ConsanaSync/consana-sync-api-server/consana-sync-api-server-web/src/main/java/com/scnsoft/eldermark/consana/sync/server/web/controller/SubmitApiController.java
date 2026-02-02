package com.scnsoft.eldermark.consana.sync.server.web.controller;

import com.scnsoft.eldermark.consana.sync.server.facade.ConsanaPatientUpdateFacade;
import com.scnsoft.eldermark.consana.sync.server.model.ConsanaSyncDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Generated;

@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-11-16T12:51:54.549+03:00")
@Api(value = "Submit controller")
@Validated
@RestController
@RequestMapping("")
public class SubmitApiController {

    private final ConsanaPatientUpdateFacade consanaPatientUpdateFacade;

    @Autowired
    public SubmitApiController(ConsanaPatientUpdateFacade consanaPatientUpdateFacade) {
        this.consanaPatientUpdateFacade = consanaPatientUpdateFacade;
    }

    @ApiOperation(value = "Submit patient to be syncronized.", notes = "Submit patient to be syncronized.")
    @PostMapping(value = "/submit", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> sync(
        @ApiParam(value = "Data which contains patient's information necessary for syncronization." , required = true ) @RequestBody ConsanaSyncDto consanaSyncDto
    ) {
        consanaPatientUpdateFacade.convertAndSendToQueue(consanaSyncDto);
        return ResponseEntity.accepted().build();
    }

}
