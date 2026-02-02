package com.scnsoft.eldermark;

public final class ForeignKey {
    private final String referencingColumnName;
    private final String referencedTableName;

    public ForeignKey(String referencingColumnName, String referencedTableName) {
        this.referencingColumnName = referencingColumnName;
        this.referencedTableName = referencedTableName;
    }

    public String getReferencingColumnName() {
        return referencingColumnName;
    }

    public String getReferencedTableName() {
        return referencedTableName;
    }
}
