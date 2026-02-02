package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;

/**
 * This DTO is intended to represent document metadata
 */
@ApiModel(description = "This DTO is intended to represent document metadata")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-06-05T16:44:06.013+03:00")
public class DocumentInfoDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("title")
    private String title = null;

    @JsonProperty("extension")
    private String extension = null;

    @JsonProperty("mimeType")
    private String mimeType = null;

    @JsonProperty("type")
    private String type = null;

    @JsonProperty("sizeKb")
    private Double sizeKb = null;

    @JsonProperty("hash")
    private String hash = null;

    @JsonProperty("createdOn")
    private DateDto createdOn = null;

    @JsonProperty("dataSource")
    private String dataSource = null;

    @JsonProperty("isCdaViewable")
    private Boolean isCdaViewable = null;

    /**
     * Document ID. It is null for CCD.xml and FACESHEET.pdf
     * minimum: 1
     *
     * @return id
     */
    @ApiModelProperty(value = "Document ID. It is null for CCD.xml and FACESHEET.pdf")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ApiModelProperty(example = "FACESHEET")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @ApiModelProperty(example = "pdf")
    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    @ApiModelProperty(example = "application/pdf")
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @ApiModelProperty
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * File size in kilobytes. It is null for CCD.xml and FACESHEET.pdf
     *
     * @return sizeKb
     */
    @ApiModelProperty(example = "1.82", value = "File size in kilobytes. It is null for CCD.xml and FACESHEET.pdf")
    public Double getSizeKb() {
        return sizeKb;
    }

    public void setSizeKb(Double sizeKb) {
        this.sizeKb = sizeKb;
    }

    /**
     * File hash sum. It is null for CCD.xml and FACESHEET.pdf
     *
     * @return hash
     */
    @ApiModelProperty(example = "91ec9e2b8a356e0232f0087927926a30", value = "File hash sum. It is null for CCD.xml and FACESHEET.pdf")
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @ApiModelProperty
    public DateDto getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(DateDto createdOn) {
        this.createdOn = createdOn;
    }

    /**
     * Data source name
     *
     * @return dataSource
     */
    @ApiModelProperty(example = "EMTest_21250", value = "Data source name")
    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * This flag indicates that this document can be viewed as CDA document.
     *
     * @return isCdaViewable
     */

    @ApiModelProperty(value = "This flag indicates that this document can be viewed as CDA document.")
    public Boolean IsCdaViewable() {
        return isCdaViewable;
    }

    public void setIsCdaViewable(Boolean isCdaViewable) {
        this.isCdaViewable = isCdaViewable;
    }

}
