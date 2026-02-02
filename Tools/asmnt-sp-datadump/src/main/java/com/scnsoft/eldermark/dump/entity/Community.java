package com.scnsoft.eldermark.dump.entity;


import javax.persistence.*;

@Entity
@Table(name = "Organization")
public class Community extends BasicEntity {

    @Column(columnDefinition = "nvarchar(255)")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
