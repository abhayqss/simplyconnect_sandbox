package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0078AbnormalFlags;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0085ObservationResultStatusCodesInterpretation;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "OBX_Observation_Result")
public class OBXObservationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @Column(name = "value_type")
    private String valueType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "obsv_identifier_id")
    private CECodedElement obsvIdentifier;

    @OneToMany(mappedBy = "obx", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OBXValue> obsvValues;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "units_id")
    private CECodedElement unitsId;

    @Column(name = "references_range")
    private String referencesRange;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "OBX_IS_abnormal_flags",
            joinColumns = @JoinColumn(name = "obx_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "abnormal_flag_id", nullable = false)
    )
    private List<ISCodedValueForUserDefinedTables<HL7CodeTable0078AbnormalFlags>> abnormalFlags;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "observation_result_status_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0085ObservationResultStatusCodesInterpretation> observationResultStatus;

    @Column(name = "datetime_of_observation")
    private Instant datetimeOfObservation;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performing_org_name_id")
    private XONExtendedCompositeNameAndIdForOrganizations performingOrgName;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performing_org_addr_id")
    private XADPatientAddress performingOrgAddr;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "performing_org_medical_director_id")
    private XCNExtendedCompositeIdNumberAndNameForPersons performingOrgMedicalDirector;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public CECodedElement getObsvIdentifier() {
        return obsvIdentifier;
    }

    public void setObsvIdentifier(CECodedElement obsvIdentifier) {
        this.obsvIdentifier = obsvIdentifier;
    }

    public List<OBXValue> getObsvValues() {
        return obsvValues;
    }

    public void setObsvValues(List<OBXValue> obsvValues) {
        this.obsvValues = obsvValues;
    }

    public CECodedElement getUnitsId() {
        return unitsId;
    }

    public void setUnitsId(CECodedElement unitsId) {
        this.unitsId = unitsId;
    }

    public String getReferencesRange() {
        return referencesRange;
    }

    public void setReferencesRange(String referencesRange) {
        this.referencesRange = referencesRange;
    }

    public List<ISCodedValueForUserDefinedTables<HL7CodeTable0078AbnormalFlags>> getAbnormalFlags() {
        return abnormalFlags;
    }

    public void setAbnormalFlags(List<ISCodedValueForUserDefinedTables<HL7CodeTable0078AbnormalFlags>> abnormalFlags) {
        this.abnormalFlags = abnormalFlags;
    }

    public IDCodedValueForHL7Tables<HL7CodeTable0085ObservationResultStatusCodesInterpretation> getObservationResultStatus() {
        return observationResultStatus;
    }

    public void setObservationResultStatus(IDCodedValueForHL7Tables<HL7CodeTable0085ObservationResultStatusCodesInterpretation> observationResultStatus) {
        this.observationResultStatus = observationResultStatus;
    }

    public Instant getDatetimeOfObservation() {
        return datetimeOfObservation;
    }

    public void setDatetimeOfObservation(Instant datetimeOfObservation) {
        this.datetimeOfObservation = datetimeOfObservation;
    }

    public XONExtendedCompositeNameAndIdForOrganizations getPerformingOrgName() {
        return performingOrgName;
    }

    public void setPerformingOrgName(XONExtendedCompositeNameAndIdForOrganizations performingOrgName) {
        this.performingOrgName = performingOrgName;
    }

    public XADPatientAddress getPerformingOrgAddr() {
        return performingOrgAddr;
    }

    public void setPerformingOrgAddr(XADPatientAddress performingOrgAddr) {
        this.performingOrgAddr = performingOrgAddr;
    }

    public XCNExtendedCompositeIdNumberAndNameForPersons getPerformingOrgMedicalDirector() {
        return performingOrgMedicalDirector;
    }

    public void setPerformingOrgMedicalDirector(XCNExtendedCompositeIdNumberAndNameForPersons performingOrgMedicalDirector) {
        this.performingOrgMedicalDirector = performingOrgMedicalDirector;
    }
}
