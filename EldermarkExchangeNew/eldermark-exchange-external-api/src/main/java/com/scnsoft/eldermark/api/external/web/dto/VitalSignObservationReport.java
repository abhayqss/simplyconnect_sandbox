package com.scnsoft.eldermark.api.external.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.scnsoft.eldermark.api.shared.entity.VitalSignType;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.List;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-01-29T14:21:48.776+03:00")
public class VitalSignObservationReport {

    @JsonProperty("results")
    private List<VitalSignObservationDto> results = new ArrayList<VitalSignObservationDto>();

    @JsonProperty("unit")
    private String unit = null;

    @JsonProperty("vitalSignType")
    private VitalSignType vitalSignType = null;

    @JsonProperty("vitalSignTypeDisplayName")
    private String vitalSignTypeDisplayName = null;

    public VitalSignObservationReport addResultsItem(VitalSignObservationDto resultsItem) {
        this.results.add(resultsItem);
        return this;
    }


    @ApiModelProperty(value = "")
    public List<VitalSignObservationDto> getResults() {
        return results;
    }

    public void setResults(List<VitalSignObservationDto> results) {
        this.results = results;
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
    public VitalSignType getVitalSignType() {
        return vitalSignType;
    }

    public void setVitalSignType(VitalSignType vitalSignType) {
        this.vitalSignType = vitalSignType;
    }

    /**
     * Human readable description of Vital Sign type
     *
     * @return vitalSignTypeDisplayName
     */

    @ApiModelProperty(value = "Human readable description of Vital Sign type")
    public String getVitalSignTypeDisplayName() {
        return vitalSignTypeDisplayName;
    }

    public void setVitalSignTypeDisplayName(String vitalSignTypeDisplayName) {
        this.vitalSignTypeDisplayName = vitalSignTypeDisplayName;
    }

}

