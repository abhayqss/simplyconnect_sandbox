package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * When submitting a new Vital Sign Observation, specify a valid LOINC code in this section or leave it blank and use `type` attribute.
 */
@ApiModel(description = "When submitting a new Vital Sign Observation, specify a valid LOINC code in this section or leave it blank and use `type` attribute.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T12:37:24.514+03:00")
public class ConceptDescriptorDto {

    @JsonProperty("code")
    private String code = null;

    @JsonProperty("displayName")
    private String displayName = null;


    /**
     * LOINC code (format is nnnnn-n or nnnn-n) - a "magic value" which tells you what is being measured. LOINC was chosen for the \"magic values\" because this aligns with the most countries, but it can be treated as simply a fixed core set of common codes to communicate basic vital signs.
     *
     * @return code
     */
    @NotNull
    @Size(max = 70)
    @Pattern(regexp = "^\\d{4,5}-\\d$")
    @ApiModelProperty(example = "9279-1", value = "LOINC code (format is nnnnn-n or nnnn-n) - a \"magic value\" which tells you what is being measured. LOINC was chosen for the \"magic values\" because this aligns with the most countries, but it can be treated as simply a fixed core set of common codes to communicate basic vital signs.", required = true, allowEmptyValue = false)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @ApiModelProperty(example = "Respiration Rate")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

}
