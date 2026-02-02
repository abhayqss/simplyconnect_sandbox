package com.scnsoft.eldermark.dump.model;

public abstract class Dump {
    String metaInformation;

    public String getMetaInformation() {
        return metaInformation;
    }

    public void setMetaInformation(String metaInformation) {
        this.metaInformation = metaInformation;
    }

    public abstract DumpType getDumpType();
}
