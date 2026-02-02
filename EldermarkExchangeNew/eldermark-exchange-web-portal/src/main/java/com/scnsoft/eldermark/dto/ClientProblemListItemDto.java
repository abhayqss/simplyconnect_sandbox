package com.scnsoft.eldermark.dto;

public class ClientProblemListItemDto {
    private Long id;
    private String name;
    private Long identifiedDate;
    private Long resolvedDate;
    private String code;
    private String codeSet;

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

    public Long getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(Long resolvedDate) {
        this.resolvedDate = resolvedDate;
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
}
