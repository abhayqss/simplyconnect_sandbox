package com.scnsoft.eldermark.entity.event.incident;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;

@Entity
@Table(name = "IncidentWeatherConditionType")
@Immutable
public class IncidentWeatherConditionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_free_text", nullable = false)
    private boolean isFreeText;

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

    public boolean getFreeText() {
        return isFreeText;
    }

    public void setFreeText(boolean freeText) {
        isFreeText = freeText;
    }
}
