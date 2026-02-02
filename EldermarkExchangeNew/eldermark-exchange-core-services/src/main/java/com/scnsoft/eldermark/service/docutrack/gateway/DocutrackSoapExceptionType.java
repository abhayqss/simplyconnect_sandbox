package com.scnsoft.eldermark.service.docutrack.gateway;

import java.util.stream.Stream;

public enum DocutrackSoapExceptionType {

    E_DCI_BAD_DOCUMENT_ID(0x80040209, "The document id was entered incorrectly. Please check and try again.");

    private final long code;
    private final String text;

    DocutrackSoapExceptionType(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public long getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public static DocutrackSoapExceptionType fromCode(long code) {
        return Stream.of(DocutrackSoapExceptionType.values())
                .filter(v -> v.code == code)
                .findFirst()
                .orElse(null);
    }
}
