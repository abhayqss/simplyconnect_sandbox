package com.scnsoft.eldermark.web.entity.notes;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * This DTO is intended to represent submitted notes.
 */
@ApiModel(description = "This DTO is intended to represent submitted notes.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-26T14:44:50.580+03:00")
public class NoteCreateDto {

    @JsonProperty("careReceiverId")
    @Deprecated
    /**
     * Architectural misunderstanding: actually userId is passed here from frontend.
     *
     */
    private Long careReceiverId = null;

    @JsonProperty("eventId")
    private Long eventId = null;

    @JsonProperty("lastModifiedDate")
    private Date lastModifiedDate = null;

    @JsonProperty("subjective")
    private String subjective = null;

    @JsonProperty("objective")
    private String objective = null;

    @JsonProperty("assessment")
    private String assessment = null;

    @JsonProperty("plan")
    private String plan = null;

    @JsonProperty("subTypeId")
    private Long subTypeId = null;

    @JsonProperty("admitDateId")
    private Long admitDateId = null;


    /**
     * CareReceiver's Id
     * minimum: 1
     *
     * @return careReceiverId
     */
    @Min(1)
    @ApiModelProperty(example = "42", value = "CareReceiver's Id")
    public Long getCareReceiverId() {
        return careReceiverId;
    }

    public void setCareReceiverId(Long careReceiverId) {
        this.careReceiverId = careReceiverId;
    }

    /**
     * Event Id
     * minimum: 1
     *
     * @return eventId
     */
    @Min(1)
    @ApiModelProperty(example = "13", value = "Event Id")
    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    /**
     * Date of the last modification of the note
     *
     * @return lastModifiedDate
     */
    @NotNull

    @ApiModelProperty(example = "1326862800000", required = true, value = "Date of the last modification of the note")
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    /**
     * Subjective of the note
     *
     * @return subjective
     */

    @ApiModelProperty(example = "This is subjective", value = "Subjective of the note")
    public String getSubjective() {
        return subjective;
    }

    public void setSubjective(String subjective) {
        this.subjective = subjective;
    }

    /**
     * Objective of the note
     *
     * @return objective
     */

    @ApiModelProperty(example = "This is objective", value = "Objective of the note")
    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    /**
     * Assesment of the note
     *
     * @return assessment
     */

    @ApiModelProperty(example = "This is assessment", value = "Assesment of the note")
    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    /**
     * Plan of the note
     *
     * @return plan
     */

    @ApiModelProperty(example = "This is plan", value = "Plan of the note")
    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    /**
     * Note subtype id
     * minimum: 1
     *
     * @return subTypeid
     */
    @Min(1)
    @ApiModelProperty(example = "16", value = "Note subtype id")
    public Long getSubTypeId() {
        return subTypeId;
    }

    public void setSubTypeid(Long subTypeid) {
        this.subTypeId = subTypeid;
    }

    /**
     * Id of admit date record.
     *
     * @return admitDateId
     */

    @ApiModelProperty(example = "42", value = "Id of admit date record.")
    public Long getAdmitDateId() {
        return admitDateId;
    }

    public void setAdmitDateId(Long admitDateId) {
        this.admitDateId = admitDateId;
    }

}
