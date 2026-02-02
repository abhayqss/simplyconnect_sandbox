package com.scnsoft.eldermark.api.shared.dto.adt;


import com.scnsoft.eldermark.api.shared.dto.adt.datatype.CECodedElementDto;

import java.util.List;

public class AL1AllergySegmentDto implements SegmentDto {
    private String setId;
    private String allergyType;
    private CECodedElementDto allergyCode;
    private String allergySeverity;
    private List<String> allergyReactionList;

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

    public List<String> getAllergyReactionList() {
        return allergyReactionList;
    }

    public void setAllergyReactionList(List<String> allergyReactionList) {
        this.allergyReactionList = allergyReactionList;
    }
}
