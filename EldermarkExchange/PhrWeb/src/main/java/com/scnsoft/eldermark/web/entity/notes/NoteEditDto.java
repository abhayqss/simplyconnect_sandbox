package com.scnsoft.eldermark.web.entity.notes;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;
import javax.annotation.Generated;

/**
 * This DTO is intended to represent edited notes.
 */
@ApiModel(description = "This DTO is intended to represent edited notes.")
@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-04-09T11:45:06.936+03:00")
public class NoteEditDto {

    @JsonProperty("id")
    private Long id = null;

    @JsonProperty("subjective")
    private String subjective = null;

    @JsonProperty("objective")
    private String objective = null;

    @JsonProperty("assessment")
    private String assessment = null;

    @JsonProperty("plan")
    private String plan = null;


    /**
    * Note id
    * minimum: 1
    *
    * @return id
    */
    @NotNull
    @Min(1)
    @ApiModelProperty(example = "13", required = true, value = "Note id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
    * Subjective of the note
    *
    * @return subjective
    */
    @ApiModelProperty(example = "This is subjective", value = "Subjective of the note")
    public String getSubjective() {
        return subjective;
    }

    public void setSubjective(String subjective) {
        this.subjective = subjective;
    }

    /**
    * Objective of the note
    *
    * @return objective
    */

    @ApiModelProperty(example = "This is objective", value = "Objective of the note")
    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    /**
    * Assessment of the note
    *
    * @return assessment
    */

    @ApiModelProperty(example = "This is assessment", value = "Assessment of the note")
    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    /*** Plan of the note
    *
    * @return plan
    */

    @ApiModelProperty(example = "This is plan", value = "Plan of the note")
    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

}
