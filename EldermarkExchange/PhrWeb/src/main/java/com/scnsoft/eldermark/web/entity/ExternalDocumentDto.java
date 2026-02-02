package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-12T04:58:52.497-03:00")
public class ExternalDocumentDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("text")
    private String text = null;

    @JsonProperty("mediaType")
    private String mediaType = null;

    @JsonProperty("url")
    private String url = null;


    
   
    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    
   
    @ApiModelProperty(value = "")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    
   
    @ApiModelProperty(value = "")
    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    
   
    @ApiModelProperty(value = "")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
