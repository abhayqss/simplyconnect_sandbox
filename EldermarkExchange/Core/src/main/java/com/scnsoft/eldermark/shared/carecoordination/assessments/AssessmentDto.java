package com.scnsoft.eldermark.shared.carecoordination.assessments;

public class AssessmentDto {
    private Long assessmentId;
    private Long patientId;
    private String jsonContent;
    private String name;
    private Boolean isScoringEnabled;
    private String shortName;
    private Boolean hasNumeration;
//    todo
    private Boolean type;

    public Long getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(Long assessmentId) {
        this.assessmentId = assessmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public String getJsonContent() {
        return jsonContent;
    }

    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getScoringEnabled() {
        return isScoringEnabled;
    }

    public void setScoringEnabled(Boolean scoringEnabled) {
        isScoringEnabled = scoringEnabled;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean getHasNumeration() {
        return hasNumeration;
    }

    public void setHasNumeration(Boolean hasNumeration) {
        this.hasNumeration = hasNumeration;
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
    }
}
