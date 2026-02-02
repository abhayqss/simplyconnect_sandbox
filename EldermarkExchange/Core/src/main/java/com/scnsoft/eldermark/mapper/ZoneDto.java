package com.scnsoft.eldermark.mapper;

public class ZoneDto {

    private Long id;

    private String name;

    private String soundName;

    private Integer soundCount;

    private Long soundInterval;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
