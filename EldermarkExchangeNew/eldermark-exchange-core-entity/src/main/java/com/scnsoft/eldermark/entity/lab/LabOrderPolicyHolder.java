package com.scnsoft.eldermark.entity.lab;

public enum LabOrderPolicyHolder {
    SELF("Self", "SEL"),
    SPOUSE("Spouse", "SPO"),
    PARENT("Parent", "PAR");

    private String displayName;
    private String HL7Code;

    LabOrderPolicyHolder(String displayName, String HL7Code) {
        this.displayName = displayName;
        this.HL7Code = HL7Code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getHL7Code() {
        return HL7Code;
    }
}
