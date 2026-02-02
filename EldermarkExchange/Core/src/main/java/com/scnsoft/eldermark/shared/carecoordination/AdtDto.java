package com.scnsoft.eldermark.shared.carecoordination;

import java.util.Date;

/**
 * Created by knetkachou on 10/12/2016.
 */
public class AdtDto {
    private Long residentId;
    private Date eventDate;
    private Long msgId;
    private Boolean newPatient;
    private String databaseOid;


    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getMsgId() {
        return msgId;
    }

    public Boolean getNewPatient() {
        return newPatient;
    }

    public void setNewPatient(Boolean newPatient) {
        this.newPatient = newPatient;
    }

    public String getDatabaseOid() {
        return databaseOid;
    }

    public void setDatabaseOid(String databaseOid) {
        this.databaseOid = databaseOid;
    }

    @Override
    public String toString() {
        return "AdtDto{" +
                "residentId=" + residentId +
                ", eventDate=" + eventDate +
                ", msgId=" + msgId +
                ", newPatient=" + newPatient +
                ", databaseOid='" + databaseOid + '\'' +
                '}';
    }
}
