package com.scnsoft.eldermark.dto.event;

public interface EventDescriptionViewData {
    String getLocation();

    void setLocation(String location);

    boolean getHasInjury();

    void setHasInjury(boolean hasInjury);

    String getSituation();

    void setSituation(String situation);

    String getBackground();

    void setBackground(String background);

    String getAssessment();

    void setAssessment(String assessment);

    boolean getIsFollowUpExpected();

    void setIsFollowUpExpected(boolean followUpExpected);

    String getFollowUpDetails();

    void setFollowUpDetails(String followUpDetails);

    PccEventAdtRecordDetails getPccEventAdtRecordDetails();

    void setPccEventAdtRecordDetails(PccEventAdtRecordDetails pccEventAdtRecordDetails);
}
