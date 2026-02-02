package com.scnsoft.eldermark.mobile.dto.document;

public class DocumentDto extends BaseDocumentDto {

    private Integer size;

    private String clientFirstName;
    private String clientLastName;

    private Long communityId;
    private String communityName;

    private String description;
    private String author;

    private Long assignedDate;
    private String assignedBy;

    private DocumentSignatureDto signature;

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public void setClientFirstName(String clientFirstName) {
        this.clientFirstName = clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }

    public void setClientLastName(String clientLastName) {
        this.clientLastName = clientLastName;
    }

    public Long getCommunityId() {
        return communityId;
    }

    public void setCommunityId(Long communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Long getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(Long assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public DocumentSignatureDto getSignature() {
        return signature;
    }

    public void setSignature(DocumentSignatureDto signature) {
        this.signature = signature;
    }
}
