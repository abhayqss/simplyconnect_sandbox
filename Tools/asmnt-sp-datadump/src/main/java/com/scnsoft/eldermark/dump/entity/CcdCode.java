package com.scnsoft.eldermark.dump.entity;


import org.apache.commons.lang3.ObjectUtils;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
@Immutable
public class CcdCode extends DisplayableNamedEntity implements Serializable, Comparable<CcdCode> {

    private static final long serialVersionUID = 1L;

    @Column(name = "code")
    private String code;

    @Column(name = "code_system")
    private String codeSystem;

    @Column(name = "code_system_name")
    private String codeSystemName;

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
