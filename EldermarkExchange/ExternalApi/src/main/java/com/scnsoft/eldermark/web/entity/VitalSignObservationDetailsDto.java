package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.entity.phr.VitalSignType;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-02-20T12:37:24.514+03:00")
public class VitalSignObservationDetailsDto {

    @NotNull
    @JsonProperty("dateTime")
    private Long dateTime = null;

    @JsonProperty("value")
    private Double value = null;

    @JsonProperty("unit")
    private String unit = null;

    @JsonProperty("loinc")
    private ConceptDescriptorDto loinc = null;

    @JsonProperty("type")
    private VitalSignType type = null;


    /**
     * Date and time of the observation
     *
     * @return dateTime
     */
    @ApiModelProperty(example = "1435234244000", value = "Date and time of the observation", required = true)
    public Long getDateTime() {
        return dateTime;
    }

    public void setDateTime(Long dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Measurement value. Nullable
     *
     * @return value
     */
    @NotNull
    @ApiModelProperty(example = "26.0", value = "Numeric measurement value", required = true)
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
    @Size(max = 50)
    @ApiModelProperty(example = "/min", value = "Unit of measurement. Nullable")
    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Valid
    public ConceptDescriptorDto getLoinc() {
        return loinc;
    }

    public void setLoinc(ConceptDescriptorDto loinc) {
        this.loinc = loinc;
    }

    @ApiModelProperty(value = "Vital Sign type. See GET /info/vitalSigns for a full list of supported values.")
    public VitalSignType getType() {
        return type;
    }

    public void setType(VitalSignType type) {
        this.type = type;
    }

}
