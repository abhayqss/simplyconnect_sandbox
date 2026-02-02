package com.scnsoft.eldermark.dto.notification.event;

import com.scnsoft.eldermark.dto.event.EventEssentialsViewData;

public class EventEssentialsNotificationDto implements EventEssentialsViewData {
    private String author;
    private String authorRole;

    private Long date;

    private String typeTitle;
    private boolean isEmergencyDepartmentVisit;
    private boolean isOvernightInpatient;
    private String deviceId;
    private String typeCode;

    private Long recordedDate;

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getAuthorRole() {
        return authorRole;
    }

    @Override
    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    @Override
    public Long getDate() {
        return date;
    }

    @Override
    public void setDate(Long date) {
        this.date = date;
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
    public boolean getIsEmergencyDepartmentVisit() {
        return isEmergencyDepartmentVisit;
    }

    @Override
    public void setIsEmergencyDepartmentVisit(boolean emergencyDepartmentVisit) {
        this.isEmergencyDepartmentVisit = emergencyDepartmentVisit;
    }

    @Override
    public boolean getIsOvernightInpatient() {
        return isOvernightInpatient;
    }

    @Override
    public void setIsOvernightInpatient(boolean overnightInPatient) {
        this.isOvernightInpatient = overnightInPatient;
    }

    @Override
    public String getDeviceId() {
        return deviceId;
    }

    @Override
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String getTypeCode() {
        return typeCode;
    }

    @Override
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Override
    public Long getRecordedDate() {
        return recordedDate;
    }

    @Override
    public void setRecordedDate(Long recordedDate) {
        this.recordedDate = recordedDate;
    }
}
