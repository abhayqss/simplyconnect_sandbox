package com.scnsoft.eldermark.entity.document.ccd;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.scnsoft.eldermark.entity.basic.LegacyTableAwareEntity;
import com.scnsoft.eldermark.entity.document.CcdCode;

@Entity
public class SeverityObservation extends LegacyTableAwareEntity {
    private static final long serialVersionUID = 1L;

    @ManyToOne
    @JoinColumn(name = "severity_code_id")
    private CcdCode severityCode;

    @Column(name = "severity_text")
    private String severityText;

    public CcdCode getSeverityCode() {
        return severityCode;
    }

    public void setSeverityCode(CcdCode severityCode) {
        this.severityCode = severityCode;
    }

    public String getSeverityText() {
        return severityText;
    }

    public void setSeverityText(String severityText) {
        this.severityText = severityText;
    }
}