package com.scnsoft.eldermark.dto.adt.segment;

import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.dto.adt.datatype.XCNDto;

import java.util.List;

public class AdtDiagnosisDto {

    private String setId;
    private String diagnosisCodingMethod;
    private CECodedElementDto diagnosisCode;
    private String diagnosisDescription;
    private Long diagnosisDateTime;
    private String diagnosisType;
    private List<XCNDto> diagnosingClinicians;

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

    public CECodedElementDto getDiagnosisCode() {
        return diagnosisCode;
    }

    public void setDiagnosisCode(CECodedElementDto diagnosisCode) {
        this.diagnosisCode = diagnosisCode;
    }

    public String getDiagnosisDescription() {
        return diagnosisDescription;
    }

    public void setDiagnosisDescription(String diagnosisDescription) {
        this.diagnosisDescription = diagnosisDescription;
    }

    public Long getDiagnosisDateTime() {
        return diagnosisDateTime;
    }

    public void setDiagnosisDateTime(Long diagnosisDateTime) {
        this.diagnosisDateTime = diagnosisDateTime;
    }

    public String getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(String diagnosisType) {
        this.diagnosisType = diagnosisType;
    }

    public List<XCNDto> getDiagnosingClinicians() {
        return diagnosingClinicians;
    }

    public void setDiagnosingClinicians(List<XCNDto> diagnosingClinicians) {
        this.diagnosingClinicians = diagnosingClinicians;
    }

}
