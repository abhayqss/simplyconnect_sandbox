package com.scnsoft.eldermark.framework;

import java.util.Date;

public class SuccessMessage {
    private Date date;
    private Date syncRevision;
    private DatabaseInfo database;
    private String text;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DatabaseInfo getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseInfo database) {
        this.database = database;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getSyncRevision() {
        return syncRevision;
    }

    public void setSyncRevision(Date syncRevision) {
        this.syncRevision = syncRevision;
    }
}
