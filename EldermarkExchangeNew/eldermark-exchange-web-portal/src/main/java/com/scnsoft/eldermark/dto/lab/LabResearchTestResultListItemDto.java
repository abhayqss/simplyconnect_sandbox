package com.scnsoft.eldermark.dto.lab;

public class LabResearchTestResultListItemDto {
    private Long id;
    private String name;
    private String value;
    private String units;
    private String refRange;
    private String abnormalFlags;

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public String getRefRange() {
        return refRange;
    }

    public void setRefRange(String refRange) {
        this.refRange = refRange;
    }

    public String getAbnormalFlags() {
        return abnormalFlags;
    }

    public void setAbnormalFlags(String abnormalFlags) {
        this.abnormalFlags = abnormalFlags;
    }
}