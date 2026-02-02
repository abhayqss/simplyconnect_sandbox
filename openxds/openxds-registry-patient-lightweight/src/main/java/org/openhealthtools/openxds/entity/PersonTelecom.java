package org.openhealthtools.openxds.entity;

import javax.persistence.*;

@Entity
public class PersonTelecom extends StringLegacyIdAwareEntity {
    @Column(length = 255,name = "legacy_table", nullable = false)
    private String legacyTable;

    @Column(length = 15, name = "use_code")
    private String useCode;

    @Column(length = 150, name = "value")
    private String value;

    @Column(length = 150, name = "value_normalized")
    private String valueNormalized;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    @Column(name = "sync_qualifier", nullable = false)
    private int syncQualifier;

    public String getUseCode() {
        return useCode;
    }

    public void setUseCode(String useCode) {
        this.useCode = useCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueNormalized() {
        return valueNormalized;
    }

    public void setValueNormalized(String valueNormalized) {
        this.valueNormalized = valueNormalized;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public int getSyncQualifier() {
        return syncQualifier;
    }

    public void setSyncQualifier(int syncQualifier) {
        this.syncQualifier = syncQualifier;
    }

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }
}
