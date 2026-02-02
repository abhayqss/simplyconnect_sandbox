package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-18T17:28:43.790+03:00")
public class CalleeInfoDto {

    @JsonProperty("employeeId")
    private Long employeeId = null;

    @JsonProperty("residentId")
    private Long residentId = null;

    @JsonProperty("userId")
    private Long userId = null;


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
     * mobile user id. Nullable
     * minimum: 1
     *
     * @return userId
     */
    @Min(1)
    @ApiModelProperty(example = "1", value = "mobile user id. Nullable")
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}
