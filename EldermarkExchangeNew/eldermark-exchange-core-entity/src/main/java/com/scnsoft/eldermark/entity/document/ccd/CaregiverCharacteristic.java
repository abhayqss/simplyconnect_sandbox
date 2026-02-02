package com.scnsoft.eldermark.entity.document.ccd;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class CaregiverCharacteristic extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @ManyToOne
    @JoinColumn
    private CcdCode value;

    @Column(name = "participant_time_low")
    private Date participantTimeLow;

    @Column(name = "participant_time_high")
    private Date participantTimeHigh;

    @ManyToOne
    @JoinColumn(name = "participant_role_code_id")
    private CcdCode participantRoleCode;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }

    public Date getParticipantTimeLow() {
        return participantTimeLow;
    }

    public void setParticipantTimeLow(Date participantTimeLow) {
        this.participantTimeLow = participantTimeLow;
    }

    public Date getParticipantTimeHigh() {
        return participantTimeHigh;
    }

    public void setParticipantTimeHigh(Date participantTimeHigh) {
        this.participantTimeHigh = participantTimeHigh;
    }

    public CcdCode getParticipantRoleCode() {
        return participantRoleCode;
    }

    public void setParticipantRoleCode(CcdCode participantRoleCode) {
        this.participantRoleCode = participantRoleCode;
    }
}
