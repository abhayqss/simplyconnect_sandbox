package com.scnsoft.eldermark.beans;

import java.util.Optional;

public enum AffiliatedCareTeamType {
    REGULAR(true, false),
    PRIMARY(false, true), //todo rename to AFFILIATED?
    REGULAR_AND_PRIMARY(true, true);

    private final boolean includesRegular;
    private final boolean includesPrimary;

    AffiliatedCareTeamType(boolean includesRegular, boolean includesPrimary) {
        this.includesRegular = includesRegular;
        this.includesPrimary = includesPrimary;
    }

    public boolean isIncludesRegular() {
        return includesRegular;
    }

    public boolean isIncludesPrimary() {
        return includesPrimary;
    }

    public Optional<AffiliatedCareTeamType> downсast(AffiliatedCareTeamType anotherType) {
        switch (this) {
            case REGULAR_AND_PRIMARY:
                return Optional.of(anotherType);
            case PRIMARY:
                return anotherType.includesPrimary ? Optional.of(PRIMARY) : Optional.empty();
            case REGULAR:
                return anotherType.includesRegular ? Optional.of(REGULAR) : Optional.empty();
        }
        throw new IllegalArgumentException("Should never get there");
    }
}
