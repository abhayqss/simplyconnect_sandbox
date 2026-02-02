package com.scnsoft.eldermark.shared.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.AlertStatus;
import com.scnsoft.eldermark.entity.palatiumcare.AlertType;
import com.scnsoft.eldermark.mapper.UserMobileDto;
import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;

import java.util.Date;

public class AlertDto  {

    private Long id;

    private UserMobileDto responder;

    private ResidentDto resident;

    private DeviceTypeDto device;

    private NotifyLocationDto nearLocation;

    private String text;

    private Date eventDateTime;

    private String ackDateTime;

    private AlertStatus status;

    private AlertType alertType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DeviceTypeDto getDevice() {
        return device;
    }

    public void setDevice(DeviceTypeDto device) {
        this.device = device;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(Date eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getAckDateTime() {
        return ackDateTime;
    }

    public void setAckDateTime(String ackDateTime) {
        this.ackDateTime = ackDateTime;
    }

    public UserMobileDto getResponder() {
        return responder;
    }

    public void setResponder(UserMobileDto responder) {
        this.responder = responder;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public void setStatus(AlertStatus status) {
        this.status = status;
    }

    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(AlertType alertType) {
        this.alertType = alertType;
    }

    public ResidentDto getResident() {
        return resident;
    }

    public void setResident(ResidentDto resident) {
        this.resident = resident;
    }

    public NotifyLocationDto getNearLocation() {
        return nearLocation;
    }

    public void setNearLocation(NotifyLocationDto nearLocation) {
        this.nearLocation = nearLocation;
    }

    @Override
    public String toString() {
        return "AlertDto{" +
                "id=" + id +
                ", responder=" + responder +
                ", resident=" + resident +
                ", device=" + device +
                ", nearLocation=" + nearLocation +
                ", text='" + text + '\'' +
                ", eventDateTime='" + eventDateTime + '\'' +
                ", ackDateTime='" + ackDateTime + '\'' +
                ", status=" + status +
                '}';
    }
}
