package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-06T08:01:57.882-03:00")
public class ListItemDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("name")
    private String name = null;


    
     @NotNull
 @Min(1) 
    @ApiModelProperty(required = true, value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
   
    @ApiModelProperty(example = "Bipolar disorder", value = "")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
   
}
