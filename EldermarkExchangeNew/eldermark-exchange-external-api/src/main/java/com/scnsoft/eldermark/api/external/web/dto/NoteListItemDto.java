package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.basic.AuditableEntityStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import java.util.Date;

/**
 * This dto is is intended to represent a note entry in notes list
 */
@ApiModel(description = "This dto is is intended to represent a note entry in notes list")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-09T11:45:06.936+03:00")
public class NoteListItemDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("text")
    private String text = null;

    @JsonProperty("status")
    private AuditableEntityStatus status = null;

    @JsonProperty("lastModifiedDate")
    private Date lastModifiedDate = null;


    /**
    * Note id
    * minimum: 1
    *
    * @return id
    */
    @Min(1) 
    @ApiModelProperty(example = "40", value = "Note id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
    * This is a text of the note
    *
    * @return text
    */
   
    @ApiModelProperty(example = "Note text will be displayed here, the text will be cut by “…” symbols", value = "This is a text of the note")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    
   
    @ApiModelProperty(value = "")
    public AuditableEntityStatus getStatus() {
        return status;
    }

    public void setStatus(AuditableEntityStatus status) {
        this.status = status;
    }

    /**
    * Date of the last modification of the note
    *
    * @return lastModifiedDate
    */
   
    @ApiModelProperty(example = "1326862800000", value = "Date of the last modification of the note")
    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

}
