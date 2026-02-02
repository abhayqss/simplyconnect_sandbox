package com.scnsoft.eldermark.entity.document;

import com.scnsoft.eldermark.entity.ConceptDescriptor;
import com.scnsoft.eldermark.entity.ValueSet;
import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Table
@Immutable
public class CcdCode implements Serializable, Comparable<CcdCode>, ConceptDescriptor {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
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

    @Column(name = "is_interpretation", columnDefinition = "int")
    private boolean interpretation;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeSystem() {
        return codeSystem;
    }

    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodeSystemName() {
        return codeSystemName;
    }

    public void setCodeSystemName(String codeSystemName) {
        this.codeSystemName = codeSystemName;
    }

    public String getValueSet() {
        return valueSet;
    }

    public void setValueSet(String valueSet) {
        this.valueSet = valueSet;
    }

    public String getValueSetName() {
        return valueSetName;
    }

    public void setValueSetName(String valueSetName) {
        this.valueSetName = valueSetName;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public List<ValueSet> getValueSets() {
        return valueSets;
    }

    public void setValueSets(List<ValueSet> valueSets) {
        this.valueSets = valueSets;
    }

    public boolean isInterpretation() {
        return interpretation;
    }

    public void setInterpretation(boolean interpretation) {
        this.interpretation = interpretation;
    }

    @Override
    public int compareTo(CcdCode o) {
        return ObjectUtils.compare(this.getCode(), o.getCode());
    }
}
