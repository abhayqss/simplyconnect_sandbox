package com.scnsoft.exchange.adt.entity;


import java.util.Map;

public class Iti18RequestDto {
    private String host;
    private String port;
    private String returnType;
    private String storedQueryId;
    private String patientId;
    private String createTimeFrom;
    private String createTimeTo;
    private String healthCareFacilityTypeCode;
    private String documentEntryUniqueId;
    private String documentEntryEntryUUID;

    private Boolean patientCriteria;
    private Boolean docStatusCriteria;
    private Boolean createTimeFromCriteria;
    private Boolean createTimeToCriteria;
    private Boolean healthcareFacilityTypeCodeCriteria;
    private Boolean documentEntryUniqueIdCriteria;
    private Boolean documentEntryEntryUUIDCriteria;

    private String templateFor;

    private String template;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public String getStoredQueryId() {
        return storedQueryId;
    }

    public void setStoredQueryId(String storedQueryId) {
        this.storedQueryId = storedQueryId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getCreateTimeFrom() {
        return createTimeFrom;
    }

    public void setCreateTimeFrom(String createTimeFrom) {
        this.createTimeFrom = createTimeFrom;
    }

    public String getCreateTimeTo() {
        return createTimeTo;
    }

    public void setCreateTimeTo(String createTimeTo) {
        this.createTimeTo = createTimeTo;
    }

    public String getHealthCareFacilityTypeCode() {
        return healthCareFacilityTypeCode;
    }

    public void setHealthCareFacilityTypeCode(String healthCareFacilityTypeCode) {
        this.healthCareFacilityTypeCode = healthCareFacilityTypeCode;
    }

    public String getDocumentEntryUniqueId() {
        return documentEntryUniqueId;
    }

    public void setDocumentEntryUniqueId(String documentEntryUniqueId) {
        this.documentEntryUniqueId = documentEntryUniqueId;
    }

    public String getDocumentEntryEntryUUID() {
        return documentEntryEntryUUID;
    }

    public void setDocumentEntryEntryUUID(String documentEntryEntryUUID) {
        this.documentEntryEntryUUID = documentEntryEntryUUID;
    }

    public Boolean getPatientCriteria() {
        return patientCriteria;
    }

    public void setPatientCriteria(Boolean patientCriteria) {
        this.patientCriteria = patientCriteria;
    }

    public Boolean getDocStatusCriteria() {
        return docStatusCriteria;
    }

    public void setDocStatusCriteria(Boolean docStatusCriteria) {
        this.docStatusCriteria = docStatusCriteria;
    }

    public Boolean getCreateTimeFromCriteria() {
        return createTimeFromCriteria;
    }

    public void setCreateTimeFromCriteria(Boolean createTimeFromCriteria) {
        this.createTimeFromCriteria = createTimeFromCriteria;
    }

    public Boolean getCreateTimeToCriteria() {
        return createTimeToCriteria;
    }

    public void setCreateTimeToCriteria(Boolean createTimeToCriteria) {
        this.createTimeToCriteria = createTimeToCriteria;
    }

    public Boolean getHealthcareFacilityTypeCodeCriteria() {
        return healthcareFacilityTypeCodeCriteria;
    }

    public void setHealthcareFacilityTypeCodeCriteria(Boolean healthcareFacilityTypeCodeCriteria) {
        this.healthcareFacilityTypeCodeCriteria = healthcareFacilityTypeCodeCriteria;
    }

    public Boolean getDocumentEntryUniqueIdCriteria() {
        return documentEntryUniqueIdCriteria;
    }

    public void setDocumentEntryUniqueIdCriteria(Boolean documentEntryUniqueIdCriteria) {
        this.documentEntryUniqueIdCriteria = documentEntryUniqueIdCriteria;
    }

    public Boolean getDocumentEntryEntryUUIDCriteria() {
        return documentEntryEntryUUIDCriteria;
    }

    public void setDocumentEntryEntryUUIDCriteria(Boolean documentEntryEntryUUIDCriteria) {
        this.documentEntryEntryUUIDCriteria = documentEntryEntryUUIDCriteria;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplateFor() {
        return templateFor;
    }

    public void setTemplateFor(String templateFor) {
        this.templateFor = templateFor;
    }
}
