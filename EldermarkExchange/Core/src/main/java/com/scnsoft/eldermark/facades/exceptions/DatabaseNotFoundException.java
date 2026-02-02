package com.scnsoft.eldermark.facades.exceptions;

public class DatabaseNotFoundException extends RuntimeException {
    private Long databaseId;

    public DatabaseNotFoundException(Long databaseId) {
        super("Database #" + databaseId + " not found");
        this.databaseId = databaseId;
    }

    public Long getDatabaseId() {
        return databaseId;
    }
}

