package com.scnsoft.eldermark.entity.inbound.therap.summary.programenrollment;

import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapEntityRecordProcessingSummary;

public class TherapProgramEnrollmentRecordProcessingSummary extends TherapEntityRecordProcessingSummary {

    private String idfFormId;
    private String programName;
    private String programId;
    private Long communityId;
    private boolean alreadyExisted;

    public String getIdfFormId() {
        return idfFormId;
    }

    public void setIdfFormId(String idfFormId) {
        this.idfFormId = idfFormId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getProgramId() {
        return programId;
    }

    public void setProgramId(String programId) {
        this.programId = programId;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public boolean isAlreadyExisted() {
        return alreadyExisted;
    }

    public void setAlreadyExisted(boolean alreadyExisted) {
        this.alreadyExisted = alreadyExisted;
    }

    @Override
    protected boolean shouldSetOkStatus() {
        return communityId != null;
    }

    @Override
    protected String buildWarnMessage() {
        return "System didn't find create community.";
    }
}
