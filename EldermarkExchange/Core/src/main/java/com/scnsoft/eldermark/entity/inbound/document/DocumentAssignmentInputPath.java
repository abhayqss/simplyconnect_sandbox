package com.scnsoft.eldermark.entity.inbound.document;

import com.scnsoft.eldermark.entity.Database;

import javax.persistence.*;

@Entity
@Table(name = "DocumentAssignmentInputPath")
public class DocumentAssignmentInputPath {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "database_id", nullable = false)
    private Database database;

    @Column(name = "input_path", nullable = false)
    private String inputPath;

    @Column(name = "disabled", nullable = false)
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

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}



