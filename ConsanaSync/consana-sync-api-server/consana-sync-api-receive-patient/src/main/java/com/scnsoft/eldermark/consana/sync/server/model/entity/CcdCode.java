package com.scnsoft.eldermark.consana.sync.server.model.entity;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Immutable
@Data
public class CcdCode implements ConceptDescriptor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "code_system")
    private String codeSystem;

    @Column(name = "code_system_name")
    private String codeSystemName;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "value_set")
    private String valueSet;

    @Column(name = "value_set_name")
    private String valueSetName;

    @Column(columnDefinition = "int")
    private Boolean inactive;

    @ManyToMany
    @JoinTable(name = "ValueSet_CcdCode", joinColumns = {@JoinColumn(name = "ccd_code_id")}, inverseJoinColumns = {@JoinColumn(name = "value_set_id")})
    private List<ValueSet> valueSets;
}