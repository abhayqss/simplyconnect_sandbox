package com.scnsoft.eldermark.openxds.api.dto;

import com.scnsoft.eldermark.openxds.api.beans.AdtType;

import javax.validation.constraints.NotNull;

public class AdtDto {

    @NotNull
    private Long residentId;

    private AdtType adtType;
    private Long msgId;
    private Boolean isNewPatient;

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public AdtType getAdtType() {
        return adtType;
    }

    public void setAdtType(AdtType adtType) {
        this.adtType = adtType;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public Boolean getIsNewPatient() {
        return isNewPatient;
    }

    public void setIsNewPatient(Boolean newPatient) {
        isNewPatient = newPatient;
    }

    @Override
    public String toString() {
        return "AdtDto{" +
                "residentId=" + residentId +
                ", adtType=" + adtType +
                ", msgId=" + msgId +
                ", isNewPatient=" + isNewPatient + '\'' +
                '}';
    }
}
