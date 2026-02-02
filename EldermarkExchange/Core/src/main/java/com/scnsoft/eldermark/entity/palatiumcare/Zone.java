package com.scnsoft.eldermark.entity.palatiumcare;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "NotifyZone")
@Table(name = "PalCare_Zone")
public class Zone extends BasicEntity {

    @Column(name = "zone_name", nullable = false)
    private String name;

    @Column(name = "sound_name", nullable = false)
    private String soundName;

    @Column(name = "sound_count")
    private Integer soundCount;

    @Column(name = "sound_interval")
    private Long soundInterval;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSoundName() {
        return soundName;
    }

    public void setSoundName(String soundName) {
        this.soundName = soundName;
    }

    public Integer getSoundCount() {
        return soundCount;
    }

    public void setSoundCount(Integer soundCount) {
        this.soundCount = soundCount;
    }

    public Long getSoundInterval() {
        return soundInterval;
    }

    public void setSoundInterval(Long soundInterval) {
        this.soundInterval = soundInterval;
    }
}
