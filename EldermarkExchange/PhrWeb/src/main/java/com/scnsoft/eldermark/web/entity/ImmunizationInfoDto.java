package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Generated;
import java.util.HashMap;
import java.util.Map;

/**
 * This DTO is intended to represent immunizations
 */
@ApiModel(description = "This DTO is intended to represent immunizations")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-05-15T15:12:02.581+03:00")
public class ImmunizationInfoDto implements Comparable<ImmunizationInfoDto> {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("immunizationName")
    private String immunizationName = null;

    @JsonProperty("administeredBy")
    private String administeredBy = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("period")
    private PeriodDto period = null;

    @JsonProperty("doseQuantity")
    private Double doseQuantity = null;

    @JsonProperty("doseUnit")
    private String doseUnit = null;

    @JsonProperty("route")
    private String route = null;

    @JsonProperty("site")
    private String site = null;

    @JsonProperty("indications")
    private Map<String, String> indications = new HashMap<String, String>();

    @JsonProperty("instructions")
    private String instructions = null;

    @JsonProperty("repeat")
    private Integer repeat = null;

    @JsonProperty("reaction")
    private String reaction = null;

    @JsonProperty("refusal")
    private Boolean refusal = null;

    @JsonProperty("refusalReason")
    private String refusalReason = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;


    @ApiModelProperty
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Immunization name
     *
     * @return immunizationName
     */
    @ApiModelProperty(example = "Typhoid vaccine, live, oral", value = "Immunization name")
    public String getImmunizationName() {
        return immunizationName;
    }

    public void setImmunizationName(String immunizationName) {
        this.immunizationName = immunizationName;
    }

    /**
     * Immunization administrator
     *
     * @return administeredBy
     */
    @ApiModelProperty(example = "Administered By: DWeber, RN", value = "Immunization administrator")
    public String getAdministeredBy() {
        return administeredBy;
    }

    public void setAdministeredBy(String administeredBy) {
        this.administeredBy = administeredBy;
    }

    /**
     * Immunization status
     *
     * @return status
     */
    @ApiModelProperty(example = "Completed", value = "Immunization status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ApiModelProperty
    public PeriodDto getPeriod() {
        return period;
    }

    public void setPeriod(PeriodDto period) {
        this.period = period;
    }

    @ApiModelProperty(example = "90.0")
    public Double getDoseQuantity() {
        return doseQuantity;
    }

    public void setDoseQuantity(Double doseQuantity) {
        this.doseQuantity = doseQuantity;
    }

    @ApiModelProperty(example = "ml")
    public String getDoseUnit() {
        return doseUnit;
    }

    public void setDoseUnit(String doseUnit) {
        this.doseUnit = doseUnit;
    }

    /**
     * Route of administration (Medication delivery method)
     *
     * @return route
     */
    @ApiModelProperty(value = "Route of administration (Medication delivery method)")
    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @ApiModelProperty
    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public ImmunizationInfoDto putIndicationsItem(String key, String indicationsItem) {
        this.indications.put(key, indicationsItem);
        return this;
    }

    /**
     * A map of key-value pairs representing indications. An indication describes the rationale for the immunization.
     *
     * @return indications
     */
    @ApiModelProperty(value = "A map of key-value pairs representing indications. An indication describes the rationale for the immunization.")
    public Map<String, String> getIndications() {
        return indications;
    }

    public void setIndications(Map<String, String> indications) {
        this.indications = indications;
    }

    /**
     * A list of instructions. The patient instructions are additional information provided to a patient related to this immunization.
     *
     * @return instructions
     */
    @ApiModelProperty(value = "A list of instructions. The patient instructions are additional information provided to a patient related to this immunization.")
    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @ApiModelProperty(example = "3")
    public Integer getRepeat() {
        return repeat;
    }

    public void setRepeat(Integer repeat) {
        this.repeat = repeat;
    }

    /**
     * Reaction to immunization (if any)
     *
     * @return reaction
     */
    @ApiModelProperty(example = "Nausea And Vomiting", value = "Reaction to immunization (if any)")
    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    @ApiModelProperty
    public Boolean getRefusal() {
        return refusal;
    }

    public void setRefusal(Boolean refusal) {
        this.refusal = refusal;
    }

    /**
     * Immunization refusal reason (text)
     *
     * @return refusalReason
     */
    @ApiModelProperty(value = "Immunization refusal reason (text)")
    public String getRefusalReason() {
        return refusalReason;
    }

    public void setRefusalReason(String refusalReason) {
        this.refusalReason = refusalReason;
    }

    @ApiModelProperty
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int compareTo(ImmunizationInfoDto o) {
        int result = ObjectUtils.compare(this.period.getStartDate(), o.period.getStartDate());
        if (result != 0) return result;
        return ObjectUtils.compare(this.immunizationName, o.immunizationName);
    }
}
