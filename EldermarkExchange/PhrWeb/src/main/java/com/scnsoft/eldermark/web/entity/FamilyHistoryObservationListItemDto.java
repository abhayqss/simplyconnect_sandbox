package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.Date;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-07T16:20:18.041+03:00")
public class FamilyHistoryObservationListItemDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;

    @JsonProperty("date")
    private Date date = null;


    
   
    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
    * Observation name
    *
    * @return name
    */
   
    @ApiModelProperty(example = "Diabet", value = "Observation name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
    * Observation date
    *
    * @return date
    */
   
    @ApiModelProperty(example = "1326862800000", value = "Observation date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
