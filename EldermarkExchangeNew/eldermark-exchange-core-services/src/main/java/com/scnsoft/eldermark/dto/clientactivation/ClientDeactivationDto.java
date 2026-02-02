package com.scnsoft.eldermark.dto.clientactivation;

public class ClientDeactivationDto {
    private Long exitDate;
    private String deactivationReason;
    private String comment;

    public Long getExitDate() {
        return exitDate;
    }

    public void setExitDate(Long exitDate) {
        this.exitDate = exitDate;
    }

    public String getDeactivationReason() {
        return deactivationReason;
    }

    public void setDeactivationReason(String deactivationReason) {
        this.deactivationReason = deactivationReason;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
