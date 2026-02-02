package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
@Table(name = "DrugVehicle")
public class DrugVehicle extends BasicEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private CcdCode code;

    @Column(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CcdCode getCode() {
        return code;
    }

    public void setCode(CcdCode code) {
        this.code = code;
    }
}
