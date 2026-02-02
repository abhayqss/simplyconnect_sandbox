package com.scnsoft.eldermark.entity.community;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "DeviceType")
public class DeviceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JoinColumn(name = "community_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private Community community;

    @Column(name = "type", nullable = false)
    private String type;

    @JoinColumn(name = "auto_close_interval_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(optional = false)
    private AutoCloseInterval autoCloseInterval;

    @Enumerated(EnumType.STRING)
    @Column(name = "workflow", nullable = false)
    private DeviceTypeWorkflow workflow;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Community getCommunity() {
        return community;
    }

    public void setCommunity(Community community) {
        this.community = community;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AutoCloseInterval getAutoCloseInterval() {
        return autoCloseInterval;
    }

    public void setAutoCloseInterval(AutoCloseInterval autoCloseInterval) {
        this.autoCloseInterval = autoCloseInterval;
    }

    public DeviceTypeWorkflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(DeviceTypeWorkflow workflow) {
        this.workflow = workflow;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
