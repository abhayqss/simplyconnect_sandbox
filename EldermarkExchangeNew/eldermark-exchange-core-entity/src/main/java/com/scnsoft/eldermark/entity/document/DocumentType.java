package com.scnsoft.eldermark.entity.document;

public enum DocumentType {
    CCD("CCD"),
    CUSTOM("Custom"),
    FACESHEET("Facesheet"),
    FAX("Fax"),
    LAB_RESULT("Lab results"),
    MAP("Map");

    private String title;

    DocumentType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
