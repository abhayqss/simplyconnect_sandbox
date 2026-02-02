package com.scnsoft.eldermark.dto.adt.segment;

import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;

import java.util.List;

public class AdtAllergyDto {
    private String setId;
    private String allergyType;
    private CECodedElementDto allergyCode;
    private String allergySeverity;
    private List<String> allergyReactions;
    private Long identificationDate;

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getAllergyType() {
        return allergyType;
    }

    public void setAllergyType(String allergyType) {
        this.allergyType = allergyType;
    }

    public CECodedElementDto getAllergyCode() {
        return allergyCode;
    }

    public void setAllergyCode(CECodedElementDto allergyCode) {
        this.allergyCode = allergyCode;
    }

    public String getAllergySeverity() {
        return allergySeverity;
    }

    public void setAllergySeverity(String allergySeverity) {
        this.allergySeverity = allergySeverity;
    }

    public List<String> getAllergyReactions() {
        return allergyReactions;
    }

    public void setAllergyReactions(List<String> allergyReactions) {
        this.allergyReactions = allergyReactions;
    }

    public Long getIdentificationDate() {
        return identificationDate;
    }

    public void setIdentificationDate(Long identificationDate) {
        this.identificationDate = identificationDate;
    }
}
