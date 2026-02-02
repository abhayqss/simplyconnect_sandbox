package com.scnsoft.eldermark.entity.palatiumcare;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity(name = "NotifyEvent")
@Table(name = "PalCare_Event")
public class PCEvent extends BasicEntity  {

    @Transient
    // @Column(name = "type_id", nullable = false)
    private Integer typeId;

    @Column(name = "pal_care_id")
    private String palCareId;

    @Column(name = "type_name")
    private String type;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "contact_id", foreignKey = @ForeignKey(name = "FK_Event_Contact"))
    private Contact contact;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "resident_id", foreignKey = @ForeignKey(name = "FK_Event_Resident"))
    private NotifyResident resident;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "FK_Event_Device"))
    private DeviceType deviceType;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "near_location_id", foreignKey = @ForeignKey(name = "FK_Event_Location"))
    private Location nearLocation;

    @Column(name = "text")
    private String text;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    @JoinTable(name = "PalCare_Event_CptCode",
            joinColumns = @JoinColumn(
                    name = "event_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_Event_CPT_Code")),
            inverseJoinColumns = @JoinColumn(
                    name = "cpt_code_id",
                    referencedColumnName = "id",
                    foreignKey = @ForeignKey(name = "FK_CPT_Code_Event")))
    private List<CptCode> cptCodeList;

    @Column(name = "event_date_time")
    private Date eventDateTime;

    @Column(name = "ack_date_time")
    private String ackDateTime;

    @Column(name = "version")
    private String version;

    public String getPalCareId() {
        return palCareId;
    }

    public void setPalCareId(String palCareId) {
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

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public NotifyResident getResident() {
        return resident;
    }

    public void setResident(NotifyResident resident) {
        this.resident = resident;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Location getNearLocation() {
        return nearLocation;
    }

    public void setNearLocation(Location nearLocation) {
        this.nearLocation = nearLocation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<CptCode> getCptCodeList() {
        return cptCodeList;
    }

    public void setCptCodeList(List<CptCode> cptCodeList) {
        this.cptCodeList = cptCodeList;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "PCEvent{" +
                "typeId=" + typeId +
                ", palCareId='" + palCareId + '\'' +
                ", type='" + type + '\'' +
                ", contact=" + contact +
                ", resident=" + resident +
                ", deviceType=" + deviceType +
                ", nearLocation=" + nearLocation +
                ", text='" + text + '\'' +
                ", cptCodeList=" + cptCodeList +
                ", eventDateTime=" + eventDateTime +
                ", ackDateTime='" + ackDateTime + '\'' +
                ", version='" + version + '\'' +
                '}';
    }
}
