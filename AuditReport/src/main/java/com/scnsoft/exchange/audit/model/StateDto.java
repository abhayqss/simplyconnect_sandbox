package com.scnsoft.exchange.audit.model;

public class StateDto {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            this.name = "null";
        } else {
            this.name = name;
        }
    }
}
