package com.scnsoft.eldermark.dto.notes;

import com.scnsoft.eldermark.dto.notification.note.NoteEncounterViewData;

public class EncounterDto implements NoteEncounterViewData {
    private Long typeId;
    private String typeTitle;
    private Long clinicianId;
    private String clinicianTitle;
    private String otherClinician;
    private Long fromDate;
    private Long toDate;

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public Long getClinicianId() {
        return clinicianId;
    }

    public void setClinicianId(Long clinicianId) {
        this.clinicianId = clinicianId;
    }

    public String getClinicianTitle() {
        return clinicianTitle;
    }

    public void setClinicianTitle(String clinicianTitle) {
        this.clinicianTitle = clinicianTitle;
    }

    public String getOtherClinician() {
        return otherClinician;
    }

    public void setOtherClinician(String otherClinician) {
        this.otherClinician = otherClinician;
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
