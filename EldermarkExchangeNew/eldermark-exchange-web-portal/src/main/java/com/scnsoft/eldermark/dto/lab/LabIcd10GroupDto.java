package com.scnsoft.eldermark.dto.lab;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;

import java.util.List;

public class LabIcd10GroupDto {

    private Long id;
    private String title;
    private String name;
    private List<IdentifiedNamedTitledEntityDto> codes;

    public LabIcd10GroupDto() {
    }

    public LabIcd10GroupDto(Long id, String title, String name, List<IdentifiedNamedTitledEntityDto> codes) {
        this.id = id;
        this.title = title;
        this.name = name;
        this.codes = codes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IdentifiedNamedTitledEntityDto> getCodes() {
        return codes;
    }

    public void setCodes(List<IdentifiedNamedTitledEntityDto> codes) {
        this.codes = codes;
    }
}
