package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class Instructions extends BasicEntity {
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "code_id")
    private CcdCode code;

    @Column
    private String text;

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
