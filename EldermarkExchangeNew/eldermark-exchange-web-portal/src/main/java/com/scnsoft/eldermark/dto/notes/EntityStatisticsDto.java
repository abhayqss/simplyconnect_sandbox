package com.scnsoft.eldermark.dto.notes;

public class EntityStatisticsDto {

    private String encounterType;

    private Long count;

    public String getEncounterType() {
        return encounterType;
    }

    public void setEncounterType(String encounterType) {
        this.encounterType = encounterType;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
