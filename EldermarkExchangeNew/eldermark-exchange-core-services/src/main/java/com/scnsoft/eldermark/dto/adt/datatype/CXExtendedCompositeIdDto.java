package com.scnsoft.eldermark.dto.adt.datatype;

public class CXExtendedCompositeIdDto {

    //if renaming of any field is needed - please make sure to do the same changes to #displayCX in eventNotificationSecureEmail.vm

    private String pId;
    private String identifierTypeCode;

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getIdentifierTypeCode() {
        return identifierTypeCode;
    }

    public void setIdentifierTypeCode(String identifierTypeCode) {
        this.identifierTypeCode = identifierTypeCode;
    }

}
