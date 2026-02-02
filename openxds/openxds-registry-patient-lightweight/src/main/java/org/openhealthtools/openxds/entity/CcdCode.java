package org.openhealthtools.openxds.entity;

import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Immutable
public class CcdCode implements Serializable {

    @Id
    @GeneratedValue
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
}
