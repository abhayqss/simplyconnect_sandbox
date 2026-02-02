package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent encounter DETAILS
 */
@ApiModel(description = "This DTO is intended to represent encounter DETAILS")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T08:01:57.882-03:00")
public class EncounterDto extends EncounterInfoDto {

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("endDate")
    private Long endDateTime = null;

    @JsonProperty("mode")
    private String mode = null;

    @JsonProperty("priority")
    private String priority = null;

    @JsonProperty("problems")
    private List<ListItemDto> problems = new ArrayList<ListItemDto>();

    @JsonProperty("procedures")
    private List<ProcedureListItemDto> procedures = new ArrayList<>();

    @JsonProperty("patientInstructions")
    private String patientInstructions = null;

    @JsonProperty("participants")
    private List<ParticipantListItemDto> participants = new ArrayList<ParticipantListItemDto>();

    @JsonProperty("representedOrganization")
    private RepresentedOrganizationDto representedOrganization = null;

    @JsonProperty("additionalInfo")
    private EncounterAdditionalInfoDto additionalInfo = null;


    /**
    * Status
    *
    * @return status
    */
   
    @ApiModelProperty(example = "Obsolete", value = "Status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
    * End date/Time Stopped
    *
    * @return endDateTime
    */
   
    @ApiModelProperty(example = "1336338000000", value = "End date/Time Stopped")
    public Long getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Long endDateTime) {
        this.endDateTime = endDateTime;
    }

    /**
    * mode
    *
    * @return mode
    */
   
    @ApiModelProperty(example = "Remote presence", value = "mode")
    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
    * priority
    *
    * @return priority
    */
   
    @ApiModelProperty(example = "Callback", value = "priority")
    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
    public EncounterDto addProblemsItem(ListItemDto problemsItem) {
        this.problems.add(problemsItem);
        return this;
    }

    /**
    * Problems associated with an encounter (multiple problems)
    *
    * @return problems
    */
   
    @ApiModelProperty(value = "Problems associated with an encounter (multiple problems)")
    public List<ListItemDto> getProblems() {
        return problems;
    }

    public void setProblems(List<ListItemDto> problems) {
        this.problems = problems;
    }
    public EncounterDto addProceduresItem(ProcedureListItemDto proceduresItem) {
        this.procedures.add(proceduresItem);
        return this;
    }

    /**
    * Procedures associated with an encounter (multiple procedures)
    *
    * @return procedures
    */
   
    @ApiModelProperty(value = "Procedures associated with an encounter (multiple procedures)")
    public List<ProcedureListItemDto> getProcedures() {
        return procedures;
    }

    public void setProcedures(List<ProcedureListItemDto> procedures) {
        this.procedures = procedures;
    }

    /**
    * Patient instructions text (full text) will be displayed
    *
    * @return patientInstructions
    */
   
    @ApiModelProperty(example = "To minimize pain ... Some long text", value = "Patient instructions text (full text) will be displayed")
    public String getPatientInstructions() {
        return patientInstructions;
    }

    public void setPatientInstructions(String patientInstructions) {
        this.patientInstructions = patientInstructions;
    }
    public EncounterDto addParticipantsItem(ParticipantListItemDto participantsItem) {
        this.participants.add(participantsItem);
        return this;
    }

    /**
    * Participants section represents a list of the participants.
    *
    * @return participants
    */
   
    @ApiModelProperty(value = "Participants section represents a list of the participants.")
    public List<ParticipantListItemDto> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantListItemDto> participants) {
        this.participants = participants;
    }

    
   
    @ApiModelProperty(value = "")
    public RepresentedOrganizationDto getRepresentedOrganization() {
        return representedOrganization;
    }

    public void setRepresentedOrganization(RepresentedOrganizationDto representedOrganization) {
        this.representedOrganization = representedOrganization;
    }

    
   
    @ApiModelProperty(value = "")
    public EncounterAdditionalInfoDto getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(EncounterAdditionalInfoDto additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

}
