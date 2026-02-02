package com.scnsoft.eldermark.dto.notification.note;

public interface NoteEncounterViewData {

    Long getClinicianId();

    void setClinicianId(Long clinicianId);

    String getClinicianTitle();

    void setClinicianTitle(String clinician);

    String getOtherClinician();

    void setOtherClinician(String clinician);

    String getTypeTitle();

    void setTypeTitle(String typeTitle);

    Long getFromDate();

    void setFromDate(Long fromDate);

    Long getToDate();

    void setToDate(Long toDate);

}
