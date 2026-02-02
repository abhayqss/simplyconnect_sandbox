package com.scnsoft.eldermark.dto.event;

import com.scnsoft.eldermark.dto.adt.datatype.ClientLocationDto;
import com.scnsoft.eldermark.dto.adt.datatype.DischargeLocationDto;
import com.scnsoft.eldermark.dto.adt.datatype.XCNDto;

import java.util.List;

public interface PatientVisitViewData {
    String getPatientClass();

    void setPatientClass(String patientClass);

    ClientLocationDto getAssignedPatientLocation();

    void setAssignedPatientLocation(ClientLocationDto assignedPatientLocation);

    String getAdmissionType();

    void setAdmissionType(String admissionType);

    ClientLocationDto getPriorPatientLocation();

    void setPriorPatientLocation(ClientLocationDto priorPatientLocation);

    List<XCNDto> getAttendingDoctors();

    void setAttendingDoctors(List<XCNDto> attendingDoctors);

    List<XCNDto> getReferringDoctors();

    void setReferringDoctors(List<XCNDto> referringDoctors);

    List<XCNDto> getConsultingDoctors();

    void setConsultingDoctors(List<XCNDto> consultingDoctors);

    String getPreadmitTestIndicator();

    void setPreadmitTestIndicator(String preadmitTestIndicator);

    String getReadmissionIndicator();

    void setReadmissionIndicator(String readmissionIndicator);

    String getAdmitSource();

    void setAdmitSource(String admitSource);

    List<String> getAmbulatoryStatuses();

    void setAmbulatoryStatuses(List<String> ambulatoryStatuses);

    List<XCNDto> getAdmittingDoctors();

    void setAdmittingDoctors(List<XCNDto> admittingDoctors);

    String getDischargeDisposition();

    void setDischargeDisposition(String dischargeDisposition);

    DischargeLocationDto getDischargedToLocation();

    void setDischargedToLocation(DischargeLocationDto dischargedToLocation);

    String getServicingFacility();

    void setServicingFacility(String servicingFacility);

    Long getAdmitDate();

    void setAdmitDate(Long admitDate);

    Long getDischargeDate();

    void setDischargeDate(Long dischargeDate);

    List<XCNDto> getOtherHealthcareProviders();

    void setOtherHealthcareProviders(List<XCNDto> otherHealthcareProviders);
}
