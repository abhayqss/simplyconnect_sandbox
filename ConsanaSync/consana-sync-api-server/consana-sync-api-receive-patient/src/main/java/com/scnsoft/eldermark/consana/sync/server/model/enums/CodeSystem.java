package com.scnsoft.eldermark.consana.sync.server.model.enums;

public enum CodeSystem {

    SNOMED_CT("SNOMED-CT", "2.16.840.1.113883.6.96", "SNOMED Clinical Terms", "http://snomed.info/sct"),
    ICD_10_CM("ICD-10-CM", "2.16.840.1.113883.6.90", "ICD10", "https://www.cms.gov/Medicare/Coding/ICD10/2017-ICD-10-CM-and-GEMs.html"),
    LOINC("LOINC", "2.16.840.1.113883.6.1", "Logical Observation Identifier Names and Codes", "http://loinc.org");

    private final String displayName;
    private final String oid;
    private final String fullName;
    private final String url;

    CodeSystem(String displayName, String oid, String fullName, String url) {
        this.displayName = displayName;
        this.oid = oid;
        this.fullName = fullName;
        this.url = url;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getOid() {
        return oid;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUrl() {
        return url;
    }

    public static CodeSystem findBySystemUrl(String url) {
        for (var code : CodeSystem.values()) {
            if (code.getUrl().equals(url)) {
                return code;
            }
        }
        return null;
    }
}