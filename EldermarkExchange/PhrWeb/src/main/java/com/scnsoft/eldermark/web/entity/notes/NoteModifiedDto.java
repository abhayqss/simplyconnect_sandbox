package com.scnsoft.eldermark.web.entity.notes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.NoteStatus;
import com.scnsoft.eldermark.entity.NoteType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent result of note modification/creation.
 */
@ApiModel(description = "This DTO is intended to represent result of note modification/creation.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-12T16:00:55.138+03:00")
public class NoteModifiedDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("type")
    private NoteType type = null;

    @JsonProperty("status")
    private NoteStatus status = null;

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

    
   
    @ApiModelProperty(value = "")
    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }


    @ApiModelProperty(value = "")
    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }
}
