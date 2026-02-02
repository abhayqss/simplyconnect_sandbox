package com.scnsoft.eldermark.dto.event;

public interface EventEssentialsViewData {

    String getAuthor();

    void setAuthor(String author);

    String getAuthorRole();

    void setAuthorRole(String authorRole);

    Long getDate();

    void setDate(Long date);

    String getTypeTitle();

    void setTypeTitle(String typeTitle);

    boolean getIsEmergencyDepartmentVisit();

    void setIsEmergencyDepartmentVisit(boolean emergencyDepartmentVisit);

    boolean getIsOvernightInpatient();

    void setIsOvernightInpatient(boolean overnightInPatient);

    String getDeviceId();

    void setDeviceId(String deviceId);

    String getTypeCode();

    void setTypeCode(String typeCode);

    Long getRecordedDate();

    void setRecordedDate(Long recordedDate);
}
