package com.scnsoft.eldermark.dto.signature;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scnsoft.eldermark.beans.security.projection.dto.DocumentSignatureRequestSecurityFieldsAware;
import com.scnsoft.eldermark.entity.signature.SignatureRequestNotificationMethod;
import com.scnsoft.eldermark.entity.signature.SignatureRequestRecipientType;
import com.scnsoft.eldermark.validation.SpELAssert;
import com.scnsoft.eldermark.validation.ValidationRegExpConstants;
import org.apache.commons.lang3.StringUtils;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@SpELAssert.List({
        @SpELAssert(
                applyIf = "notificationMethod != null && notificationMethod.name().equals('SMS')",
                value = "#isNotEmpty(phone)",
                message = "phone {javax.validation.constraints.NotEmpty.message}",
                helpers = StringUtils.class
        ),
        @SpELAssert(
                applyIf = "notificationMethod != null && notificationMethod.name().equals('EMAIL')",
                value = "#isNotEmpty(email)",
                message = "email {javax.validation.constraints.NotEmpty.message}",
                helpers = StringUtils.class
        )
})
public class SubmitTemplateSignatureRequestsDto {

    private Long clientId;

    @JsonIgnore
    private Integer timezoneOffset;

    @NotNull
    private SignatureRequestRecipientType recipientType;

    @NotNull
    private Long recipientId;

    @NotNull
    private Long expirationDate;

    private SignatureRequestNotificationMethod notificationMethod;

    @Pattern(regexp = ValidationRegExpConstants.PHONE_REGEXP)
    private String phone;

    @Pattern(regexp = ValidationRegExpConstants.EMAIL_REGEXP)
    private String email;

    private String message;

    @NotEmpty
    private List<@Valid TemplateInfo> data;

    public static class TemplateInfo {
        @NotNull
        private Long templateId;
        private Long documentId;
        private Set<Long> signatureAreaIds;
        private Map<String, Object> templateFieldValues;

        public Long getTemplateId() {
            return templateId;
        }

        public void setTemplateId(Long templateId) {
            this.templateId = templateId;
        }

        public Long getDocumentId() {
            return documentId;
        }

        public void setDocumentId(Long documentId) {
            this.documentId = documentId;
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

    public List<DocumentSignatureRequestSecurityFieldsAware> toSecurityFieldsAwareList() {
        return data.stream()
                .map(it -> DocumentSignatureRequestSecurityFieldsAware.of(clientId, it.getTemplateId(), it.getDocumentId()))
                .collect(Collectors.toList());
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

    public SignatureRequestRecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(SignatureRequestRecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public SignatureRequestNotificationMethod getNotificationMethod() {
        return notificationMethod;
    }

    public void setNotificationMethod(SignatureRequestNotificationMethod notificationMethod) {
        this.notificationMethod = notificationMethod;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<TemplateInfo> getData() {
        return data;
    }

    public void setData(List<TemplateInfo> data) {
        this.data = data;
    }
}
