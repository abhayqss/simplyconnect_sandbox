package com.scnsoft.eldermark.framework;

public class IdAwareImpl implements IdAware {
    private long id;

    public IdAwareImpl(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
