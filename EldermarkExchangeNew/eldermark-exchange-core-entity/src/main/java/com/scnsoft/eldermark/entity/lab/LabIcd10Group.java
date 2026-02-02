package com.scnsoft.eldermark.entity.lab;

import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "LabIcd10Group")
@Immutable
public class LabIcd10Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "name")
    private String name;
    @OneToMany(mappedBy = "groupId")
    private List<LabIcd10Code> codes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LabIcd10Code> getCodes() {
        return codes;
    }

    public void setCodes(List<LabIcd10Code> codes) {
        this.codes = codes;
    }
}
