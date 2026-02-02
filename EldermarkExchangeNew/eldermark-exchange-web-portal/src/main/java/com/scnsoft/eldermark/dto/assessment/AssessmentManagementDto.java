package com.scnsoft.eldermark.dto.assessment;

import java.util.List;

public class AssessmentManagementDto {
    String message;
    List<AssessmentScoringGroupDto> scale;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<AssessmentScoringGroupDto> getScale() {
        return scale;
    }

    public void setScale(List<AssessmentScoringGroupDto> scale) {
        this.scale = scale;
    }
}
