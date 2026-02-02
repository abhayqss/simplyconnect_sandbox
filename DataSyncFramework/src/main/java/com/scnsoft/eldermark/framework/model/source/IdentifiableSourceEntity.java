package com.scnsoft.eldermark.framework.model.source;

public abstract class IdentifiableSourceEntity<ID> extends SourceEntity {
    public abstract ID getId();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdentifiableSourceEntity that = (IdentifiableSourceEntity) o;
        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
