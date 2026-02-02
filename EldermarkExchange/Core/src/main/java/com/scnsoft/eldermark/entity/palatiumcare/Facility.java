package com.scnsoft.eldermark.entity.palatiumcare;

import com.scnsoft.eldermark.entity.Organization;

import javax.persistence.*;
import java.util.List;

@Entity(name = "NotifyFacility")
@Table(name = "PalCare_Facility")
// @PrimaryKeyJoinColumn(name = "community_id")
@NamedQueries({
        @NamedQuery(
                name="NotifyFacility.findFacilityByName",
                query="select facility from NotifyFacility facility where facility_name = :facilityName"
        )
})
public class Facility /*extends Organization */ extends BasicEntity {

    @Column(name = "facility_name", nullable = false, unique = true)
    private String name;

    @Column(name = "facility_label", nullable = false, unique = true)
    private String label;

    @Column(name = "pal_care_id")
    private Long palCareId;

    @OneToMany(mappedBy = "notifyCommunity", fetch = FetchType.LAZY)
    private List<NotifyResident> notifyResidentList;

   /* @OneToMany(fetch = FetchType.LAZY, mappedBy = "facility")
    private List<MobileDevice> mobileDeviceList; */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<NotifyResident> getNotifyResidentList() {
        return notifyResidentList;
    }

    public void setNotifyResidentList(List<NotifyResident> notifyResidentList) {
        this.notifyResidentList = notifyResidentList;
    }

    /* public List<MobileDevice> getMobileDeviceList() {
        return mobileDeviceList;
    }

    public void setMobileDeviceList(List<MobileDevice> mobileDeviceList) {
        this.mobileDeviceList = mobileDeviceList;
    } */

    public Long getPalCareId() {
        return palCareId;
    }

    public void setPalCareId(Long palCareId) {
        this.palCareId = palCareId;
    }

    @Override
    public String toString() {
        return "Facility{" +
                "name='" + name + '\'' +
                // ", mobileDeviceList=" + mobileDeviceList +
                '}';
    }
}
