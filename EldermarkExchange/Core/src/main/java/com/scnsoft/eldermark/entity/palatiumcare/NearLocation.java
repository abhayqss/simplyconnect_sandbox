package com.scnsoft.eldermark.entity.palatiumcare;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "NotifyNearLocation")
@Table(name = "PalCare_NearLocation")
public class NearLocation  extends BasicEntity  {

    @Column(name = "location", nullable = false)
    private String location;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
