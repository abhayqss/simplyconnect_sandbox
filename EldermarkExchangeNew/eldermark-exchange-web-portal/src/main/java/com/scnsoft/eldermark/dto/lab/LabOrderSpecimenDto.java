package com.scnsoft.eldermark.dto.lab;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


public class LabOrderSpecimenDto {
    @NotEmpty
    private List<IdentifiedNamedTitledEntityDto> types;
    private String collectorName;
    @NotEmpty
    private String site;
    @NotNull
    private Long date;

    public List<IdentifiedNamedTitledEntityDto> getTypes() {
        return types;
    }

    public void setTypes(List<IdentifiedNamedTitledEntityDto> types) {
        this.types = types;
    }

    public String getCollectorName() {
        return collectorName;
    }

    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Long getDate() {
        return date;
    }

    public void setDate(Long date) {
        this.date = date;
    }
}
