package com.scnsoft.eldermark.entity.xds.datatype;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "MSG_MessageType")
public class MSGMessageType {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "trigger_event")
    private String triggerEvent;

    @Column(name = "message_structure")
    private String messageStructure;

    public MSGMessageType() {
    }

    public MSGMessageType(String messageType, String triggerEvent, String messageStructure) {
        this.messageType = messageType;
        this.triggerEvent = triggerEvent;
        this.messageStructure = messageStructure;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(String triggerEvent) {
        this.triggerEvent = triggerEvent;
    }

    public String getMessageStructure() {
        return messageStructure;
    }

    public void setMessageStructure(String messageStructure) {
        this.messageStructure = messageStructure;
    }

}
