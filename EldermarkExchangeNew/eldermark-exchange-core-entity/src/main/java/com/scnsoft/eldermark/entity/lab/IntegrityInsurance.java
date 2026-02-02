package com.scnsoft.eldermark.entity.lab;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "IntegrityInsurance")
@Immutable
public class IntegrityInsurance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "integrity_id", nullable = false)
    private String integrityId;

    @Column(name = "name", nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIntegrityId() {
        return integrityId;
    }

    public void setIntegrityId(String integrityId) {
        this.integrityId = integrityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
