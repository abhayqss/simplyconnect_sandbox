package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T16:56:24.739+03:00")
public class VitalSignObservationDto {

    @JsonProperty("dateTime")
    private Long dateTime = null;

    @JsonProperty("dateTimeStr")
    private String dateTimeStr = null;

    @JsonProperty("value")
    private Double value = null;

    @JsonProperty("unit")
    private String unit = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;


    @ApiModelProperty(example = "1435234244000", value = "")
    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    @ApiModelProperty(value = "")
    public String getDateTimeStr() {
        return dateTimeStr;
    }

    public void setDateTimeStr(String dateTimeStr) {
        this.dateTimeStr = dateTimeStr;
    }

    /**
     * Measurement value. Nullable
     *
     * @return value
     */
    @ApiModelProperty(example = "82.0", value = "Measurement value. Nullable")
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * Unit of measurement. Nullable
     *
     * @return unit
     */
    @ApiModelProperty(example = "mm Hg", value = "Unit of measurement. Nullable")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

}
