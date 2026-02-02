package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.*;
import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class MedicationInfoDto {

    @JsonProperty("directions")
    private String directions = null;

    @JsonProperty("indications")
    private List<String> indications = new ArrayList<String>();

    @JsonProperty("medicationName")
    private String medicationName = null;

    @JsonProperty("startedDate")
    private Long startedDate = null;

    @JsonProperty("stoppedDate")
    private Long stoppedDate = null;


    
   
    @ApiModelProperty(example = "Give 30 ml by mouth daily as needed for Constipation Also has routine order")
    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }
    public MedicationInfoDto addIndicationsItem(String indicationsItem) {
        this.indications.add(indicationsItem);
        return this;
    }

    
   
    @ApiModelProperty(value = "")
    public List<String> getIndications() {
        return indications;
    }

    public void setIndications(List<String> indications) {
        this.indications = indications;
    }

    
   
    @ApiModelProperty(example = "Milk of Magnesia 7.75 % SUSP")
    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    
   
    @ApiModelProperty(example = "1326862800000")
    public Long getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Long startedDate) {
        this.startedDate = startedDate;
    }

    
   
    @ApiModelProperty(value = "")
    public Long getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(Long stoppedDate) {
        this.stoppedDate = stoppedDate;
    }

}

