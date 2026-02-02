package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-18T15:27:43.413+03:00")
public class NucleusInfoDto {

    @JsonProperty("employeeId")
    private Long employeeId = null;

    @JsonProperty("residentId")
    private Long residentId = null;

    @JsonProperty("nucleusUserId")
    private String nucleusUserId = null;


    /**
     * employee id. Nullable
     * minimum: 1
     *
     * @return employeeId
     */
    @Min(1)
    @ApiModelProperty(value = "employee id. Nullable")
    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * resident id. Nullable
     * minimum: 1
     *
     * @return residentId
     */
    @Min(1)
    @ApiModelProperty(example = "1", value = "resident id. Nullable")
    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    /**
     * nucleus user id (36-char UUID)
     *
     * @return nucleusUserId
     */
    @NotNull
    @ApiModelProperty(example = "dfa1953f-e401-442f-bbcf-bde33b3ca018", required = true, value = "nucleus user id (36-char UUID)")
    public String getNucleusUserId() {
        return nucleusUserId;
    }

    public void setNucleusUserId(String nucleusUserId) {
        this.nucleusUserId = nucleusUserId;
    }

}
