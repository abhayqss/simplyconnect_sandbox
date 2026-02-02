package com.scnsoft.eldermark.web.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-07-07T16:20:18.041+03:00")
public class FamilyHistoryInfoDto {

    @JsonProperty("relationshipToPatient")
    private String relationshipToPatient = null;

    @JsonProperty("gender")
    private String gender = null;

    @JsonProperty("birthDate")
    private Date birthDate = null;

    @JsonProperty("dataSource")
    private DataSourceDto dataSource = null;

    @JsonProperty("observations")
    private List<FamilyHistoryObservationListItemDto> observations = new ArrayList<FamilyHistoryObservationListItemDto>();


    /**
    * Relationship to patient
    *
    * @return relationshipToPatient
    */
   
    @ApiModelProperty(example = "Father", value = "Relationship to patient")
    public String getRelationshipToPatient() {
        return relationshipToPatient;
    }

    public void setRelationshipToPatient(String relationshipToPatient) {
        this.relationshipToPatient = relationshipToPatient;
    }

    
   
    @ApiModelProperty(value = "")
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
    * Birth date
    *
    * @return birthDate
    */
   
    @ApiModelProperty(example = "1326862800000", value = "Birth date")
    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    
   
    @ApiModelProperty(value = "")
    public DataSourceDto getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceDto dataSource) {
        this.dataSource = dataSource;
    }
    public FamilyHistoryInfoDto addObservationsItem(FamilyHistoryObservationListItemDto observationsItem) {
        this.observations.add(observationsItem);
        return this;
    }

    /**
    * Family History Observations
    *
    * @return observations
    */
   
    @ApiModelProperty(value = "Family History Observations")
    public List<FamilyHistoryObservationListItemDto> getObservations() {
        return observations;
    }

    public void setObservations(List<FamilyHistoryObservationListItemDto> observations) {
        this.observations = observations;
    }

}
