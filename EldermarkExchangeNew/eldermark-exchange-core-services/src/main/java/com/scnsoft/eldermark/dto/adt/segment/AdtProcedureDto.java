package com.scnsoft.eldermark.dto.adt.segment;

import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;

public class AdtProcedureDto {
    private String setId;
    private String procedureCodingMethod;
    private CECodedElementDto procedureCode;
    private String procedureDescription;
    private Long procedureDatetime;
    private String procedureFunctionalType;
    private CECodedElementDto associatedDiagnosisCode;

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public String getProcedureCodingMethod() {
        return procedureCodingMethod;
    }

    public void setProcedureCodingMethod(String procedureCodingMethod) {
        this.procedureCodingMethod = procedureCodingMethod;
    }

    public CECodedElementDto getProcedureCode() {
        return procedureCode;
    }

    public void setProcedureCode(CECodedElementDto procedureCode) {
        this.procedureCode = procedureCode;
    }

    public String getProcedureDescription() {
        return procedureDescription;
    }

    public void setProcedureDescription(String procedureDescription) {
        this.procedureDescription = procedureDescription;
    }

    public Long getProcedureDatetime() {
        return procedureDatetime;
    }

    public void setProcedureDatetime(Long procedureDatetime) {
        this.procedureDatetime = procedureDatetime;
    }

    public String getProcedureFunctionalType() {
        return procedureFunctionalType;
    }

    public void setProcedureFunctionalType(String procedureFunctionalType) {
        this.procedureFunctionalType = procedureFunctionalType;
    }

    public CECodedElementDto getAssociatedDiagnosisCode() {
        return associatedDiagnosisCode;
    }

    public void setAssociatedDiagnosisCode(CECodedElementDto associatedDiagnosisCode) {
        this.associatedDiagnosisCode = associatedDiagnosisCode;
    }

}
