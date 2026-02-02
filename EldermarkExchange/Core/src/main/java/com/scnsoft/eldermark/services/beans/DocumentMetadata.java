package com.scnsoft.eldermark.services.beans;

import java.io.Serializable;

public final class DocumentMetadata implements Serializable {
    private final String documentTitle;
    private final String fileName;
    private final String mimeType;

    private DocumentMetadata(Builder builder) {
        this.documentTitle = builder.documentTitle;
        this.fileName = builder.fileName;
        this.mimeType = builder.mimeType;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static class Builder {
        private String documentTitle;
        private String fileName;
        private String mimeType;

        public Builder setDocumentTitle(String documentTitle) {
            this.documentTitle = documentTitle;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Builder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        public DocumentMetadata build() {
            if (documentTitle == null) {
                throw new NullPointerException("documentTitle cannot be null");
            }

            if (fileName == null) {
                throw new NullPointerException("fileName cannot be null");
            }

            if (mimeType == null) {
                throw new NullPointerException("mimeType cannot be null");
            }

            return new DocumentMetadata(this);
        }
    }
}
