package com.scnsoft.eldermark.api.shared.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * This DTO is intended to represent event details (Event Essentials).
 * <p>
 * Created by pzhurba on 05-Oct-15.
 */
@ApiModel(description = "This DTO is intended to represent event details (Event Essentials).")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-16T18:53:11.881+03:00")
public class EventDetailsDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:mm a Z")
    private Date eventDatetime;
    private String location;
    private Long eventTypeId;
    private String eventType;
    private boolean injury;
    private String situation;
    private String background;
    private String assessment;
    private Date assessmentCompletedDate;
    private boolean followUpExpected;
    private String followUpDetails;
    private String auxiliaryInfo;

    private boolean emergencyVisit;
    private boolean overnightPatient;
    private String deviceId;

    private Date deathDateTime;
    private Boolean deathIndicator;


    /**
     * Date and time of the event occurrence. It may be any moment in the past or future.
     */
    @NotNull
    @ApiModelProperty(required = true, value = "Date and time of the event occurrence. It may be any moment in the past or future.", example = "1519063927000")
    public Date getEventDatetime() {
        return eventDatetime;
    }

    public void setEventDatetime(Date eventDatetime) {
        this.eventDatetime = eventDatetime;
    }

    /**
     * Event location.
     */
    @ApiModelProperty(example = "House", value = "Event location.")
    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Value of the field shows whether event has resulted in injury.
     */
    @ApiModelProperty(value = "Value of the field shows whether event has resulted in injury.")
    public boolean isInjury() {
        return injury;
    }

    public void setInjury(boolean injury) {
        this.injury = injury;
    }

    /**
     * A clear and concise description of an event.
     */
    @Size(max = 5000)
    @ApiModelProperty(example = "Donald fell today after tripping on a rug", value = "A clear and concise description of an event.")
    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    /**
     * Any historical information about the person(s) involved that could have led to event.
     */
    @Size(max = 5000)
    @ApiModelProperty(value = "Any historical information about the person(s) involved that could have led to event.")
    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    /**
     * Value of the field describes what has happened or is happening.
     */
    @Size(max = 5000)
    @ApiModelProperty(example = "Donald is in bad mood", value = "Value of the field describes what has happened or is happening.")
    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public Date getAssessmentCompletedDate() {
        return assessmentCompletedDate;
    }

    public void setAssessmentCompletedDate(Date assessmentCompletedDate) {
        this.assessmentCompletedDate = assessmentCompletedDate;
    }

    /**
     * Follow Up Expected
     */
    @ApiModelProperty(value = "Follow Up Expected", example = "true")
    public boolean isFollowUpExpected() {
        return followUpExpected;
    }

    public void setFollowUpExpected(boolean followUpExpected) {
        this.followUpExpected = followUpExpected;
    }

    /**
     * Details on event follow up (required if `followUpExpected` is true).
     */
    @Size(max = 5000)
    @ApiModelProperty(value = "Details on event follow up (required if `followUpExpected` is true).", example = "Some details of follow up.")
    public String getFollowUpDetails() {
        return followUpDetails;
    }

    public void setFollowUpDetails(String followUpDetails) {
        this.followUpDetails = followUpDetails;
    }


    /**
     * This field is used for testing purposes. The information will NOT be displayed for the end users.
     */
    @ApiModelProperty(value = "This field is used for testing purposes. The information will NOT be displayed for the end users.")
    public String getAuxiliaryInfo() {
        return auxiliaryInfo;
    }

    public void setAuxiliaryInfo(String auxiliaryInfo) {
        this.auxiliaryInfo = auxiliaryInfo;
    }

    /**
     * Value of the field specifies whether patient has visited emergency room (Emergency Department).
     */
    @NotNull
    @ApiModelProperty(required = true, value = "Value of the field specifies whether patient has visited emergency room (Emergency Department).")
    public boolean isEmergencyVisit() {
        return emergencyVisit;
    }

    public void setEmergencyVisit(boolean emergencyVisit) {
        this.emergencyVisit = emergencyVisit;
    }

    /**
     * Value of the field specifies whether patient has an overnight (in-patient) stay.
     */
    @NotNull
    @ApiModelProperty(required = true, value = "Value of the field specifies whether patient has an overnight (in-patient) stay.")
    public boolean isOvernightPatient() {
        return overnightPatient;
    }

    public void setOvernightPatient(boolean overnightPatient) {
        this.overnightPatient = overnightPatient;
    }

    /**
     * Event type id (`GET /info/eventtypes`)
     */
    @NotNull
    @Min(1)
    @ApiModelProperty(required = true, value = "Event type id (`GET /info/eventtypes`)", example = "8")
    public Long getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * Event type. May be `null` in POST call when creating a new Event.
     */
    @ApiModelProperty(example = "Accident requiring treatment", value = "Event type. May be `null` in POST call when creating a new Event.")
    public String getEventType() {
        return eventType;
    }

    @ApiModelProperty(value = "In case of event, submitted for device id, value of the field specifies device id.")
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @ApiModelProperty(value = "This value is resident death date.")
    public Date getDeathDateTime() {
        return deathDateTime;
    }

    public void setDeathDateTime(Date deathDateTime) {
        this.deathDateTime = deathDateTime;
    }

    @ApiModelProperty(value = "This value indicates that resident deceased.")
    public Boolean getDeathIndicator() {
        return deathIndicator;
    }

    public void setDeathIndicator(Boolean deathIndicator) {
        this.deathIndicator = deathIndicator;
    }
}
