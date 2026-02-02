package com.scnsoft.eldermark.hl7v2.model;

import java.util.List;

public class PatientIdentifiersHolder {

    private PersonIdentifier pid2Identifier;
    private List<PersonIdentifier> pid3Identifiers;


    public PersonIdentifier getPid2Identifier() {
        return pid2Identifier;
    }

    public void setPid2Identifier(PersonIdentifier pid2Identifier) {
        this.pid2Identifier = pid2Identifier;
    }

    public List<PersonIdentifier> getPid3Identifiers() {
        return pid3Identifiers;
    }

    public void setPid3Identifiers(List<PersonIdentifier> pid3Identifiers) {
        this.pid3Identifiers = pid3Identifiers;
    }

    @Override
    public String toString() {
        return "PatientIdentifiersHolder{" +
                "pid2Identifier=" + pid2Identifier +
                ", pid3Identifiers=" + pid3Identifiers +
                '}';
    }
}
