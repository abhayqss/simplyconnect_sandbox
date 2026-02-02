package com.scnsoft.eldermark.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.document.AnyCcdCode;
import com.scnsoft.eldermark.entity.document.ccd.ConcreteCcdCode;

@Entity
@Table(name = "InterpretiveCcdCode")
@PrimaryKeyJoinColumn(name = "id")
@NamedQuery(name = "interpretiveCcdCode.getCcdCode", query = "SELECT o FROM InterpretiveCcdCode o " +
        "WHERE o.referredCcdCode=:originalCode AND o.displayName=:displayName")
public class InterpretiveCcdCode extends AnyCcdCode {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name="referred_ccd_code", nullable = false)
    private ConcreteCcdCode referredCcdCode;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    public ConcreteCcdCode getReferredCcdCode() {
        return referredCcdCode;
    }

    public void setReferredCcdCode(ConcreteCcdCode referredCcdCode) {
        this.referredCcdCode = referredCcdCode;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOriginalDisplayName() {
        return referredCcdCode.getDisplayName();
    }

    @Override
    public String getCode() {
        return referredCcdCode.getCode();
    }

    @Override
    public void setCode(String code) {
        // do not modify the underlying CCD code
    }

    @Override
    public String getCodeSystem() {
        return referredCcdCode.getCodeSystem();
    }

    @Override
    public void setCodeSystem(String codeSystem) {
        // do not modify the underlying CCD code
    }

    @Override
    public String getCodeSystemName() {
        return referredCcdCode.getCodeSystemName();
    }

    @Override
    public void setCodeSystemName(String codeSystemName) {
        // do not modify the underlying CCD code
    }

}
