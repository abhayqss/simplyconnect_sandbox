package com.scnsoft.eldermark.cda.service.schema;

import java.util.Objects;

public class TemplateId {
    private final String root;
    private final String extension;

    public TemplateId(String root, String extension) {
        this.root = root;
        this.extension = extension;
    }

    public String getRoot() {
        return root;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public int hashCode() {
        return (root + ":" + String.valueOf(extension)).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TemplateId that = (TemplateId) obj;
        return Objects.equals(getRoot(), that.getRoot()) &&
                Objects.equals(getExtension(), that.getExtension());
    }

    public static TemplateId of(String root, String extension) {
        return new TemplateId(root, extension);
    }

}
