package com.scnsoft.eldermark.beans.security.projection.dto;

import com.scnsoft.eldermark.beans.projection.ClientIdAware;

public interface DocumentSignatureRequestSecurityFieldsAware extends ClientIdAware {

    Long getTemplateId();
    Long getDocumentId();

    static DocumentSignatureRequestSecurityFieldsAware of(Long clientId, Long templateId) {
        return new DocumentSignatureRequestSecurityFieldsAware() {
            @Override
            public Long getTemplateId() {
                return templateId;
            }

            @Override
            public Long getClientId() {
                return clientId;
            }

            @Override
            public Long getDocumentId() {
                return null;
            }
        };
    }

    static DocumentSignatureRequestSecurityFieldsAware of(Long clientId, Long templateId, Long documentId) {
        return new DocumentSignatureRequestSecurityFieldsAware() {
            @Override
            public Long getTemplateId() {
                return templateId;
            }

            @Override
            public Long getClientId() {
                return clientId;
            }

            @Override
            public Long getDocumentId() {
                return documentId;
            }
        };
    }

}
