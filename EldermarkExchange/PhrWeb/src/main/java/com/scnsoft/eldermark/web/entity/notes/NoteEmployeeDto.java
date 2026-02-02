package com.scnsoft.eldermark.web.entity.notes;

import com.scnsoft.eldermark.shared.carecoordination.EmployeeDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;


import javax.validation.constraints.Min;


public class NoteEmployeeDto extends EmployeeDto {

    @JsonProperty("id")
    private Long id = null;

    /**
     * Note id
     * minimum: 1
     *
     * @return id
     */
    @Min(1)
    @ApiModelProperty(example = "13", value = "Creator id.")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
