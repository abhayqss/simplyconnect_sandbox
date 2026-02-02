package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent encounter DETAILS
 */
@ApiModel(description = "This DTO is intended to represent encounter DETAILS")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T11:44:53.445-03:00")
public class ParticipantDto extends ParticipantListItemDto {

    @JsonProperty("type")
    private String participantType = null;

    @JsonProperty("dateTime")
    private Long dateTime = null;

    @JsonProperty("address")
    private String address = null;

    @JsonProperty("telecom")
    private TelecomsDto telecom = null;


    /**
    * Participant type
    *
    * @return participantType
    */
   
    @ApiModelProperty(example = "Verifier", value = "Participant type")
    public String getParticipantType() {
        return participantType;
    }

    public void setParticipantType(String participantType) {
        this.participantType = participantType;
    }

    /**
    * date
    *
    * @return dateTime
    */
   
    @ApiModelProperty(example = "1336338000000", value = "date")
    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    /**
    * Address
    *
    * @return address
    */
   
    @ApiModelProperty(example = "484 Bluff Street, Rapid City, South Dakota, 57701", value = "Address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    
   
    @ApiModelProperty(value = "")
    public TelecomsDto getTelecom() {
        return telecom;
    }

    public void setTelecom(TelecomsDto telecom) {
        this.telecom = telecom;
    }

}
