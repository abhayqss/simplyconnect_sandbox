package com.scnsoft.eldermark.entity.inbound.marco;

import java.io.File;

public class MarcoInboundFile {

    private File document;
    private File metadataFile;

    public MarcoInboundFile(File document, File metadataFile) {
        this.document = document;
        this.metadataFile = metadataFile;
    }

    public File getDocument() {
        return document;
    }

    public void setDocument(File document) {
        this.document = document;
    }

    public File getMetadataFile() {
        return metadataFile;
    }

    public void setMetadataFile(File metadataFile) {
        this.metadataFile = metadataFile;
    }
}
