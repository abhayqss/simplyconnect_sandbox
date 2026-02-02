package com.scnsoft.dto.incoming;

import com.scnsoft.eldermark.shared.palatiumcare.location.NotifyLocationDto;

import java.util.List;

public class PalCareEventDto {

    private Long id;

    private Long palCareId;

    private Integer typeId;

    private String type;

    private PalCareContactDto contact;

    private Long residentId;

    private NotifyLocationDto location;

    private PalCareDeviceTypeDto device;

    private List<NotifyLocationDto> nearLocationList;

    private String text;

    private List<PalCareCptCodeDto> cptCodeList;

    private String eventDateTime;

    private String ackDateTime;

    private String version;

    private List<PalCareActionDto> actionList;

    private String actionUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPalCareId() {
        return palCareId;
    }

    public void setPalCareId(Long palCareId) {
        this.palCareId = palCareId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PalCareContactDto getContact() {
        return contact;
    }

    public void setContact(PalCareContactDto contact) {
        this.contact = contact;
    }

    public Long getResidentId() {
        return residentId;
    }

    public void setResidentId(Long residentId) {
        this.residentId = residentId;
    }

    public NotifyLocationDto getLocation() {
        return location;
    }

    public void setLocation(NotifyLocationDto location) {
        this.location = location;
    }

    public PalCareDeviceTypeDto getDevice() {
        return device;
    }

    public void setDevice(PalCareDeviceTypeDto device) {
        this.device = device;
    }

    public List<NotifyLocationDto> getNearLocationList() {
        return nearLocationList;
    }

    public void setNearLocationList(List<NotifyLocationDto> nearLocationList) {
        this.nearLocationList = nearLocationList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<PalCareCptCodeDto> getCptCodeList() {
        return cptCodeList;
    }

    public void setCptCodeList(List<PalCareCptCodeDto> cptCodeList) {
        this.cptCodeList = cptCodeList;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getAckDateTime() {
        return ackDateTime;
    }

    public void setAckDateTime(String ackDateTime) {
        this.ackDateTime = ackDateTime;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<PalCareActionDto> getActionList() {
        return actionList;
    }

    public void setActionList(List<PalCareActionDto> actionList) {
        this.actionList = actionList;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    @Override
    public String toString() {
        return "PalCareEventDto{" +
                "id=" + id +
                ", typeId=" + typeId +
                ", type='" + type + '\'' +
                ", contact=" + contact +
                ", residentId=" + residentId +
                ", location=" + location +
                ", device=" + device +
                ", nearLocationList=" + nearLocationList +
                ", text='" + text + '\'' +
                ", cptCodeList=" + cptCodeList +
                ", eventDateTime='" + eventDateTime + '\'' +
                ", ackDateTime='" + ackDateTime + '\'' +
                ", version='" + version + '\'' +
                ", actionList=" + actionList +
                ", actionUrl='" + actionUrl + '\'' +
                '}';
    }
}
