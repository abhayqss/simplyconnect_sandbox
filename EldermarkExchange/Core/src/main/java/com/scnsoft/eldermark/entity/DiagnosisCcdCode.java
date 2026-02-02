package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;

@Entity
@javax.persistence.Table(name = "DiagnosisCcdCode")
@PrimaryKeyJoinColumn(name = "id")
public class DiagnosisCcdCode extends AnyCcdCode {

    @Column(name = "code")
    private String code;

    @Column(name = "code_system")
    private String codeSystem;

    @Column(name = "code_system_name")
    private String codeSystemName;

    @Column(name = "display_name")
    private String displayName;

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
}
