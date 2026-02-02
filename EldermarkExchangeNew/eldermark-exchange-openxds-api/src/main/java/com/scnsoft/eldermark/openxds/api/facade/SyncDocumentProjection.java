package com.scnsoft.eldermark.openxds.api.facade;

import com.scnsoft.eldermark.entity.document.DocumentFileFieldsAware;
import com.scnsoft.eldermark.entity.document.DocumentXdsConnectorFieldsAware;

import java.time.Instant;

public interface SyncDocumentProjection extends DocumentFileFieldsAware, DocumentXdsConnectorFieldsAware {

    default SyncDocumentProjection withHash(String hash) {

        var delegate = this;

        return new SyncDocumentProjection() {
            @Override
            public Long getId() {
                return delegate.getId();
            }

            @Override
            public String getAuthorOrganizationAlternativeId() {
                return delegate.getAuthorOrganizationAlternativeId();
            }

            @Override
            public String getAuthorLegacyId() {
                return delegate.getAuthorLegacyId();
            }

            @Override
            public String getUuid() {
                return delegate.getUuid();
            }

            @Override
            public String getHash() {
                return hash;
            }

            @Override
            public String getUniqueId() {
                return delegate.getUniqueId();
            }

            @Override
            public Integer getSize() {
                return delegate.getSize();
            }

            @Override
            public Instant getCreationTime() {
                return delegate.getCreationTime();
            }

            @Override
            public String getMimeType() {
                return delegate.getMimeType();
            }

            @Override
            public String getDocumentTitle() {
                return delegate.getDocumentTitle();
            }

            @Override
            public Boolean getVisible() {
                return delegate.getVisible();
            }

            @Override
            public boolean isEldermarkShared() {
                return delegate.isEldermarkShared();
            }
        };
    }
}
