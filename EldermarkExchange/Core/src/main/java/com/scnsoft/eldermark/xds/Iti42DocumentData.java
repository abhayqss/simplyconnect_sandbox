package com.scnsoft.eldermark.xds;

import java.util.Date;

/**
 * Created by averazub on 8/9/2016.
 */
public class Iti42DocumentData {
    private String contentTypeCode;
    private String contentTypeCodeLocalized;
    private String confidentiallyCode;
    private String confidentiallyCodeLocalized;
    private Date creationTime;
    private String documentUUID;
    private String formatCode;
    private String formatCodeLocalized;
    private String healthcareFacilityTypeCode;
    private String languageCode;
    private String mimeType;
    private String patientId;
    private String assigningAuthorityId; //Assigning authorityID
    private String repositoryUniqueId;
    private String sourcePatientId; //TODOCHECK
    private String documentTitle;

    private String uniqueId;
    private String practiceSettingCode;
    private String hash;
    private Integer size;

    public String getContentTypeCode() {
        return contentTypeCode;
    }

    public void setContentTypeCode(String contentTypeCode) {
        this.contentTypeCode = contentTypeCode;
    }

    public String getContentTypeCodeLocalized() {
        return contentTypeCodeLocalized;
    }

    public void setContentTypeCodeLocalized(String contentTypeCodeLocalized) {
        this.contentTypeCodeLocalized = contentTypeCodeLocalized;
    }

    public String getConfidentiallyCode() {
        return confidentiallyCode;
    }

    public void setConfidentiallyCode(String confidentiallyCode) {
        this.confidentiallyCode = confidentiallyCode;
    }

    public String getConfidentiallyCodeLocalized() {
        return confidentiallyCodeLocalized;
    }

    public void setConfidentiallyCodeLocalized(String confidentiallyCodeLocalized) {
        this.confidentiallyCodeLocalized = confidentiallyCodeLocalized;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public String getDocumentUUID() {
        return documentUUID;
    }

    public void setDocumentUUID(String documentUUID) {
        this.documentUUID = documentUUID;
    }

    public String getFormatCode() {
        return formatCode;
    }

    public void setFormatCode(String formatCode) {
        this.formatCode = formatCode;
    }

    public String getFormatCodeLocalized() {
        return formatCodeLocalized;
    }

    public void setFormatCodeLocalized(String formatCodeLocalized) {
        this.formatCodeLocalized = formatCodeLocalized;
    }

    public String getHealthcareFacilityTypeCode() {
        return healthcareFacilityTypeCode;
    }

    public void setHealthcareFacilityTypeCode(String healthcareFacilityTypeCode) {
        this.healthcareFacilityTypeCode = healthcareFacilityTypeCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getAssigningAuthorityId() {
        return assigningAuthorityId;
    }

    public void setAssigningAuthorityId(String assigningAuthorityId) {
        this.assigningAuthorityId = assigningAuthorityId;
    }

    public String getRepositoryUniqueId() {
        return repositoryUniqueId;
    }

    public void setRepositoryUniqueId(String repositoryUniqueId) {
        this.repositoryUniqueId = repositoryUniqueId;
    }

    public String getSourcePatientId() {
        return sourcePatientId;
    }

    public void setSourcePatientId(String sourcePatientId) {
        this.sourcePatientId = sourcePatientId;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getPracticeSettingCode() {
        return practiceSettingCode;
    }

    public void setPracticeSettingCode(String practiceSettingCode) {
        this.practiceSettingCode = practiceSettingCode;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }
}
