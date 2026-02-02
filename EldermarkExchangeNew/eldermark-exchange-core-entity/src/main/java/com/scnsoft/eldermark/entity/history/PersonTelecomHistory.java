package com.scnsoft.eldermark.entity.history;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "PersonTelecom_History")
public class PersonTelecomHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    //updated on db level
    @Column(name = "updated_datetime", insertable = false, updatable = false)
    private Instant updatedDatetime;

    //updated on db level
    @Column(name = "deleted_datetime", insertable = false, updatable = false)
    private Instant deletedDatetime;

    @Column(name = "person_telecom_id")
    private Long personTelecomId;

    @Column(name = "database_id", nullable = false)
    private Long organizationId;

    @Column(name = "legacy_id", nullable = false)
    private String legacyId;

    @Column(name = "legacy_table", nullable = false)
    private String legacyTable;

    @Column(name = "person_id")
    private Long personId;

    @Column(length = 15, name = "use_code")
    private String useCode;

    @Column(length = 256, name = "value")
    private String value;

    @Column(length = 256, name = "value_normalized", insertable = false, updatable = false)
    private String normalized;

    @Column(name = "sync_qualifier", nullable = false)
    private int syncQualifier;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Instant updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }

    public Instant getDeletedDatetime() {
        return deletedDatetime;
    }

    public void setDeletedDatetime(Instant deletedDatetime) {
        this.deletedDatetime = deletedDatetime;
    }

    public Long getPersonTelecomId() {
        return personTelecomId;
    }

    public void setPersonTelecomId(Long personTelecomId) {
        this.personTelecomId = personTelecomId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(String legacyId) {
        this.legacyId = legacyId;
    }

    public String getLegacyTable() {
        return legacyTable;
    }

    public void setLegacyTable(String legacyTable) {
        this.legacyTable = legacyTable;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

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

    public String getNormalized() {
        return normalized;
    }

    public void setNormalized(String normalized) {
        this.normalized = normalized;
    }

    public int getSyncQualifier() {
        return syncQualifier;
    }

    public void setSyncQualifier(int syncQualifier) {
        this.syncQualifier = syncQualifier;
    }
}
