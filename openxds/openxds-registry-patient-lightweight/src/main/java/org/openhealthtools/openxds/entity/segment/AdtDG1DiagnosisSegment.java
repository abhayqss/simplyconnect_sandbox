package org.openhealthtools.openxds.entity.segment;

import org.hibernate.annotations.Cascade;
import org.openhealthtools.openxds.entity.datatype.CECodedElement;
import org.openhealthtools.openxds.entity.datatype.ISCodedValueForUserDefinedTables;
import org.openhealthtools.openxds.entity.datatype.XCNExtendedCompositeIdNumberAndNameForPersons;
import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable0052DiagnosisType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "ADT_SGMNT_DG1_Diagnosis")
public class AdtDG1DiagnosisSegment implements AdtBaseMessageSegment, Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @Column(name = "diagnosis_coding_method")
    private String diagnosisCodingMethod;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "diagnosis_code_id")
    private CECodedElement diagnosisCode;

    @Column(name = "diagnosis_description")
    private String diagnosisDescription;

    @Column(name = "diagnosis_date_time")
    private Date diagnosisDateTime;

    @OneToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "diagnosis_type_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0052DiagnosisType> diagnosisType;

    @OneToMany(cascade = CascadeType.ALL)
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

    public Date getDiagnosisDateTime() {
        return diagnosisDateTime;
    }

    public void setDiagnosisDateTime(Date diagnosisDateTime) {
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
