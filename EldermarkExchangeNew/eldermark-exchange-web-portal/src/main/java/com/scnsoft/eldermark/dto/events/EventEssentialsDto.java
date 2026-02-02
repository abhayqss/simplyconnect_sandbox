package com.scnsoft.eldermark.dto.events;

import com.scnsoft.eldermark.dto.event.EventEssentialsViewData;

public class EventEssentialsDto implements EventEssentialsViewData {

    private String author;
    private String authorRole;

    private Long typeId;
    private String typeName;
    private String typeTitle;
    private String typeCode;

    private Long date;
    private boolean isEmergencyDepartmentVisit;
    private boolean isOvernightInpatient;

    private Long recordedDate;
    private String deviceId;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorRole() {
        return authorRole;
    }

    public void setAuthorRole(String authorRole) {
        this.authorRole = authorRole;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public boolean getIsEmergencyDepartmentVisit() {
        return isEmergencyDepartmentVisit;
    }

    public void setIsEmergencyDepartmentVisit(boolean emergencyDepartmentVisit) {
        isEmergencyDepartmentVisit = emergencyDepartmentVisit;
    }

    public boolean getIsOvernightInpatient() {
        return isOvernightInpatient;
    }

    public void setIsOvernightInpatient(boolean overnightInpatient) {
        isOvernightInpatient = overnightInpatient;
    }

    public Long getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Long recordedDate) {
        this.recordedDate = recordedDate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
