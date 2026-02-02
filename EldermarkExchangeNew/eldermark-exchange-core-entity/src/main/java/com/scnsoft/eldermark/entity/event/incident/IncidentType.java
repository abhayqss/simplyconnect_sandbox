package com.scnsoft.eldermark.entity.event.incident;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.List;

@Entity
@Immutable
@Table(name = "IncidentType")
public class IncidentType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "incident_level", nullable = false)
    private Integer incidentLevel;

    @Column(name = "name")
    private String name;

    @Column(name = "is_free_text", nullable = false)
    private boolean isFreeText;

    @OneToMany
    @JoinTable(
            name = "IncidentTypeHierarchy",
            joinColumns = @JoinColumn(name = "parent_incident_type_id"),
            inverseJoinColumns = @JoinColumn(name = "child_incident_type_id")
    )
    private List<IncidentType> childIncidentTypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIncidentLevel() {
        return incidentLevel;
    }

    public void setIncidentLevel(Integer incidentLevel) {
        this.incidentLevel = incidentLevel;
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

    public List<IncidentType> getChildIncidentTypes() {
        return childIncidentTypes;
    }

    public void setChildIncidentTypes(List<IncidentType> childIncidentTypes) {
        this.childIncidentTypes = childIncidentTypes;
    }

    @Override
    public String toString() {
        return "IncidentType [id=" + id + ", incidentLevel=" + incidentLevel + ", name=" + name + ", isFreeText="
                + isFreeText + ", childIncidentTypes=" + childIncidentTypes + "]";
    }
}
