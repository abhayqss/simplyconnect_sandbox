package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(name = "ConcreteCcdCode")
@PrimaryKeyJoinColumn(name = "id")
@NamedQuery(name = "concreteCcdCode.getCcdCode", query = "SELECT o FROM ConcreteCcdCode o " +
        "WHERE o.code = :code AND o.codeSystem = :codeSystem AND (:valueSet IS NULL OR o.valueSet = :valueSet)")
public class ConcreteCcdCode extends AnyCcdCode {

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "code_system", nullable = false)
    private String codeSystem;

    @Column(name = "code_system_name")
    private String codeSystemName;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "value_set")
    private String valueSet;

    @Column(name = "value_set_name")
    private String valueSetName;

    private Boolean inactive;

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getCodeSystem() {
        return codeSystem;
    }

    @Override
    public void setCodeSystem(String codeSystem) {
        this.codeSystem = codeSystem;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getCodeSystemName() {
        return codeSystemName;
    }

    @Override
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
}
