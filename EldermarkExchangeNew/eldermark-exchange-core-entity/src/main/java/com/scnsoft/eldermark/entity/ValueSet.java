package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.document.CcdCode;

import javax.persistence.*;
import java.util.List;

/*
This entity should be used for adding new value sets to application instead if 'valueSet' column in CcdCode
 */
@Entity
@Table(name = "ValueSet")
public class ValueSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "oid")
    private String oid;

    @Column(name = "name")
    private String name;


    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @ManyToMany
    @JoinTable(name = "ValueSet_CcdCode", joinColumns = {@JoinColumn(name = "value_set_id")}, inverseJoinColumns = {@JoinColumn(name = "ccd_code_id")})
    private List<CcdCode> codes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<CcdCode> getCodes() {
        return codes;
    }

    public void setCodes(List<CcdCode> codes) {
        this.codes = codes;
    }
}
