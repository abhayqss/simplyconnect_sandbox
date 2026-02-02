package com.scnsoft.eldermark.framework.fk;

import com.scnsoft.eldermark.framework.Utils;

import java.util.List;

public final class FKResolveResult<FK> {
    private FK foreignKeys;
    private List<FKResolveError> errors;

    public FKResolveResult(FK foreignKeys, List<FKResolveError> errors) {
        Utils.ensureNotNull(foreignKeys, "foreignKeys");
        this.foreignKeys = foreignKeys;
        this.errors = errors;
    }

    public boolean isResolved() {
        return errors == null || errors.isEmpty();
    }

    public FK getForeignKeys() {
        return foreignKeys;
    }

    public List<FKResolveError> getErrors() {
        return errors;
    }
}
