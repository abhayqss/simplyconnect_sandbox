package com.scnsoft.eldermark.monitoring.dto;

public enum ItemType {
    ENDPOINT("Endpoint"),
    SERVICE("Service"),
    DISK_SPACE("Disk space");

    private final String name;

    ItemType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
