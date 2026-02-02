package org.openhealthtools.openxds.entity.segment;

import org.hibernate.annotations.Cascade;
import org.openhealthtools.openxds.entity.datatype.DLDDischargeLocation;
import org.openhealthtools.openxds.entity.datatype.ISCodedValueForUserDefinedTables;
import org.openhealthtools.openxds.entity.datatype.PLPatientLocation;
import org.openhealthtools.openxds.entity.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import org.openhealthtools.openxds.entity.hl7table.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "PV1_PatientVisitSegment")
public class PV1PatientVisitSegment implements AdtBaseMessageSegment, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "patient_class_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0004PatientClass> patientClass;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "assigned_patient_location_id", referencedColumnName = "id")
    private PLPatientLocation assignedPatientLocation;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "admission_type_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0007AdmissionType> admissionType;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "prior_patient_location_id", referencedColumnName = "id")
    private PLPatientLocation priorPatientLocation;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "attending_doctor_id")
    private XCNExtendedCompositeIdNumberAndNameForPersons attendingDoctor;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "reffering_doctor_id")
    private XCNExtendedCompositeIdNumberAndNameForPersons refferingDoctor;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "consulting_doctor_id")
    private XCNExtendedCompositeIdNumberAndNameForPersons consultingDoctor;

    @Column(name = "preadmit_test_indicator")
    private String preadmitTestIndicator;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "readmission_indicator_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0092ReadmissionIndicator> readmissionIndicator;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "admit_source_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0023AdmitSource> admitSource;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "ADT_FIELD_PV1_AmbulatoryStatus_LIST",
            joinColumns = @JoinColumn(name = "PV1_Id"),
            inverseJoinColumns = @JoinColumn(name = "IS_Id")
    )
    private List<ISCodedValueForUserDefinedTables<HL7CodeTable0009AmbulatoryStatus>> ambulatoryStatuses;

    @Column(name = "discharge_disposition")
    private String dischargeDisposition;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "discharged_to_location_id")
    private DLDDischargeLocation dischargedToLocation;

    @Column(name = "admit_datetime")
    private Date admitDatetime;

    @Column(name = "discharge_datetime")
    private Date dischargeDatetime;

    @Column(name = "servicing_facility")
    private String servicingFacility;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

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
