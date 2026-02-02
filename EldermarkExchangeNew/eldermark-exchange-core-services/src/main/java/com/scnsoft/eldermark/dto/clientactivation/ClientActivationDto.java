package com.scnsoft.eldermark.dto.clientactivation;

public class ClientActivationDto {
    private Long intakeDate;
    private String programType;
    private String comment;

    public Long getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Long intakeDate) {
        this.intakeDate = intakeDate;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
