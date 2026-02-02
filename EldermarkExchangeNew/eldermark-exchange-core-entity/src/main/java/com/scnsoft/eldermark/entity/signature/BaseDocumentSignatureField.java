package com.scnsoft.eldermark.entity.signature;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class BaseDocumentSignatureField extends BaseDocumentSignatureFieldLocation {

    @Column(name = "field_value")
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
