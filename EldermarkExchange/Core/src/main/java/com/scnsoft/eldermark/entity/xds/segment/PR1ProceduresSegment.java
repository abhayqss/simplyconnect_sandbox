package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.ISCodedValueForUserDefinedTables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0230ProcedureFunctionalType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Nationalized;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "PR1_Procedures")
public class PR1ProceduresSegment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @Column(name = "procedure_coding_method")
    private String procedureCodingMethod;

    @ManyToOne
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @JoinColumn(name = "procedure_code_id")
    private CECodedElement procedureCode;

    @Nationalized
    @Column(name = "procedure_description")
    private String procedureDescription;

    @Column(name = "procedure_datetime")
    private Date procedureDatetime;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "procedure_functional_type_id")
    private ISCodedValueForUserDefinedTables<HL7CodeTable0230ProcedureFunctionalType> procedureFunctionalType;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "associated_diagnosis_code_id")
    private CECodedElement associatedDiagnosisCode;

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

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getProcedureCodingMethod() {
        return procedureCodingMethod;
    }

    public void setProcedureCodingMethod(String procedureCodingMethod) {
        this.procedureCodingMethod = procedureCodingMethod;
    }

    public CECodedElement getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(CECodedElement procedureCode) {
        this.procedureCode = procedureCode;
    }

    public String getProcedureDescription() {
        return procedureDescription;
    }

    public void setProcedureDescription(String procedureDescription) {
        this.procedureDescription = procedureDescription;
    }

    public Date getProcedureDatetime() {
        return procedureDatetime;
    }

    public void setProcedureDatetime(Date procedureDatetime) {
        this.procedureDatetime = procedureDatetime;
    }

    public ISCodedValueForUserDefinedTables<HL7CodeTable0230ProcedureFunctionalType> getProcedureFunctionalType() {
        return procedureFunctionalType;
    }

    public void setProcedureFunctionalType(ISCodedValueForUserDefinedTables<HL7CodeTable0230ProcedureFunctionalType> procedureFunctionalType) {
        this.procedureFunctionalType = procedureFunctionalType;
    }

    public CECodedElement getAssociatedDiagnosisCode() {
        return associatedDiagnosisCode;
    }

    public void setAssociatedDiagnosisCode(CECodedElement associatedDiagnosisCode) {
        this.associatedDiagnosisCode = associatedDiagnosisCode;
    }
}
