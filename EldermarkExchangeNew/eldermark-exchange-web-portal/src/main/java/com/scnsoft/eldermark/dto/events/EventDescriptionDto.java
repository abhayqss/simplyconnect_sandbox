package com.scnsoft.eldermark.dto.events;

import com.scnsoft.eldermark.dto.event.EventDescriptionViewData;
import com.scnsoft.eldermark.dto.event.PccEventAdtRecordDetails;

import javax.validation.constraints.Size;

public class EventDescriptionDto implements EventDescriptionViewData {

    @Size(max = 5000)
    private String location;
    private Boolean hasInjury;
    @Size(max = 5000)
    private String situation;
    @Size(max = 5000)
    private String background;
    private boolean isFollowUpExpected;
    @Size(max = 5000)
    private String followUpDetails;
    @Size(max = 5000)
    private String assessment;

    private PccEventAdtRecordDetails pccEventAdtRecordDetails;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public boolean getHasInjury() {
        return hasInjury;
    }

    public void setHasInjury(boolean hasInjury) {
        this.hasInjury = hasInjury;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public boolean getIsFollowUpExpected() {
        return isFollowUpExpected;
    }

    public void setIsFollowUpExpected(boolean followUpExpected) {
        isFollowUpExpected = followUpExpected;
    }

    public String getFollowUpDetails() {
        return followUpDetails;
    }

    public void setFollowUpDetails(String followUpDetails) {
        this.followUpDetails = followUpDetails;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public PccEventAdtRecordDetails getPccEventAdtRecordDetails() {
        return pccEventAdtRecordDetails;
    }

    public void setPccEventAdtRecordDetails(PccEventAdtRecordDetails pccEventAdtRecordDetails) {
        this.pccEventAdtRecordDetails = pccEventAdtRecordDetails;
    }
}
