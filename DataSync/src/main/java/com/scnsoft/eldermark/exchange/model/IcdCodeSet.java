package com.scnsoft.eldermark.exchange.model;

/**
 * Created by averazub on 2/16/2017.
 */
public enum IcdCodeSet {
    ICD9CM("2.16.840.1.113883.6.103", "ICD-9-CM"),
    ICD10CM_DIAGNOSIS("2.16.840.1.113883.6.90", "ICD-10-CM"),
    ICD10PCS("2.16.840.1.113883.6.4", "ICD-10-PCS");

    private final String codeSystemOid;
    private final String codeSystemName;

    IcdCodeSet(String codeSystemOid, String codeSystemName) {
        this.codeSystemOid = codeSystemOid;
        this.codeSystemName = codeSystemName;
    }

    public String getCodeSystemOid() {
        return codeSystemOid;
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }
}
