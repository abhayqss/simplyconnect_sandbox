package com.scnsoft.eldermark.shared.carecoordination.assessments;

public class SlumsAssessmentScoringGroupDto {

    private String severityShort;

    private String scoreForPatientsWithHighSchoolEducation;

    private String scoreForPatientsWithoutHighSchoolEducation;

    public String getSeverityShort() {
        return severityShort;
    }

    public void setSeverityShort(String severityShort) {
        this.severityShort = severityShort;
    }

    public String getScoreForPatientsWithHighSchoolEducation() {
        return scoreForPatientsWithHighSchoolEducation;
    }

    public void setScoreForPatientsWithHighSchoolEducation(String scoreForPatientsWithHighSchoolEducation) {
        this.scoreForPatientsWithHighSchoolEducation = scoreForPatientsWithHighSchoolEducation;
    }

    public String getScoreForPatientsWithoutHighSchoolEducation() {
        return scoreForPatientsWithoutHighSchoolEducation;
    }

    public void setScoreForPatientsWithoutHighSchoolEducation(String scoreForPatientsWithoutHighSchoolEducation) {
        this.scoreForPatientsWithoutHighSchoolEducation = scoreForPatientsWithoutHighSchoolEducation;
    }
}
