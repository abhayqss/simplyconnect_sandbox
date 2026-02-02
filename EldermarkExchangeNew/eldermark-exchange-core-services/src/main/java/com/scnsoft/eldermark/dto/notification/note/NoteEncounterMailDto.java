package com.scnsoft.eldermark.dto.notification.note;

public class NoteEncounterMailDto implements NoteEncounterViewData {

    private Long clinicianId;
    private String clinicianTitle;
    private String otherClinician;
    private String typeTitle;
    private Long fromDate;
    private Long toDate;
    private Long totalTime;
    private String range;
    private Long units;

    @Override
    public Long getClinicianId() {
        return clinicianId;
    }

    @Override
    public void setClinicianId(Long clinicianId) {
        this.clinicianId = clinicianId;
    }

    @Override
    public String getClinicianTitle() {
        return clinicianTitle;
    }

    @Override
    public void setClinicianTitle(String clinicianTitle) {
        this.clinicianTitle = clinicianTitle;
    }

    @Override
    public String getOtherClinician() {
        return otherClinician;
    }

    @Override
    public void setOtherClinician(String otherClinician) {
        this.otherClinician = otherClinician;
    }

    @Override
    public String getTypeTitle() {
        return typeTitle;
    }

    @Override
    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    @Override
    public Long getFromDate() {
        return fromDate;
    }

    @Override
    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    @Override
    public Long getToDate() {
        return toDate;
    }

    @Override
    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Long getUnits() {
        return units;
    }

    public void setUnits(Long units) {
        this.units = units;
    }
}
