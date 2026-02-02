package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "ValueSet")
@Data
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
}
