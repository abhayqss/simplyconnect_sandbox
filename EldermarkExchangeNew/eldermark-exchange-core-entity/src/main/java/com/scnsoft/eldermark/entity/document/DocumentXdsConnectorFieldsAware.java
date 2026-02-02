package com.scnsoft.eldermark.entity.document;

import com.scnsoft.eldermark.beans.projection.IdAware;

import java.time.Instant;

public interface DocumentXdsConnectorFieldsAware extends IdAware {
    String getUuid();
    String getHash();
    String getUniqueId();
    Integer getSize();
    Instant getCreationTime();
    String getMimeType();
    String getDocumentTitle();
    Boolean getVisible();
    boolean isEldermarkShared();
}
