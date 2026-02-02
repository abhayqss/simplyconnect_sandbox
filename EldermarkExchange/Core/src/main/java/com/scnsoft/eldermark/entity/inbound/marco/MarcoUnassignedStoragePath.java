package com.scnsoft.eldermark.entity.inbound.marco;

import com.scnsoft.eldermark.entity.Database;

import javax.persistence.*;

@Entity
@Table(name = "MarcoUnassignedStoragePath")
public class MarcoUnassignedStoragePath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "database_id", nullable = false)
    private Database database;

    @Column(name = "path", nullable = false)
    private String path;

    @Column(name = "disabled")
    private boolean disabled;

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
