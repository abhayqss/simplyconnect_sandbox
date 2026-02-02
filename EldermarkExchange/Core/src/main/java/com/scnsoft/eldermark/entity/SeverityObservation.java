package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
public class SeverityObservation extends LegacyTableAwareEntity {
    @ManyToOne
    @JoinColumn(name="severity_code_id")
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
