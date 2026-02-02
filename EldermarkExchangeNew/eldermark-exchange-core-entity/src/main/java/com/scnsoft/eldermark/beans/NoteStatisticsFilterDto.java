package com.scnsoft.eldermark.beans;

import com.scnsoft.eldermark.entity.note.NoteSubType.EncounterCode;

public class NoteStatisticsFilterDto {

    private Long clientId;

    private EncounterCode code;

    private Long fromDate;

    private Long toDate;

    public NoteStatisticsFilterDto() {

    }

    public NoteStatisticsFilterDto(Long clientId, EncounterCode code, Long fromDate, Long toDate) {
        this.clientId = clientId;
        this.code = code;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public EncounterCode getCode() {
        return code;
    }

    public void setCode(EncounterCode code) {
        this.code = code;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }
}
