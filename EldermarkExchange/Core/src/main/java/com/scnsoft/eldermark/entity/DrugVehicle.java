package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "DrugVehicle")
public class DrugVehicle extends BasicEntity {
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
