package com.scnsoft.eldermark.api.shared.dto;

import java.util.Date;

/**
 * Created by pzhurba on 05-Oct-15.
 */
@Deprecated
public class PatientVisitDto {
    private String attendingDoctor;
    private String referringDoctor;
    private String consultingDoctor;
//    private String hospitalService;
//    private String temporaryLocation;
    private String ambulatoryStatus;
    private String patientClass;

    private String admissionType;
    private String preadmitTestIndicator;
    private String readmissionIndicator;
    private String admitSource;
    private String dischargeDisposition;
    private String dischargedToLocation;
    private Date admitDateTime;
    private Date dischargeDateTime;

    public String getAttendingDoctor() {
        return attendingDoctor;
    }

    public void setAttendingDoctor(String attendingDoctor) {
        this.attendingDoctor = attendingDoctor;
    }

    public String getReferringDoctor() {
        return referringDoctor;
    }

    public void setReferringDoctor(String referringDoctor) {
        this.referringDoctor = referringDoctor;
    }

    public String getConsultingDoctor() {
        return consultingDoctor;
    }

    public void setConsultingDoctor(String consultingDoctor) {
        this.consultingDoctor = consultingDoctor;
    }

    public String getAmbulatoryStatus() {
        return ambulatoryStatus;
    }

    public void setAmbulatoryStatus(String ambulatoryStatus) {
        this.ambulatoryStatus = ambulatoryStatus;
    }

    public String getPatientClass() {
        return patientClass;
    }

    public void setPatientClass(String patientClass) {
        this.patientClass = patientClass;
    }

    public String getAdmissionType() {
        return admissionType;
    }

    public void setAdmissionType(String admissionType) {
        this.admissionType = admissionType;
    }

    public String getPreadmitTestIndicator() {
        return preadmitTestIndicator;
    }

    public void setPreadmitTestIndicator(String preadmitTestIndicator) {
        this.preadmitTestIndicator = preadmitTestIndicator;
    }

    public String getReadmissionIndicator() {
        return readmissionIndicator;
    }

    public void setReadmissionIndicator(String readmissionIndicator) {
        this.readmissionIndicator = readmissionIndicator;
    }

    public String getAdmitSource() {
        return admitSource;
    }

    public void setAdmitSource(String admitSource) {
        this.admitSource = admitSource;
    }

    public String getDischargeDisposition() {
        return dischargeDisposition;
    }

    public void setDischargeDisposition(String dischargeDisposition) {
        this.dischargeDisposition = dischargeDisposition;
    }

    public String getDischargedToLocation() {
        return dischargedToLocation;
    }

    public void setDischargedToLocation(String dischargedToLocation) {
        this.dischargedToLocation = dischargedToLocation;
    }

    public Date getAdmitDateTime() {
        return admitDateTime;
    }

    public void setAdmitDateTime(Date admitDateTime) {
        this.admitDateTime = admitDateTime;
    }

    public Date getDischargeDateTime() {
        return dischargeDateTime;
    }

    public void setDischargeDateTime(Date dischargeDateTime) {
        this.dischargeDateTime = dischargeDateTime;
    }
}
