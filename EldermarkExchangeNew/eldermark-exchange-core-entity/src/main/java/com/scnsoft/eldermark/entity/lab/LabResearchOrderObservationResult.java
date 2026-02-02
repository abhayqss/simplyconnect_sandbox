package com.scnsoft.eldermark.entity.lab;

import com.scnsoft.eldermark.entity.xds.datatype.XADPatientAddress;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "LabResearchOrderObservationResult")
public class LabResearchOrderObservationResult {

    @Id
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_research_order_id")
    private LabResearchOrder labOrder;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "\"value\"")
    private String value;

    @Column(name = "units_text")
    private String unitsText;

    @Column(name = "limits")
    private String limits;

    @Column(name = "abnormal_flags", columnDefinition = "nvarchar")
    @Nationalized
    private String abnormalFlags;

    @Column(name = "observation_source")
    private String observationSource;

    @Column(name = "datetime_of_observation")
    private Instant datetimeOfObservation;

    @Column(name = "performing_org_name", columnDefinition = "nvarchar(80)")
    @Nationalized
    private String performingOrgName;

    @OneToOne
    @JoinColumn(name = "performing_org_addr_id")
    private XADPatientAddress performingOrgAddr;

    @OneToOne
    @JoinColumn(name = "performing_org_medical_director_id")
    private XCNExtendedCompositeIdNumberAndNameForPersons performingOrgMedicalDirector;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LabResearchOrder getLabOrder() {
        return labOrder;
    }

    public void setLabOrder(LabResearchOrder labOrder) {
        this.labOrder = labOrder;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnitsText() {
        return unitsText;
    }

    public void setUnitsText(String unitsText) {
        this.unitsText = unitsText;
    }

    public String getLimits() {
        return limits;
    }

    public void setLimits(String limits) {
        this.limits = limits;
    }

    public String getAbnormalFlags() {
        return abnormalFlags;
    }

    public void setAbnormalFlags(String abnormalFlags) {
        this.abnormalFlags = abnormalFlags;
    }

    public String getObservationSource() {
        return observationSource;
    }

    public void setObservationSource(String observationSource) {
        this.observationSource = observationSource;
    }

    public Instant getDatetimeOfObservation() {
        return datetimeOfObservation;
    }

    public void setDatetimeOfObservation(Instant datetimeOfObservation) {
        this.datetimeOfObservation = datetimeOfObservation;
    }

    public String getPerformingOrgName() {
        return performingOrgName;
    }

    public void setPerformingOrgName(String performingOrgName) {
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
