package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This DTO is intended to represent admit date.
 */
@ApiModel(description = "This DTO is intended to represent admit date.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-17T14:26:11.290+03:00")
public class AdmitDateDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("value")
    private Date value = null;

    @JsonProperty("takenNoteTypeCodes")
    private List<String> takenNoteTypeCodes = new ArrayList<String>();

    public AdmitDateDto(Long id, Date value) {
        this.id = id;
        this.value = value;
    }

    public AdmitDateDto() {
    }

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

    /**
    * Admit date of patient.
    *
    * @return value
    */
   
    @ApiModelProperty(example = "1326862800000", value = "Admit date of patient.")
    public Date getValue() {
        return value;
    }

    public void setValue(Date value) {
        this.value = value;
    }
    public AdmitDateDto addTakenNoteTypeCodesItem(String takenNoteTypeCodesItem) {
        this.takenNoteTypeCodes.add(takenNoteTypeCodesItem);
        return this;
    }

    /**
    * Note sub type codes which are already present in the system.
    *
    * @return takenNoteTypeCodes
    */
   
    @ApiModelProperty(value = "Note sub type codes which are already present in the system.")
    public List<String> getTakenNoteTypeCodes() {
        return takenNoteTypeCodes;
    }

    public void setTakenNoteTypeCodes(List<String> takenNoteTypeCodes) {
        this.takenNoteTypeCodes = takenNoteTypeCodes;
    }

}
