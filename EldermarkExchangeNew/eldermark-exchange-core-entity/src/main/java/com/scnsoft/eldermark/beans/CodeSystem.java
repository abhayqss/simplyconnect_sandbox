package com.scnsoft.eldermark.beans;

import java.util.Set;

public enum CodeSystem {
    ICD_10_CM(Set.of("ICD-10-CM", "ICD10"), "2.16.840.1.113883.6.90"),
    ICD_9_CM(Set.of("ICD-9-CM", "ICD-9CM", "ICD9"), "2.16.840.1.113883.6.103"),
    SNOMED_CT(Set.of("SNOMED-CT", "SNOMED CT"), "2.16.840.1.113883.6.96");

    private Set<String> names;
    private String oid;

    CodeSystem(Set<String> names, String oid) {
        this.names = names;
        this.oid = oid;
    }

    public Set<String> getNames() {
        return names;
    }

    public void setNames(Set<String> names) {
        this.names = names;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }
}
