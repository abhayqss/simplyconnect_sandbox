package com.scnsoft.eldermark.api.shared.dto.adt;

import com.scnsoft.eldermark.api.shared.dto.adt.datatype.PLPatientLocationDto;
import com.scnsoft.eldermark.api.shared.dto.adt.datatype.DLDDischargeLocationDto;

import java.util.Date;
import java.util.List;

public class PV1PatientVisitSegmentDto implements SegmentDto {
    private String patientClass;
    private PLPatientLocationDto assignedPatientLocation;
    private String admissionType;
    private PLPatientLocationDto priorPatientLocation;
    private List<String> attendingDoctors;
    private List<String> referringDoctors;
    private List<String> consultingDoctors;
    private String preadmitTestIndicator;
    private String readmissionIndicator;
    private String admitSource;
    private List<String> ambulatoryStatuses;
    private String dischargeDisposition;
    private DLDDischargeLocationDto dischargedToLocation;
    private Date admitDatetime;
    private Date dischargeDatetime;
    private String servicingFacility;

    public String getPatientClass() {
        return patientClass;
    }

    public void setPatientClass(String patientClass) {
        this.patientClass = patientClass;
    }

    public PLPatientLocationDto getAssignedPatientLocation() {
        return assignedPatientLocation;
    }

    public void setAssignedPatientLocation(PLPatientLocationDto assignedPatientLocation) {
        this.assignedPatientLocation = assignedPatientLocation;
    }

    public String getAdmissionType() {
        return admissionType;
    }

    public void setAdmissionType(String admissionType) {
        this.admissionType = admissionType;
    }

    public PLPatientLocationDto getPriorPatientLocation() {
        return priorPatientLocation;
    }

    public void setPriorPatientLocation(PLPatientLocationDto priorPatientLocation) {
        this.priorPatientLocation = priorPatientLocation;
    }

    public List<String> getAttendingDoctors() {
        return attendingDoctors;
    }

    public void setAttendingDoctors(List<String> attendingDoctors) {
        this.attendingDoctors = attendingDoctors;
    }

    public List<String> getReferringDoctors() {
        return referringDoctors;
    }

    public void setReferringDoctors(List<String> referringDoctors) {
        this.referringDoctors = referringDoctors;
    }

    public List<String> getConsultingDoctors() {
        return consultingDoctors;
    }

    public void setConsultingDoctors(List<String> consultingDoctors) {
        this.consultingDoctors = consultingDoctors;
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

    public List<String> getAmbulatoryStatuses() {
        return ambulatoryStatuses;
    }

    public void setAmbulatoryStatuses(List<String> ambulatoryStatuses) {
        this.ambulatoryStatuses = ambulatoryStatuses;
    }

    public String getDischargeDisposition() {
        return dischargeDisposition;
    }

    public void setDischargeDisposition(String dischargeDisposition) {
        this.dischargeDisposition = dischargeDisposition;
    }

    public DLDDischargeLocationDto getDischargedToLocation() {
        return dischargedToLocation;
    }

    public void setDischargedToLocation(DLDDischargeLocationDto dischargedToLocation) {
        this.dischargedToLocation = dischargedToLocation;
    }

    public Date getAdmitDatetime() {
        return admitDatetime;
    }

    public void setAdmitDatetime(Date admitDatetime) {
        this.admitDatetime = admitDatetime;
    }

    public Date getDischargeDatetime() {
        return dischargeDatetime;
    }

    public void setDischargeDatetime(Date dischargeDatetime) {
        this.dischargeDatetime = dischargeDatetime;
    }

    public String getServicingFacility() {
        return servicingFacility;
    }

    public void setServicingFacility(String servicingFacility) {
        this.servicingFacility = servicingFacility;
    }
}
