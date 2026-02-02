package com.scnsoft.eldermark.entity.prospect;

public enum RelatedPartyRelationship {
    SPOUSE("Spouse"),
    LIFE_PARTNER("Life partner"),
    CHILD("Child"),
    GRANDCHILD("Grandchild"),
    PARENT("Parent"),
    CAREGIVER("Caregiver"),
    GUARDIAN("Guardian"),
    EMERGENCY_CONTACT("Emergency contact"),
    FRIEND("Friend"),
    SIBLING("Sibling"),
    OTHER("Other");

    private final String title;

    RelatedPartyRelationship(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
