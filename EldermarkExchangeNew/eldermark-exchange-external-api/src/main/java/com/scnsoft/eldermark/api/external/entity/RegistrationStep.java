package com.scnsoft.eldermark.api.external.entity;

import com.scnsoft.eldermark.api.shared.entity.BaseEntity;

import javax.persistence.*;

@Entity
@Table(name = "UserMobileRegistrationStep")
public class RegistrationStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, columnDefinition = "int")
    private Long id;

    @Column(name = "name")
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
