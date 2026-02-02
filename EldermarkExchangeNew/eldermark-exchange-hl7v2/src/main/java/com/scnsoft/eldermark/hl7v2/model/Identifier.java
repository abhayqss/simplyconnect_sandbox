package com.scnsoft.eldermark.hl7v2.model;

public class Identifier {

    private String namespaceId = null;
    private String universalId = null;
    private String universalIdType = null;

    public Identifier() {
    }

    public Identifier(String namespaceId, String universalId, String universalIdType) {
        this.namespaceId = namespaceId;
        this.universalId = universalId;
        this.universalIdType = universalIdType;
    }

    public String getNamespaceId() {
        return namespaceId;
    }

    public void setNamespaceId(String namespaceId) {
        this.namespaceId = namespaceId;
    }

    public String getUniversalId() {
        return universalId;
    }

    public void setUniversalId(String universalId) {
        this.universalId = universalId;
    }

    public String getUniversalIdType() {
        return universalIdType;
    }

    public void setUniversalIdType(String universalIdType) {
        this.universalIdType = universalIdType;
    }
}
