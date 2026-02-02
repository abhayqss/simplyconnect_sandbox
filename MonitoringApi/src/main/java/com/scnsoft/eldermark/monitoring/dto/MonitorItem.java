package com.scnsoft.eldermark.monitoring.dto;

public class MonitorItem {
    private String name;
    private ItemType type;
    private boolean isAvailable;

    public MonitorItem(String name, ItemType type, boolean isAvailable) {
        this.name = name;
        this.type = type;
        this.isAvailable = isAvailable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}
