package com.scnsoft.eldermark.dto.adt.segment;

import com.scnsoft.eldermark.dto.adt.datatype.ClientLocationDto;
import com.scnsoft.eldermark.dto.adt.datatype.DischargeLocationDto;
import com.scnsoft.eldermark.dto.adt.datatype.XCNDto;
import com.scnsoft.eldermark.dto.event.PatientVisitViewData;

import java.util.List;

public class PatientVisitNotificationDto implements PatientVisitViewData {

    private String patientClass;
    private ClientLocationDto assignedPatientLocation;
    private String admissionType;
    private ClientLocationDto priorPatientLocation;
    private List<XCNDto> attendingDoctors;
    private List<XCNDto> referringDoctors;
    private List<XCNDto> consultingDoctors;
    private String preadmitTestIndicator;
    private String readmissionIndicator;
    private String admitSource;
    private List<String> ambulatoryStatuses;
    private List<XCNDto> admittingDoctors;
    private String dischargeDisposition;
    private DischargeLocationDto dischargedToLocation;
    private String servicingFacility;
    private Long admitDate;
    private Long dischargeDate;
    private List<XCNDto> otherHealthcareProviders;

    @Override
    public String getPatientClass() {
        return patientClass;
    }

    @Override
    public void setPatientClass(String patientClass) {
        this.patientClass = patientClass;
    }

    @Override
    public ClientLocationDto getAssignedPatientLocation() {
        return assignedPatientLocation;
    }

    @Override
    public void setAssignedPatientLocation(ClientLocationDto assignedPatientLocation) {
        this.assignedPatientLocation = assignedPatientLocation;
    }

    @Override
    public String getAdmissionType() {
        return admissionType;
    }

    @Override
    public void setAdmissionType(String admissionType) {
        this.admissionType = admissionType;
    }

    @Override
    public ClientLocationDto getPriorPatientLocation() {
        return priorPatientLocation;
    }

    @Override
    public void setPriorPatientLocation(ClientLocationDto priorPatientLocation) {
        this.priorPatientLocation = priorPatientLocation;
    }

    @Override
    public List<XCNDto> getAttendingDoctors() {
        return attendingDoctors;
    }

    @Override
    public void setAttendingDoctors(List<XCNDto> attendingDoctors) {
        this.attendingDoctors = attendingDoctors;
    }

    @Override
    public List<XCNDto> getReferringDoctors() {
        return referringDoctors;
    }

    @Override
    public void setReferringDoctors(List<XCNDto> referringDoctors) {
        this.referringDoctors = referringDoctors;
    }

    @Override
    public List<XCNDto> getConsultingDoctors() {
        return consultingDoctors;
    }

    @Override
    public void setConsultingDoctors(List<XCNDto> consultingDoctors) {
        this.consultingDoctors = consultingDoctors;
    }

    @Override
    public String getPreadmitTestIndicator() {
        return preadmitTestIndicator;
    }

    @Override
    public void setPreadmitTestIndicator(String preadmitTestIndicator) {
        this.preadmitTestIndicator = preadmitTestIndicator;
    }

    @Override
    public String getReadmissionIndicator() {
        return readmissionIndicator;
    }

    @Override
    public void setReadmissionIndicator(String readmissionIndicator) {
        this.readmissionIndicator = readmissionIndicator;
    }

    @Override
    public String getAdmitSource() {
        return admitSource;
    }

    @Override
    public void setAdmitSource(String admitSource) {
        this.admitSource = admitSource;
    }

    @Override
    public List<String> getAmbulatoryStatuses() {
        return ambulatoryStatuses;
    }

    @Override
    public void setAmbulatoryStatuses(List<String> ambulatoryStatuses) {
        this.ambulatoryStatuses = ambulatoryStatuses;
    }

    @Override
    public List<XCNDto> getAdmittingDoctors() {
        return admittingDoctors;
    }

    @Override
    public void setAdmittingDoctors(List<XCNDto> admittingDoctors) {
        this.admittingDoctors = admittingDoctors;
    }

    @Override
    public String getDischargeDisposition() {
        return dischargeDisposition;
    }

    @Override
    public void setDischargeDisposition(String dischargeDisposition) {
        this.dischargeDisposition = dischargeDisposition;
    }

    @Override
    public DischargeLocationDto getDischargedToLocation() {
        return dischargedToLocation;
    }

    @Override
    public void setDischargedToLocation(DischargeLocationDto dischargedToLocation) {
        this.dischargedToLocation = dischargedToLocation;
    }

    @Override
    public String getServicingFacility() {
        return servicingFacility;
    }

    @Override
    public void setServicingFacility(String servicingFacility) {
        this.servicingFacility = servicingFacility;
    }

    @Override
    public Long getAdmitDate() {
        return admitDate;
    }

    @Override
    public void setAdmitDate(Long admitDate) {
        this.admitDate = admitDate;
    }

    @Override
    public Long getDischargeDate() {
        return dischargeDate;
    }

    @Override
    public void setDischargeDate(Long dischargeDate) {
        this.dischargeDate = dischargeDate;
    }

    @Override
    public List<XCNDto> getOtherHealthcareProviders() {
        return otherHealthcareProviders;
    }

    @Override
    public void setOtherHealthcareProviders(List<XCNDto> otherHealthcareProviders) {
        this.otherHealthcareProviders = otherHealthcareProviders;
    }
}
