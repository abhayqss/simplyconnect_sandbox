package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This abstract class is the base class for all auditable Entities in Eldermark Exchange Web application.
 */
@MappedSuperclass
public abstract class BasicAuditableEntity extends AuditableEntity implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id", nullable = false, insertable = true, updatable = true)
    private Database database;

    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
    private long databaseId;

    public BasicAuditableEntity() {
    }

    public BasicAuditableEntity(Long id) {
        setId(id);
    }

    public Database getDatabase() {
        return database;
    }

    public long getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(long databaseId) {
        this.databaseId = databaseId;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public String getDatabaseAlternativeId() {
        return database.getAlternativeId();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[id=").append(getId());
        if (database != null) {
            sb.append(", databaseId=").append(database.getId());
        }
        sb.append("]");
        return sb.toString();
    }

}
