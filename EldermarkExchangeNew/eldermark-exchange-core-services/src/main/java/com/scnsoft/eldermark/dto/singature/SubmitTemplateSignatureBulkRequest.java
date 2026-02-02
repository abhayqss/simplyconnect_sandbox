package com.scnsoft.eldermark.dto.singature;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.beans.projection.TemplateSignatureBulkRequestSecurityFieldsAware;
import com.scnsoft.eldermark.entity.Employee;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SubmitTemplateSignatureBulkRequest implements TemplateSignatureBulkRequestSecurityFieldsAware {

    private Long expirationDate;
    private List<Long> clientIds;
    private Employee requestedBy;

    @NotEmpty
    private List<@Valid TemplateInfo> data;

    public static class TemplateInfo {
        @NotNull
        private Long templateId;
        private Set<Long> signatureAreaIds;
        private Map<String, Object> templateFieldValues;

        public Long getTemplateId() {
            return templateId;
        }

        public void setTemplateId(Long templateId) {
            this.templateId = templateId;
        }

        public Set<Long> getSignatureAreaIds() {
            return signatureAreaIds;
        }

        public void setSignatureAreaIds(Set<Long> signatureAreaIds) {
            this.signatureAreaIds = signatureAreaIds;
        }

        public Map<String, Object> getTemplateFieldValues() {
            return templateFieldValues;
        }

        public void setTemplateFieldValues(Map<String, Object> templateFieldValues) {
            this.templateFieldValues = templateFieldValues;
        }
    }

    @JsonIgnore
    private Integer timezoneOffset;

    public List<TemplateInfo> getData() {
        return data;
    }

    public void setData(List<TemplateInfo> data) {
        this.data = data;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public List<Long> getClientIds() {
        return clientIds;
    }

    @Override
    public List<Long> getTemplateIds() {
        return data.stream()
                .map(TemplateInfo::getTemplateId)
                .collect(Collectors.toList());
    }

    public void setClientIds(List<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public Employee getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(Employee requestedBy) {
        this.requestedBy = requestedBy;
    }
}
