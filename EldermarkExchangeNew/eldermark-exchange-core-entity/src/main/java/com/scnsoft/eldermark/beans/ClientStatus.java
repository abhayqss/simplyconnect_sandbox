package com.scnsoft.eldermark.beans;

public enum ClientStatus {
    ALL,
    ACTIVE,
    INACTIVE;

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public String getTitle() {
        return this.toString();
    }
}
