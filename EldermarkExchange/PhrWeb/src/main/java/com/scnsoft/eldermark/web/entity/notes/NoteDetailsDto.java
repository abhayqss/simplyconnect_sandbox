package com.scnsoft.eldermark.web.entity.notes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.NoteStatus;
import com.scnsoft.eldermark.entity.NoteType;
import com.scnsoft.eldermark.web.entity.AdmitDateDto;
import com.scnsoft.eldermark.web.entity.DataSourceDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This DTO is intended to represent note details.
 */
@ApiModel(description = "This DTO is intended to represent note details.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-09T11:45:06.936+03:00")
public class NoteDetailsDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("residentId")
    private Long residentId = null;

    @JsonProperty("residentName")
    private String residentName = null;

    @JsonProperty("type")
    private NoteType type = null;

    @JsonProperty("subType")
    private NoteSubTypeDto subType = null;

    @JsonProperty("eventId")
    private Long eventId = null;

    @JsonProperty("eventType")
    private String eventType = null;

    @JsonProperty("eventDate")
    private Date eventDate = null;

    @JsonProperty("eventResidentName")
    private String eventResidentName = null;

    @JsonProperty("status")
    private NoteStatus status = null;

    @JsonProperty("lastModifiedDate")
    private Date lastModifiedDate = null;

    @JsonProperty("creator")
    private NoteEmployeeDto creator = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;

    @JsonProperty("subjective")
    private String subjective = null;

    @JsonProperty("objective")
    private String objective = null;

    @JsonProperty("assessment")
    private String assessment = null;

    @JsonProperty("plan")
    private String plan = null;

    @JsonProperty("isArchived")
    private Boolean isArchived = null;

    @JsonProperty("isEditable")
    private Boolean isEditable = null;

    @JsonProperty("changeHistory")
    private List<HistoryNoteItemDto> changeHistory = new ArrayList<HistoryNoteItemDto>();

    @JsonProperty("admitDate")
    private AdmitDateDto admitDate = null;


    /**
     * Note id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(example = "13", value = "Note id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Resident id
     * minimum: 1
     *
     * @return residentId
     */
    @Min(1)
    @ApiModelProperty(example = "42", value = "Resident id")
    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    /**
     * Patient's full name
     *
     * @return residentName
     */

    @ApiModelProperty(example = "Charles Xavier", value = "Patient's full name")
    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }



    @ApiModelProperty(value = "")
    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }



    @ApiModelProperty(value = "")
    public NoteSubTypeDto getSubType() {
        return subType;
    }

    public void setSubType(NoteSubTypeDto subType) {
        this.subType = subType;
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
     * Event type description
     *
     * @return eventType
     */
    @Size(max = 255)
    @ApiModelProperty(example = "Accident requiring treatment", value = "Event type description")
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Submit date of the event.
     *
     * @return eventDate
     */

    @ApiModelProperty(example = "1326862800000", value = "Submit date of the event.")
    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    /**
     * Patient's full name from event
     *
     * @return eventResidentName
     */

    @ApiModelProperty(example = "Charles Xavier", value = "Patient's full name from event")
    public String getEventResidentName() {
        return eventResidentName;
    }

    public void setEventResidentName(String eventResidentName) {
        this.eventResidentName = eventResidentName;
    }

    @ApiModelProperty(value = "")
    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    /**
     * Date of the last modification of the note
     *
     * @return lastModifiedDate
     */

    @ApiModelProperty(example = "1326862800000", value = "Date of the last modification of the note")
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }


    @ApiModelProperty(value = "")
    public NoteEmployeeDto getCreator() {
        return creator;
    }

    public void setCreator(NoteEmployeeDto creator) {
        this.creator = creator;
    }


    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
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
     * Assessment of the note
     *
     * @return assessment
     */

    @ApiModelProperty(example = "This is assessment", value = "Assessment of the note")
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
     * Indicates whether the note is archived, i. e. it is in the history now.
     *
     * @return isArchived
     */

    @ApiModelProperty(example = "false", value = "Indicates whether the note is archived, i. e. it is in the history now.")
    public Boolean IsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    /**
     * Indicates whether this note is editable by current user.
     *
     * @return isEditable
     */

    @ApiModelProperty(example = "true", value = "Indicates whether this note is editable by current user.")
    public Boolean IsEditable() {
        return isEditable;
    }

    public void setIsEditable(Boolean isEditable) {
        this.isEditable = isEditable;
    }

    public NoteDetailsDto addChangeHistoryItem(HistoryNoteItemDto changeHistoryItem) {
        this.changeHistory.add(changeHistoryItem);
        return this;
    }

    /**
     * Change history of the note.
     *
     * @return changeHistory
     */

    @ApiModelProperty(value = "Change history of the note.")
    public List<HistoryNoteItemDto> getChangeHistory() {
        return changeHistory;
    }

    public void setChangeHistory(List<HistoryNoteItemDto> changeHistory) {
        this.changeHistory = changeHistory;
    }

    @ApiModelProperty(value = "")
    public AdmitDateDto getAdmitDate() {
        return admitDate;
    }

    public void setAdmitDate(AdmitDateDto admitDate) {
        this.admitDate = admitDate;
    }

}
