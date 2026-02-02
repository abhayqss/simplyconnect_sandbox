package com.scnsoft.eldermark.shared;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlJavaTypeAdapter(DocumentTypeXmlAdapter.class)
public enum DocumentType {
    CCD("ccd"),
    CUSTOM("custom"),
    NWHIN("nwhin"),
    FACESHEET("facesheet"),
    FAX("fax"),
    LAB_RESULTS("lab results");

    private final String name;

    DocumentType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DocumentType getTypeByName(String name) {
        DocumentType type = CUSTOM;

        if (CCD.getName().equals(name)) {
            type = CCD;
        } else if (FACESHEET.getName().equals(name)) {
            type = FACESHEET;
        }

        return type;
    }
}
