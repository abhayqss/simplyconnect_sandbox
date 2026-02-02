package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent submitted marketplace appointment
 */
@ApiModel(description = "This DTO is intended to represent submitted marketplace appointment")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-09T17:35:15.537+03:00")
public class AppointmentCreateDto {

    @JsonProperty("emergencyVisit")
    private Boolean emergencyVisit = null;

    @JsonProperty("servicesNames")
    private List<String> servicesNames = new ArrayList<String>();

    @JsonProperty("appointmentDate")
    private Long appointmentDate = null;

    @JsonProperty("requestDate")
    private Long requestDate = null;

    @JsonProperty("phone")
    private String phone = null;

    @JsonProperty("comment")
    private String comment = null;


    @ApiModelProperty(value = "")
    public Boolean getEmergencyVisit() {
        return emergencyVisit;
    }

    public void setEmergencyVisit(Boolean emergencyVisit) {
        this.emergencyVisit = emergencyVisit;
    }
    public AppointmentCreateDto addServicesNamesItem(String servicesNamesItem) {
        this.servicesNames.add(servicesNamesItem);
        return this;
    }

    @ApiModelProperty(value = "")
    public List<String> getServicesNames() {
        return servicesNames;
    }

    public void setServicesNames(List<String> servicesNames) {
        this.servicesNames = servicesNames;
    }

    @ApiModelProperty(example = "1515660749899", required = true, value = "")
    @NotNull
    public Long getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Long appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    @ApiModelProperty(example = "1515660749899", value = "")
    public Long getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(Long requestDate) {
        this.requestDate = requestDate;
    }

    @ApiModelProperty(required = true, value = "")
    @NotNull
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @ApiModelProperty(value = "")
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}
