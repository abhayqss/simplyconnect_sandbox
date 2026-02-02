package com.scnsoft.eldermark;

import java.util.List;

public class TableInfo {
    private final List<String> dependencyTables;

    public TableInfo(List<String> dependencyTables) {
        this.dependencyTables = dependencyTables;
    }

    public List<String> getDependencyTables() {
        return dependencyTables;
    }
}
