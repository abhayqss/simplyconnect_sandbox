package com.scnsoft.eldermark.web.entity.notes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.NoteStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;
import java.util.Date;

/**
 * This DTO is intended to represent history entry for the note.
 */
@ApiModel(description = "This DTO is intended to represent history entry for the note.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-09T11:45:06.936+03:00")
public class HistoryNoteItemDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("status")
    private NoteStatus status = null;

    @JsonProperty("lastModifiedDate")
    private Date lastModifiedDate = null;

    @JsonProperty("creator")
    private NoteEmployeeDto creator = null;

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
    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
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

    
   
    @ApiModelProperty(value = "")
    public NoteEmployeeDto getCreator() {
        return creator;
    }

    public void setCreator(NoteEmployeeDto creator) {
        this.creator = creator;
    }

}
