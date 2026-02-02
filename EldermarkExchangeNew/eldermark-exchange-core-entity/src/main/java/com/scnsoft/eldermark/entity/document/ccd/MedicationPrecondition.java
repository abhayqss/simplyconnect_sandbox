package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class MedicationPrecondition extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private CcdCode code;

    @ManyToOne
    @JoinColumn(name = "value_code_id")
    private CcdCode value;

    @Column
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public CcdCode getValue() {
        return value;
    }

    public void setValue(CcdCode value) {
        this.value = value;
    }
}
