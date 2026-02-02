package com.scnsoft.eldermark.shared.palatiumcare;

import com.scnsoft.eldermark.shared.palatiumcare.resident.NotifyResidentDto;

import java.util.List;

public class FacilityDto {

    private Long id;

    private String name;

    private String label;

    private List<NotifyResidentDto> notifyResidentList;

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

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<NotifyResidentDto> getNotifyResidentList() {
        return notifyResidentList;
    }

    public void setNotifyResidentList(List<NotifyResidentDto> notifyResidentList) {
        this.notifyResidentList = notifyResidentList;
    }
}
