package com.scnsoft.eldermark.dto.signature;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;

import java.util.HashMap;
import java.util.Map;

public class DocumentSignatureTemplatePreviewRequestDto implements DocumentSignatureRequestSecurityFieldsAware {
    private Long templateId;
    private Long clientId;
    private Long documentId;

    @JsonIgnore
    private Integer timezoneOffset;

    private final Map<String, Object> templateFieldValues = new HashMap<>();

    public Long getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    @JsonAnyGetter
    public Map<String, Object> getTemplateFieldValues() {
        return templateFieldValues;
    }

    @JsonAnySetter
    public void setTemplateFieldValue(String name, Object value) {
        templateFieldValues.put(name, value);
    }
}
