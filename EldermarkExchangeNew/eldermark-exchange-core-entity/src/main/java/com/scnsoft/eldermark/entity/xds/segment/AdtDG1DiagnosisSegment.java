package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0052DiagnosisType;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(name = "ADT_SGMNT_DG1_Diagnosis")
public class AdtDG1DiagnosisSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @Column(name = "diagnosis_coding_method")
    private String diagnosisCodingMethod;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "diagnosis_code_id")
    private CECodedElement diagnosisCode;

    @Column(name = "diagnosis_description")
    private String diagnosisDescription;

    @Column(name = "diagnosis_date_time")
    private Instant diagnosisDateTime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "diagnosis_type_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0052DiagnosisType> diagnosisType;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(
            name = "ADT_FIELD_DG1_DiagnosingClinician_LIST",
            joinColumns = @JoinColumn(name = "DG1_Id"),
            inverseJoinColumns = @JoinColumn(name = "XCN_Id")
    )
    private List<XCNExtendedCompositeIdNumberAndNameForPersons> diagnosingClinicianList;

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

    public String getDiagnosisCodingMethod() {
        return diagnosisCodingMethod;
    }

    public void setDiagnosisCodingMethod(String diagnosisCodingMethod) {
        this.diagnosisCodingMethod = diagnosisCodingMethod;
    }

    public CECodedElement getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(CECodedElement diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public String getDiagnosisDescription() {
        return diagnosisDescription;
    }

    public void setDiagnosisDescription(String diagnosisDescription) {
        this.diagnosisDescription = diagnosisDescription;
    }

    public Instant getDiagnosisDateTime() {
        return diagnosisDateTime;
    }

    public void setDiagnosisDateTime(Instant diagnosisDateTime) {
        this.diagnosisDateTime = diagnosisDateTime;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0052DiagnosisType> getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(ISCodedValueForUserDefinedTables<HL7CodeTable0052DiagnosisType> diagnosisType) {
        this.diagnosisType = diagnosisType;
    }

    public List<XCNExtendedCompositeIdNumberAndNameForPersons> getDiagnosingClinicianList() {
        return diagnosingClinicianList;
    }

    public void setDiagnosingClinicianList(List<XCNExtendedCompositeIdNumberAndNameForPersons> diagnosingClinicianList) {
        this.diagnosingClinicianList = diagnosingClinicianList;
    }
}
