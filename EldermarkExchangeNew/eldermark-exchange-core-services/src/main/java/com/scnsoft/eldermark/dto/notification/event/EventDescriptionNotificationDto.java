package com.scnsoft.eldermark.dto.notification.event;

import com.scnsoft.eldermark.dto.event.EventDescriptionViewData;
import com.scnsoft.eldermark.dto.event.PccEventAdtRecordDetails;

public class EventDescriptionNotificationDto implements EventDescriptionViewData {

    private String location;
    private boolean hasInjury;
    private String situation;
    private String background;
    private String assessment;
    private boolean isFollowUpExpected;
    private String followUpDetails;

    private PccEventAdtRecordDetails pccEventAdtRecordDetails;

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public boolean getHasInjury() {
        return hasInjury;
    }

    @Override
    public void setHasInjury(boolean hasInjury) {
        this.hasInjury = hasInjury;
    }

    @Override
    public String getSituation() {
        return situation;
    }

    @Override
    public void setSituation(String situation) {
        this.situation = situation;
    }

    @Override
    public String getBackground() {
        return background;
    }

    @Override
    public void setBackground(String background) {
        this.background = background;
    }

    @Override
    public String getAssessment() {
        return assessment;
    }

    @Override
    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    @Override
    public boolean getIsFollowUpExpected() {
        return isFollowUpExpected;
    }

    @Override
    public void setIsFollowUpExpected(boolean followUpExpected) {
        this.isFollowUpExpected = followUpExpected;
    }

    @Override
    public String getFollowUpDetails() {
        return followUpDetails;
    }

    @Override
    public void setFollowUpDetails(String followUpDetails) {
        this.followUpDetails = followUpDetails;
    }

    public PccEventAdtRecordDetails getPccEventAdtRecordDetails() {
        return pccEventAdtRecordDetails;
    }

    public void setPccEventAdtRecordDetails(PccEventAdtRecordDetails pccEventAdtRecordDetails) {
        this.pccEventAdtRecordDetails = pccEventAdtRecordDetails;
    }
}
