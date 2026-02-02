package com.scnsoft.eldermark.entity.palatiumcare;

import com.scnsoft.eldermark.entity.Resident;
import javax.persistence.*;


@Entity
@Table(name = "PalCare_Resident")
// @PrimaryKeyJoinColumn(name = "resident_id")
public class NotifyResident extends BasicEntity /* extends Resident */ {

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "resident_id",  foreignKey = @ForeignKey(name = "FK_NotifyResident_Resident"))
    private Resident resident;

    @Column(name = "pal_care_id", unique = true)
    private Long palCareId;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "location_id", foreignKey = @ForeignKey(name = "FK_NotifyResident_Location"))
    private Location location;

    @ManyToOne
    @JoinColumn(name = "facility_id", foreignKey = @ForeignKey(name = "FK_Facility_NotifyResident"))
    private Facility notifyCommunity;

    public Long getPalCareId() {
        return palCareId;
    }

    public void setPalCareId(Long palCareId) {
        this.palCareId = palCareId;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Facility getNotifyCommunity() {
        return notifyCommunity;
    }

    public void setNotifyCommunity(Facility notifyCommunity) {
        this.notifyCommunity = notifyCommunity;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    @Override
    public String toString() {
        return "NotifyResident{" +
                ", location=" + location +
                '}';
    }

}
