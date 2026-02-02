package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.DLDDischargeLocation;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.datatype.PLPatientLocation;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import com.scnsoft.eldermark.entity.xds.hl7table.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "PV1_PatientVisitSegment")
//todo rename to PV1PatientVisitSegment
public class PV1ClientVisitSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "patient_class_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0004PatientClass> patientClass;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "assigned_patient_location_id", referencedColumnName = "id")
    private PLPatientLocation assignedPatientLocation;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "admission_type_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0007AdmissionType> admissionType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "prior_patient_location_id", referencedColumnName = "id")
    private PLPatientLocation priorPatientLocation;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "attending_doctor_id")
    private XCNExtendedCompositeIdNumberAndNameForPersons attendingDoctor;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "reffering_doctor_id")
    private XCNExtendedCompositeIdNumberAndNameForPersons refferingDoctor;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "consulting_doctor_id")
    private XCNExtendedCompositeIdNumberAndNameForPersons consultingDoctor;

    @Column(name = "preadmit_test_indicator")
    private String preadmitTestIndicator;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "readmission_indicator_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0092ReadmissionIndicator> readmissionIndicator;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "admit_source_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0023AdmitSource> admitSource;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_FIELD_PV1_AmbulatoryStatus_LIST",
            joinColumns = @JoinColumn(name = "PV1_Id"),
            inverseJoinColumns = @JoinColumn(name = "IS_Id")
    )
    private List<ISCodedValueForUserDefinedTables<HL7CodeTable0009AmbulatoryStatus>> ambulatoryStatuses;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_FIELD_PV1_AdmittingDoctor_LIST",
            joinColumns = @JoinColumn(name = "pv1_id"),
            inverseJoinColumns = @JoinColumn(name = "xcn_id")
    )
    private List<XCNExtendedCompositeIdNumberAndNameForPersons> admittingDoctors;

    @Column(name = "discharge_disposition")
    private String dischargeDisposition;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "discharged_to_location_id")
    private DLDDischargeLocation dischargedToLocation;

    @Column(name = "admit_datetime")
    private Instant admitDatetime;

    @Column(name = "discharge_datetime")
    private Instant dischargeDatetime;

    @Column(name = "servicing_facility")
    private String servicingFacility;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_FIELD_PV1_OtherHealthcareProvider_LIST",
            joinColumns = @JoinColumn(name = "pv1_id"),
            inverseJoinColumns = @JoinColumn(name = "xcn_id")
    )
    private List<XCNExtendedCompositeIdNumberAndNameForPersons> otherHealthcareProviders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0004PatientClass> getPatientClass() {
        return patientClass;
    }

    public void setPatientClass(ISCodedValueForUserDefinedTables<HL7CodeTable0004PatientClass> patientClass) {
        this.patientClass = patientClass;
    }

    public PLPatientLocation getAssignedPatientLocation() {
        return assignedPatientLocation;
    }

    public void setAssignedPatientLocation(PLPatientLocation assignedPatientLocation) {
        this.assignedPatientLocation = assignedPatientLocation;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0007AdmissionType> getAdmissionType() {
        return admissionType;
    }

    public void setAdmissionType(ISCodedValueForUserDefinedTables<HL7CodeTable0007AdmissionType> admissionType) {
        this.admissionType = admissionType;
    }

    public PLPatientLocation getPriorPatientLocation() {
        return priorPatientLocation;
    }

    public void setPriorPatientLocation(PLPatientLocation priorPatientLocation) {
        this.priorPatientLocation = priorPatientLocation;
    }

    public XCNExtendedCompositeIdNumberAndNameForPersons getAttendingDoctor() {
        return attendingDoctor;
    }

    public void setAttendingDoctor(XCNExtendedCompositeIdNumberAndNameForPersons attendingDoctor) {
        this.attendingDoctor = attendingDoctor;
    }

    public XCNExtendedCompositeIdNumberAndNameForPersons getRefferingDoctor() {
        return refferingDoctor;
    }

    public void setRefferingDoctor(XCNExtendedCompositeIdNumberAndNameForPersons refferingDoctor) {
        this.refferingDoctor = refferingDoctor;
    }

    public XCNExtendedCompositeIdNumberAndNameForPersons getConsultingDoctor() {
        return consultingDoctor;
    }

    public void setConsultingDoctor(XCNExtendedCompositeIdNumberAndNameForPersons consultingDoctor) {
        this.consultingDoctor = consultingDoctor;
    }

    public String getPreadmitTestIndicator() {
        return preadmitTestIndicator;
    }

    public void setPreadmitTestIndicator(String preadmitTestIndicator) {
        this.preadmitTestIndicator = preadmitTestIndicator;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0092ReadmissionIndicator> getReadmissionIndicator() {
        return readmissionIndicator;
    }

    public void setReadmissionIndicator(ISCodedValueForUserDefinedTables<HL7CodeTable0092ReadmissionIndicator> readmissionIndicator) {
        this.readmissionIndicator = readmissionIndicator;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0023AdmitSource> getAdmitSource() {
        return admitSource;
    }

    public void setAdmitSource(ISCodedValueForUserDefinedTables<HL7CodeTable0023AdmitSource> admitSource) {
        this.admitSource = admitSource;
    }

    public List<ISCodedValueForUserDefinedTables<HL7CodeTable0009AmbulatoryStatus>> getAmbulatoryStatuses() {
        return ambulatoryStatuses;
    }

    public void setAmbulatoryStatuses(List<ISCodedValueForUserDefinedTables<HL7CodeTable0009AmbulatoryStatus>> ambulatoryStatuses) {
        this.ambulatoryStatuses = ambulatoryStatuses;
    }

    public List<XCNExtendedCompositeIdNumberAndNameForPersons> getAdmittingDoctors() {
        return admittingDoctors;
    }

    public void setAdmittingDoctors(List<XCNExtendedCompositeIdNumberAndNameForPersons> admittingDoctors) {
        this.admittingDoctors = admittingDoctors;
    }

    public String getDischargeDisposition() {
        return dischargeDisposition;
    }

    public void setDischargeDisposition(String dischargeDisposition) {
        this.dischargeDisposition = dischargeDisposition;
    }

    public DLDDischargeLocation getDischargedToLocation() {
        return dischargedToLocation;
    }

    public void setDischargedToLocation(DLDDischargeLocation dischargedToLocation) {
        this.dischargedToLocation = dischargedToLocation;
    }

    public Instant getAdmitDatetime() {
        return admitDatetime;
    }

    public void setAdmitDatetime(Instant admitDatetime) {
        this.admitDatetime = admitDatetime;
    }

    public Instant getDischargeDatetime() {
        return dischargeDatetime;
    }

    public void setDischargeDatetime(Instant dischargeDatetime) {
        this.dischargeDatetime = dischargeDatetime;
    }

    public String getServicingFacility() {
        return servicingFacility;
    }

    public void setServicingFacility(String servicingFacility) {
        this.servicingFacility = servicingFacility;
    }

    public List<XCNExtendedCompositeIdNumberAndNameForPersons> getOtherHealthcareProviders() {
        return otherHealthcareProviders;
    }

    public void setOtherHealthcareProviders(List<XCNExtendedCompositeIdNumberAndNameForPersons> otherHealthcareProviders) {
        this.otherHealthcareProviders = otherHealthcareProviders;
    }
}
