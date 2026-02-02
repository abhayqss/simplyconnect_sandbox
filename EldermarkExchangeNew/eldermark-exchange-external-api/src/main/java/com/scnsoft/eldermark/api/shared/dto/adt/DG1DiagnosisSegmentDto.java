package com.scnsoft.eldermark.api.shared.dto.adt;

import com.scnsoft.eldermark.api.shared.dto.adt.datatype.CECodedElementDto;

import java.util.Date;
import java.util.List;

public class DG1DiagnosisSegmentDto implements SegmentDto {
    private String setId;
    private String diagnosisCodingMethod;
    private CECodedElementDto diagnosisCode;
    private String diagnosisDescription;
    private Date diagnosisDateTime;
    private String diagnosisType;
    private List<String> diagnosingClinicianList;

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

    public Date getDiagnosisDateTime() {
        return diagnosisDateTime;
    }

    public void setDiagnosisDateTime(Date diagnosisDateTime) {
        this.diagnosisDateTime = diagnosisDateTime;
    }

    public String getDiagnosisType() {
        return diagnosisType;
    }

    public void setDiagnosisType(String diagnosisType) {
        this.diagnosisType = diagnosisType;
    }

    public List<String> getDiagnosingClinicianList() {
        return diagnosingClinicianList;
    }

    public void setDiagnosingClinicianList(List<String> diagnosingClinicianList) {
        this.diagnosingClinicianList = diagnosingClinicianList;
    }
}
