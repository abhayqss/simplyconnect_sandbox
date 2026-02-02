package com.scnsoft.eldermark.entity;

public enum Responsibility {
    R("Responsible", false, false),
    A("Accountable", true, true),
    C("Consulted", true, true),
    I("Informed", true, true),
    V("Viewable", true, true),
    N("Not Viewable", false, true);

    private final String description;
    private final boolean changeable;
    private final boolean assignable;

    Responsibility(final String description, final boolean changeable, boolean assignable) {
        this.description = description;
        this.changeable = changeable;
        this.assignable = assignable;
    }

    public String getDescription() {
        return description;
    }

    public boolean isChangeable() {
        return changeable;
    }

    public boolean isAssignable() {
        return assignable;
    }
}