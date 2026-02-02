package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
public class CaregiverCharacteristic extends BasicEntity {

    @ManyToOne
    @JoinColumn
    private CcdCode code;

    @ManyToOne
    @JoinColumn
    private CcdCode value;

//    @Column(name = "value_code_system", length = 30)
//    private String valueCodeSystem;

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

//    public String getValueCodeSystem() {
//        return valueCodeSystem;
//    }
//
//    public void setValueCodeSystem(String codeSystem) {
//        this.valueCodeSystem = codeSystem;
//    }

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
