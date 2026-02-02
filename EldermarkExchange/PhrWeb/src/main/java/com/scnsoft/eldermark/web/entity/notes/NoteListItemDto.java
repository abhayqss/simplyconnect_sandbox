package com.scnsoft.eldermark.web.entity.notes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.NoteStatus;
import com.scnsoft.eldermark.entity.NoteType;
import com.scnsoft.eldermark.web.entity.DataSourceDto;
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

    @JsonProperty("residentName")
    private String residentName = null;

    @JsonProperty("status")
    private NoteStatus status = null;

    @JsonProperty("type")
    private NoteType type = null;

    @JsonProperty("subType")
    private NoteSubTypeDto subType = null;

    @JsonProperty("lastModifiedDate")
    private Date lastModifiedDate = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;

    @JsonProperty("unread")
    private Boolean unread = null;


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

    /**
     * Patient's full name
     *
     * @return residentName
     */

    @ApiModelProperty(example = "Charles Xavier", value = "Patient's full name")
    public String getResidentName() {
        return residentName;
    }

    public void setResidentName(String residentName) {
        this.residentName = residentName;
    }


    @ApiModelProperty(value = "")
    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }


    @ApiModelProperty(value = "")
    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }


    @ApiModelProperty(value = "")
    public NoteSubTypeDto getSubType() {
        return subType;
    }

    public void setSubType(NoteSubTypeDto subType) {
        this.subType = subType;
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
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * item status. true if current user hasn't seen this item yet.
     *
     * @return unread
     */
    @ApiModelProperty(value = "list item status. true if current user hasn't seen this item yet.")
    public Boolean getUnread() {
        return unread;
    }

    public void setUnread(Boolean unread) {
        this.unread = unread;
    }
}
