package com.scnsoft.eldermark.shared;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

//todo looks like can be removed
@XmlEnum
@XmlType(name = "DocumentType")
public enum DocumentType {
    @XmlEnumValue("CCD")
    CCD("ccd"),

    @XmlEnumValue("CUSTOM")
    CUSTOM("custom"),

    @XmlEnumValue("NWHIN")
    NWHIN("nwhin"),

    @XmlEnumValue("FACESHEET")
    FACESHEET("facesheet");

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
