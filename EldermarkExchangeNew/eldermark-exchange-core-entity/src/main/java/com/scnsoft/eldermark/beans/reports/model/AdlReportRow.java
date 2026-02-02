package com.scnsoft.eldermark.beans.reports.model;

import com.scnsoft.eldermark.beans.reports.enums.AdlReportField;

import java.util.Map;

public class AdlReportRow {
    private String communityName;
    private String clientName;
    private Long clientId;
    private Map<AdlReportField, String> fieldsWithContent;

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public Map<AdlReportField, String> getFieldsWithContent() {
        return fieldsWithContent;
    }

    public void setFieldsWithContent(Map<AdlReportField, String> fieldsWithContent) {
        this.fieldsWithContent = fieldsWithContent;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
