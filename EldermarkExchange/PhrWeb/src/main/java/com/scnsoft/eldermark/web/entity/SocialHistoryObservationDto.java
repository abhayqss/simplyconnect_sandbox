package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent social history observation details
 */
@ApiModel(description = "This DTO is intended to represent social history observation details")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-05T21:47:00.815+03:00")
public class SocialHistoryObservationDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("type")
    private String type = null;

    @JsonProperty("text")
    private String text = null;

    @JsonProperty("value")
    private String value = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;




    @ApiModelProperty(value = "")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * social history type
     *
     * @return type
     */

    @ApiModelProperty(example = "Exercise", value = "social history type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * original text
     *
     * @return text
     */

    @ApiModelProperty(example = "Exercise frequency - free text", value = "original text")
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * value
     *
     * @return value
     */

    @ApiModelProperty(example = "10-20", value = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * social history status
     *
     * @return status
     */

    @ApiModelProperty(example = "Active", value = "social history status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }



    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}
