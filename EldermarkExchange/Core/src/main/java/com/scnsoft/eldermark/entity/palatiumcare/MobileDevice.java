package com.scnsoft.eldermark.entity.palatiumcare;

import javax.persistence.*;

@Entity
@Table(name = "PalCare_MobileDevice")
@NamedQueries({
        @NamedQuery(
                name="MobileDevice.activateDevice",
                query="update MobileDevice set is_active = true, device_status = 'ACTIVE'  where id = :deviceId"
        ),
        @NamedQuery(
                name="MobileDevice.deactivateDevice",
                query="update MobileDevice set is_active = false, device_status = 'NOT_CONFIRMED_YET'  where id = :deviceId"
        ),
        @NamedQuery(
                name="MobileDevice.findByDeviceIdentifier",
                query="select device from MobileDevice device where device_identifier = :deviceUID"
        )
})
public class MobileDevice extends BasicEntity {

    @Column(name = "device_identifier", unique = true)
    private String deviceIdentifier; //the same as UID

    @Column(name = "is_active")
    private Boolean isActive;

    public enum DeviceOS { ANDROID, IOS }

    @Transient
    // @Enumerated
    private DeviceOS deviceOS;

    public enum DeviceStatus { CONFIRMATION_NOT_REQUESTED, NOT_CONFIRMED_YET, ACTIVE, BLOCKED, ACTIVATION_EXPIRED, CONFIRMATION_DECLINED, AWAITING_CONFIRMATION }

    @Enumerated(EnumType.STRING)
    @Column(name = "device_status")
    private DeviceStatus deviceStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "facility_id" /*, foreignKey = @ForeignKey(name = "facility_mobile_device_fk"), nullable = false*/)
    private Facility facility;

    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public DeviceOS getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(DeviceOS deviceOS) {
        this.deviceOS = deviceOS;
    }

    public DeviceStatus getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(DeviceStatus deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @Override
    public String toString() {
        return "MobileDevice{" +
                "deviceIdentifier='" + deviceIdentifier + '\'' +
                ", isActive=" + isActive +
                ", deviceOS=" + deviceOS +
                ", deviceStatus=" + deviceStatus +
                ", facility=" + facility +
                '}';
    }
}
