package com.scnsoft.eldermark.dto;

public class ClientProblemDto {
    private Long id;
    private String name;
    private Long identifiedDate;
    private String code;
    private String codeSet;

    private String recordedBy;
    private String type;
    private String status;
    private Long resolvedDate;
    private Long onsetDate;
    private Long recordedDate;
    private Boolean primary;
    private String comments;
    private Integer ageObservationValue;
    private String ageObservationUnit;

    private String organizationName;
    private String communityName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIdentifiedDate() {
        return identifiedDate;
    }

    public void setIdentifiedDate(Long identifiedDate) {
        this.identifiedDate = identifiedDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCodeSet() {
        return codeSet;
    }

    public void setCodeSet(String codeSet) {
        this.codeSet = codeSet;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(Long resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public Long getOnsetDate() {
        return onsetDate;
    }

    public void setOnsetDate(Long onsetDate) {
        this.onsetDate = onsetDate;
    }

    public Long getRecordedDate() {
        return recordedDate;
    }

    public void setRecordedDate(Long recordedDate) {
        this.recordedDate = recordedDate;
    }

    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getAgeObservationValue() {
        return ageObservationValue;
    }

    public void setAgeObservationValue(Integer ageObservationValue) {
        this.ageObservationValue = ageObservationValue;
    }

    public String getAgeObservationUnit() {
        return ageObservationUnit;
    }

    public void setAgeObservationUnit(String ageObservationUnit) {
        this.ageObservationUnit = ageObservationUnit;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }
}