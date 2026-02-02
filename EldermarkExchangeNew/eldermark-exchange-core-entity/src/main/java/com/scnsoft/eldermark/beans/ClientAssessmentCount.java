package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;

public class ClientAssessmentCount {

    private AssessmentStatus status;
    
    private Long count;
    
    public ClientAssessmentCount(AssessmentStatus status, Long count) {
        this.count = count;
        this.status = status;
    }

    public AssessmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssessmentStatus status) {
        this.status = status;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
