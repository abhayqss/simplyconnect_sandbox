package com.scnsoft.eldermark.entity.document;

import javax.persistence.*;

import com.scnsoft.eldermark.entity.document.AnyCcdCode;

@Entity
@Table(name = "UnknownCcdCode")
@PrimaryKeyJoinColumn(name = "id")
@NamedQuery(name = "unknownCcdCode.find", query = "SELECT o FROM UnknownCcdCode o WHERE o.code=:code AND o.codeSystem=:codeSystem")
public class UnknownCcdCode extends AnyCcdCode {
    private static final long serialVersionUID = 1L;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "code_system", nullable = false)
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
