package com.scnsoft.eldermark.consana.sync.client.model.entities;

import javax.persistence.*;
import java.io.Serializable;

@MappedSuperclass
public abstract class BasicEntity implements Serializable {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "database_id", nullable = false)
    private Database database;

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

    public void setDatabase(Database database) {
        this.database = database;
    }
}
