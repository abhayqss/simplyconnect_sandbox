package com.scnsoft.eldermark.dto;

public class CommunityDeviceTypeDto {
    
    private Long id;
    private String type;
    private String workflow;
    private Long autoCloseIntervalId;
    private String autoCloseIntervalDisplayName;
    private Boolean enabled;
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getWorkflow() {
        return workflow;
    }
    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }
    public Long getAutoCloseIntervalId() {
        return autoCloseIntervalId;
    }
    public void setAutoCloseIntervalId(Long autoCloseIntervalId) {
        this.autoCloseIntervalId = autoCloseIntervalId;
    }
    public String getAutoCloseIntervalDisplayName() {
        return autoCloseIntervalDisplayName;
    }
    public void setAutoCloseIntervalDisplayName(String autoCloseIntervalDisplayName) {
        this.autoCloseIntervalDisplayName = autoCloseIntervalDisplayName;
    }
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public String toString() {
        return "CommunityDeviceTypeDto [id=" + id + ", type=" + type + ", workflow=" + workflow
                + ", autoCloseIntervalId=" + autoCloseIntervalId + ", autoCloseIntervalDisplayName="
                + autoCloseIntervalDisplayName + ", enabled=" + enabled + "]";
    }
    
}
