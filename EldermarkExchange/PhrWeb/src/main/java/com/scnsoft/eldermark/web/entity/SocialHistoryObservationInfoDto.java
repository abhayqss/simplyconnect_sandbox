package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent social history - observation in list
 */
@ApiModel(description = "This DTO is intended to represent social history - observation in list")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T21:47:00.815+03:00")
public class SocialHistoryObservationInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("type")
    private String type = null;

    @JsonProperty("status")
    private String status = null;




    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * social history type
     *
     * @return type
     */

    @ApiModelProperty(example = "Alcohol intake", value = "social history type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * social history status
     *
     * @return status
     */

    @ApiModelProperty(example = "Active", value = "social history status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
