package com.scnsoft.eldermark.entity;

import javax.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"person_id", "sync_qualifier"}),
        indexes = {
                @Index(name = "PersonId_Index", columnList = "person_id")
        })
/*
@org.hibernate.annotations.Table(appliesTo = "PersonTelecom",
        indexes = {
                @Index(name="PersonId_Index", columnNames = "person_id")
        })
        */
@AttributeOverride(name="legacyId", column = @Column(name = "legacy_id", nullable = false, length = 25))
public class PersonTelecom extends StringLegacyTableAwareEntity implements Telecom {

    /**
     * The {@code useCode} attribute indicates the type of telecom
     * @see PersonTelecomCode#name
     */
    @Column(length = 15, name = "use_code")
    private String useCode;

    @Column(length = 150, name = "value")
    private String value;

    @Column(name = "value_normalized_hash", insertable = false, updatable = false)
    private Long valueHash;

    @Column(length = 150, name = "value_normalized", insertable = false, updatable = false)
    private String valueNormalized;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    /**
     * @see PersonTelecomCode#code
     */
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

    public Long getValueHash() {
        return valueHash;
    }

    public void setValueHash(Long valueHash) {
        this.valueHash = valueHash;
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

}
