package com.scnsoft.eldermark.entity.prospect;

public enum Veteran {
    YES("Yes"),
    NO("No"),
    UNKNOWN("Prospect doesn't know"),
    UNDISCLOSED("Chooses not to disclose");

    private final String title;

    Veteran(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
