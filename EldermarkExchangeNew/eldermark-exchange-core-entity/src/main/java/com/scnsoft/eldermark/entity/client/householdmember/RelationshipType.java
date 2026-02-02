package com.scnsoft.eldermark.entity.client.householdmember;

import java.util.Arrays;

public enum RelationshipType {
    PARTNER("Partner / Significant Other"),

    CHILD("Child of Head of Household"),

    PARENT("Parent of Head of Household"),

    SIBLING("Sibling of Head of Household"),

    NON_FAMILY("Non family member");

    private final String name;

    RelationshipType(String name) {
        this.name = name;
    }

    public static RelationshipType getByName(String name) {
        return Arrays.stream(RelationshipType.values())
            .filter(r -> r.getName().equals(name))
            .findFirst().orElse(null);
    }

    public String getName() {
        return name;
    }
}
