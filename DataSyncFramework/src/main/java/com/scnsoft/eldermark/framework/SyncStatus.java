package com.scnsoft.eldermark.framework;

public enum SyncStatus {
    //There's also "O" sync status which means "Orphan record", but it's not used in actual code
    EMPTY(""), NOT_SYNCHRONIZED("N"), SYNCHRONIZED("S"), IN_PROGRESS("P"), ORPHAN("O"), ARCHIVED("A");

    private final String value;

    private SyncStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
