package com.scnsoft.eldermark.entity.event;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

@Immutable
@Entity
@Table(name = "EventType")
public class EventType implements Serializable {

    private static final long serialVersionUID = 1762706627600836755L;

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic(optional = false)
    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Basic(optional = false)
    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_group_id", nullable = false)
    private EventGroup eventGroup;

    @Column(name = "for_external_use", nullable = false)
    private Boolean forExternalUse;

    @Basic(optional = false)
    @Column(name = "is_service", nullable = false)
    private boolean service;

    @Basic(optional = false)
    @Column(name = "is_require_ir", nullable = false)
    private boolean isRequireIr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EventGroup getEventGroup() {
        return eventGroup;
    }

    public void setEventGroup(EventGroup eventGroup) {
        this.eventGroup = eventGroup;
    }

    public Boolean getForExternalUse() {
        return forExternalUse;
    }

    public Boolean isForExternalUse() {
        return forExternalUse;
    }

    public void setForExternalUse(Boolean forExternalUse) {
        this.forExternalUse = forExternalUse;
    }

    public boolean isService() {
        return service;
    }

    public void setService(boolean service) {
        this.service = service;
    }

    public boolean isRequireIr() {
        return isRequireIr;
    }

    public void setRequireIr(boolean requireIr) {
        isRequireIr = requireIr;
    }
}
