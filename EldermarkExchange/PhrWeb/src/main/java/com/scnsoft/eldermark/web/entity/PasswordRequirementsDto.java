package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent password complexity requirements.
 */
@ApiModel(description = "This DTO is intended to represent password complexity requirements.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-11-09T14:29:16.303+03:00")
public class PasswordRequirementsDto {

    @JsonProperty("text")
    private String text = null;

    @JsonProperty("regexp")
    private String regexp = null;


    /**
     * human-readable password strenght requirements description
     *
     * @return text
     */
    @ApiModelProperty(example = "Password does not meet the password requirements (see below) ...",
            value = "human-readable password strenght requirements description")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * regular expression to test password strength
     *
     * @return regexp
     */
    @ApiModelProperty(example = "^(?&#x3D;(.*\\d))(?&#x3D;.*[a-z]{1,})(?&#x3D;.*[A-Z]{1,})[0-9a-zA-Z]{8,}",
            value = "regular expression to test password strength")
    public String getRegexp() {
        return regexp;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

}

