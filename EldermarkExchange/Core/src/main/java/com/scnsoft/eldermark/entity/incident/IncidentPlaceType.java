package com.scnsoft.eldermark.entity.incident;

import javax.persistence.*;

import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "IncidentPlaceType")
@Immutable
public class IncidentPlaceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_free_text", nullable = false)
    private Boolean isFreeText;

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

    public Boolean getFreeText() {
        return isFreeText;
    }

    public void setFreeText(Boolean freeText) {
        isFreeText = freeText;
    }
}
