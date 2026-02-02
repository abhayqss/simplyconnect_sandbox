package com.scnsoft.eldermark.dto.adt.datatype;

public class HDHierarchicDesignatorDto {

    //if renaming of any field is needed - please make sure to do the same changes to #displayHDFields in eventNotificationSecureEmail.vm

    private String namespaceID;
    private String universalID;
    private String universalIDType;

    public String getNamespaceID() {
        return namespaceID;
    }

    public void setNamespaceID(String namespaceID) {
        this.namespaceID = namespaceID;
    }

    public String getUniversalID() {
        return universalID;
    }

    public void setUniversalID(String universalID) {
        this.universalID = universalID;
    }

    public String getUniversalIDType() {
        return universalIDType;
    }

    public void setUniversalIDType(String universalIDType) {
        this.universalIDType = universalIDType;
    }

}
