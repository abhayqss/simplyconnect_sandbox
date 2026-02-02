package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.shared.carecoordination.EventDetailsDto;
import com.scnsoft.eldermark.shared.carecoordination.ManagerDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

/**
 * This DTO is intended to represent submitted events. [NOTE] `responsible` represents Details of Registered Nurse (RN); `treatingPhysician` represents Details of Treating Physician
 */
@ApiModel(description = "This DTO is intended to represent submitted events. [NOTE] `responsible` represents Details of Registered Nurse (RN); `treatingPhysician` represents Details of Treating Physician")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-09T12:09:43.945+03:00")
public class EventCreateDto {

    @Valid
    @JsonProperty("eventDetails")
    private EventDetailsDto eventDetails = null;

    @JsonProperty("includeHospital")
    private Boolean includeHospital = null;

    @JsonProperty("includeManager")
    private Boolean includeManager = null;

    @JsonProperty("includeResponsible")
    private Boolean includeResponsible = null;

    @JsonProperty("includeTreatingPhysician")
    private Boolean includeTreatingPhysician = null;

    @Valid
    @JsonProperty("manager")
    private ManagerDto manager = null;

    @Valid
    @JsonProperty("responsible")
    private NameWithAddressEditDto responsible = null;

    @Valid
    @JsonProperty("treatingHospital")
    private HospitalEditDto treatingHospital = null;

    @Valid
    @JsonProperty("treatingPhysician")
    private NameWithAddressEditDto treatingPhysician = null;

    @ApiModelProperty
    @NotNull
    public EventDetailsDto getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(EventDetailsDto eventDetails) {
        this.eventDetails = eventDetails;
    }

    @ApiModelProperty(required = true)
    @NotNull
    public Boolean getIncludeHospital() {
        return includeHospital;
    }

    public void setIncludeHospital(Boolean includeHospital) {
        this.includeHospital = includeHospital;
    }

    @ApiModelProperty(required = true)
    @NotNull
    public Boolean getIncludeManager() {
        return includeManager;
    }

    public void setIncludeManager(Boolean includeManager) {
        this.includeManager = includeManager;
    }

    @ApiModelProperty(required = true)
    @NotNull
    public Boolean getIncludeResponsible() {
        return includeResponsible;
    }

    public void setIncludeResponsible(Boolean includeResponsible) {
        this.includeResponsible = includeResponsible;
    }

    @ApiModelProperty(required = true)
    @NotNull
    public Boolean getIncludeTreatingPhysician() {
        return includeTreatingPhysician;
    }

    public void setIncludeTreatingPhysician(Boolean includeTreatingPhysician) {
        this.includeTreatingPhysician = includeTreatingPhysician;
    }

    @ApiModelProperty
    public ManagerDto getManager() {
        return manager;
    }

    public void setManager(ManagerDto manager) {
        this.manager = manager;
    }

    @ApiModelProperty
    public NameWithAddressEditDto getResponsible() {
        return responsible;
    }

    public void setResponsible(NameWithAddressEditDto responsible) {
        this.responsible = responsible;
    }

    @ApiModelProperty
    public HospitalEditDto getTreatingHospital() {
        return treatingHospital;
    }

    public void setTreatingHospital(HospitalEditDto treatingHospital) {
        this.treatingHospital = treatingHospital;
    }

    @ApiModelProperty
    public NameWithAddressEditDto getTreatingPhysician() {
        return treatingPhysician;
    }

    public void setTreatingPhysician(NameWithAddressEditDto treatingPhysician) {
        this.treatingPhysician = treatingPhysician;
    }

    // custom validation

    @AssertTrue
    private boolean isTreatingHospitalIncludedWhenIncludeHospitalIsTrue() {
        return !Boolean.TRUE.equals(includeHospital) || treatingHospital != null;
    }

    @AssertTrue
    private boolean isTreatingPhysicianIncludedWhenIncludeTreatingPhysicianIsTrue() {
        return !Boolean.TRUE.equals(includeTreatingPhysician) || treatingPhysician != null;
    }

    @AssertTrue
    private boolean isManagerIncludedWhenIncludeManagerIsTrue() {
        return !Boolean.TRUE.equals(includeManager) || manager != null;
    }

    @AssertTrue
    private boolean isResponsibleIncludedWhenIncludeResponsibleIsTrue() {
        return !Boolean.TRUE.equals(includeResponsible) || responsible != null;
    }

}

