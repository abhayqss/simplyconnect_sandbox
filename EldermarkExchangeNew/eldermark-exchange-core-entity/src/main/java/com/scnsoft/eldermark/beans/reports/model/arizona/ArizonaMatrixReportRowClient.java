package com.scnsoft.eldermark.beans.reports.model.arizona;

import java.time.LocalDate;
import java.util.List;

public class ArizonaMatrixReportRowClient {

    private Long clientId;
    private String clientName;
    private LocalDate dateOfBirth;

    private List<ArizonaMatrixReportRowAssessment> assessments;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<ArizonaMatrixReportRowAssessment> getAssessments() {
        return assessments;
    }

    public void setAssessments(List<ArizonaMatrixReportRowAssessment> assessments) {
        this.assessments = assessments;
    }
}
