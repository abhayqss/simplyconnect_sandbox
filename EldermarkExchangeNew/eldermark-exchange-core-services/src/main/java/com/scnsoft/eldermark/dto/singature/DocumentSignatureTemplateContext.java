package com.scnsoft.eldermark.dto.singature;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.Document;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DocumentSignatureTemplateContext {

    private DocumentSignatureTemplate template;
    private Document document;
    private Employee currentEmployee;
    private Client client;
    private Community community;
    private Integer timezoneOffset;
    private Map<String, Object> fieldValues = Map.of();
    private Collection<Long> signatureAreaIds = List.of();

    private boolean isRequestFromMultipleClients;

    public DocumentSignatureTemplateContext() {
    }

    public DocumentSignatureTemplateContext(DocumentSignatureTemplate template, Community community) {
        this.template = template;
        this.community = community;
    }

    public DocumentSignatureTemplate getTemplate() {
        return template;
    }

    public void setTemplate(DocumentSignatureTemplate template) {
        this.template = template;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Employee getCurrentEmployee() {
        return currentEmployee;
    }

    public void setCurrentEmployee(Employee currentEmployee) {
        this.currentEmployee = currentEmployee;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public Map<String, Object> getFieldValues() {
        return fieldValues;
    }

    public void setFieldValues(Map<String, Object> fieldValues) {
        this.fieldValues = fieldValues;
    }

    public Collection<Long> getSignatureAreaIds() {
        return signatureAreaIds;
    }

    public void setSignatureAreaIds(Collection<Long> signatureAreaIds) {
        this.signatureAreaIds = signatureAreaIds;
    }

    public boolean getIsRequestFromMultipleClients() {
        return isRequestFromMultipleClients;
    }

    public void setRequestFromMultipleClients(boolean requestFromMultipleClients) {
        isRequestFromMultipleClients = requestFromMultipleClients;
    }
}
