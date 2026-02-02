package com.scnsoft.eldermark.beans.reports.model.intune;

public class InTuneReportClientInfo {

    private boolean hasAssessments;
    private boolean hasChangesInTheLastTwoAssessments;

    public boolean getHasAssessments() {
        return hasAssessments;
    }

    public void setHasAssessments(boolean hasAssessments) {
        this.hasAssessments = hasAssessments;
    }

    public boolean getHasChangesInTheLastTwoAssessments() {
        return hasChangesInTheLastTwoAssessments;
    }

    public void setHasChangesInTheLastTwoAssessments(boolean hasChangesInTheLastTwoAssessments) {
        this.hasChangesInTheLastTwoAssessments = hasChangesInTheLastTwoAssessments;
    }
}
