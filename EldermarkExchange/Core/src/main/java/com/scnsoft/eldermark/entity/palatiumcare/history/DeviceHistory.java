package com.scnsoft.eldermark.entity.palatiumcare.history;

import com.scnsoft.eldermark.entity.palatiumcare.Device;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Entity
@EntityListeners(DeviceEntityListener.class)
public class DeviceHistory extends Auditable<String> {

    @ManyToOne
    @JoinColumn(name = "device_id", foreignKey = @ForeignKey(name = "FK_Device_Device_History"))
    private Device device;

    @Enumerated(STRING)
    private Action action;

    public DeviceHistory(Device device, Action action) {
        this.device = device;
        this.action = action;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
