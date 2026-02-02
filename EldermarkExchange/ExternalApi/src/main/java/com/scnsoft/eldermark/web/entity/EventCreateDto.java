package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.shared.carecoordination.EventDetailsDto;
import com.scnsoft.eldermark.shared.carecoordination.ManagerDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * This DTO is intended to represent submitted events. [NOTE] `responsible` represents Details of Registered Nurse (RN); `treatingPhysician` represents Details of Treating Physician
 */
@ApiModel(description = "This DTO is intended to represent submitted events. [NOTE] `responsible` represents Details of Registered Nurse (RN); `treatingPhysician` represents Details of Treating Physician")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class EventCreateDto {

    @JsonProperty("eventDetails")
    private EventDetailsDto eventDetails = null;

    @JsonProperty("author")
    private EventAuthorDto author = null;

    @JsonProperty("manager")
    private ManagerDto manager = null;

    @JsonProperty("responsible")
    private NameWithAddressEditDto responsible = null;

    @JsonProperty("treatingHospital")
    private HospitalEditDto treatingHospital = null;

    @JsonProperty("treatingPhysician")
    private NameWithAddressEditDto treatingPhysician = null;


    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    public EventDetailsDto getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(EventDetailsDto eventDetails) {
        this.eventDetails = eventDetails;
    }

    @Valid
    @NotNull
    @ApiModelProperty(required = true)
    public EventAuthorDto getAuthor() {
        return author;
    }

    public void setAuthor(EventAuthorDto author) {
        this.author = author;
    }

    @Valid
    @ApiModelProperty
    public ManagerDto getManager() {
        return manager;
    }

    public void setManager(ManagerDto manager) {
        this.manager = manager;
    }

    @Valid
    @ApiModelProperty
    public NameWithAddressEditDto getResponsible() {
        return responsible;
    }

    public void setResponsible(NameWithAddressEditDto responsible) {
        this.responsible = responsible;
    }

    @Valid
    @ApiModelProperty
    public HospitalEditDto getTreatingHospital() {
        return treatingHospital;
    }

    public void setTreatingHospital(HospitalEditDto treatingHospital) {
        this.treatingHospital = treatingHospital;
    }

    @Valid
    @ApiModelProperty
    public NameWithAddressEditDto getTreatingPhysician() {
        return treatingPhysician;
    }

    public void setTreatingPhysician(NameWithAddressEditDto treatingPhysician) {
        this.treatingPhysician = treatingPhysician;
    }

}

