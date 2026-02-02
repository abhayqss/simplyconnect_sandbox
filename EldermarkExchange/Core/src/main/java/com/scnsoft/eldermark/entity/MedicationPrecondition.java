package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class MedicationPrecondition extends BasicEntity {
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
