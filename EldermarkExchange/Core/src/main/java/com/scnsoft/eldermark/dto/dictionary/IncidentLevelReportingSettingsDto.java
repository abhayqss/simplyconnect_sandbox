package com.scnsoft.eldermark.dto.dictionary;

public class IncidentLevelReportingSettingsDto {
    private Long id;
    private Integer level;
    private String timeLines;
    private String requirements;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getLevel() {
        return level;
    }
    public void setLevel(Integer level) {
        this.level = level;
    }
    public String getTimeLines() {
        return timeLines;
    }
    public void setTimeLines(String timeLines) {
        this.timeLines = timeLines;
    }
    public String getRequirements() {
        return requirements;
    }
    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }
    public IncidentLevelReportingSettingsDto(Long id, Integer level, String timeLines, String requirements) {
        this.id = id;
        this.level = level;
        this.timeLines = timeLines;
        this.requirements = requirements;
    }
    
}
