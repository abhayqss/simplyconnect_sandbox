package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * This abstract class is the base class for all Entities in Eldermark Exchange Web application.
 */
@MappedSuperclass
public abstract class BasicEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "database_id", nullable = false, insertable = true, updatable = true)
    private Database database;

    @Column(name = "database_id", nullable = false, insertable = false, updatable = false)
    private long databaseId;

    public BasicEntity() {
    }

    public BasicEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        sb.append("[id=").append(id);
        if (database != null) {
            sb.append(", databaseId=").append(database.getId());
        }
        sb.append("]");
        return sb.toString();
    }

}
