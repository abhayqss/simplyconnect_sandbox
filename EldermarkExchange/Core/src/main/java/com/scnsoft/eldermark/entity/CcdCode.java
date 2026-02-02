package com.scnsoft.eldermark.entity;

import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Immutable
@NamedQueries({
        @NamedQuery(name = "ccdCode.findByValueSetAndCode", query = "SELECT o from CcdCode o WHERE o.valueSetName = :valueSet AND o.code = :code"),
        @NamedQuery(name = "ccdCode.findByValueSet", query = "SELECT o from CcdCode o  WHERE o.valueSetName = :valueSet"),
        @NamedQuery(name = "ccdCode.findByCodeAndCodeSystem", query = "SELECT o FROM CcdCode o WHERE o.code=:code AND o.codeSystem=:codeSystem"),
        @NamedQuery(name = "ccdCode.findByCodeAndCodeSystemAndValueSet", query = "SELECT o FROM CcdCode o WHERE o.code=:code AND o.codeSystem=:codeSystem " +
                "AND o.valueSet=:valueSet"),
        @NamedQuery(name = "ccdCode.findByValueSetAndDisplayName", query = "SELECT o from CcdCode o " +
                "WHERE o.valueSetName = :valueSet AND o.displayName = :displayName"),
        @NamedQuery(name = "ccdCode.findByValueSetCodeAndCodeSystem", query = "SELECT o from CcdCode o " +
                "WHERE o.valueSet = :valueSet AND o.codeSystem=:codeSystem ORDER BY o.displayName ASC"),
        @NamedQuery(name = "ccdCode.findWithSameDisplayName", query = "SELECT o from CcdCode o " +
                "WHERE o.displayName=(SELECT c.displayName from CcdCode c WHERE c.id=:id) AND o.codeSystem IN (:codeSystems) ORDER BY o.displayName ASC"),
        @NamedQuery(name = "ccdCode.findByCodeOrDisplayName", query = "SELECT o from CcdCode o " +
                "WHERE (o.displayName LIKE :search OR o.code LIKE :search) AND o.codeSystem IN (:codeSystems) ORDER BY o.displayName ASC"),
        @NamedQuery(name = "ccdCode.countByCodeOrDisplayName", query = "SELECT COUNT(o.id) from CcdCode o " +
                "WHERE (o.displayName LIKE :search OR o.code LIKE :search) AND o.codeSystem IN (:codeSystems)")
})

public class CcdCode implements Serializable, Comparable<CcdCode>, ConceptDescriptor {

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

    @Override
    public int compareTo(CcdCode o) {
        return ObjectUtils.compare(this.getCode(), o.getCode());
    }

}
