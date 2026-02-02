package com.scnsoft.eldermark.framework.fk;

public class FKResolveError {
    private final String description;

    public FKResolveError(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
