package com.scnsoft.eldermark.entity.event.incident;

import javax.persistence.*;

@Entity
@Table(name = "IncidentWitness")
public class IncidentWitness extends PersonData {

    @Column(name = "report")
    private String report;

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }
}
