package com.scnsoft.eldermark.shared.carecoordination;

import java.util.Date;

/**
 * Created by pzhurba on 05-Oct-15.
 */
@Deprecated
public class ProcedureDto {
    private String description;
    private Date dateTime;
    private СECodeDto code;
    private СECodeDto associatedDiagnosisCode;

//    private String functionalType;
//    private String practitioner;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

//    public String getFunctionalType() {
//        return functionalType;
//    }
//
//    public void setFunctionalType(String functionalType) {
//        this.functionalType = functionalType;
//    }
//
//    public String getPractitioner() {
//        return practitioner;
//    }
//
//    public void setPractitioner(String practitioner) {
//        this.practitioner = practitioner;
//    }

    public СECodeDto getCode() {
        return code;
    }

    public void setCode(СECodeDto code) {
        this.code = code;
    }

    public СECodeDto getAssociatedDiagnosisCode() {
        return associatedDiagnosisCode;
    }

    public void setAssociatedDiagnosisCode(СECodeDto associatedDiagnosisCode) {
        this.associatedDiagnosisCode = associatedDiagnosisCode;
    }


}
