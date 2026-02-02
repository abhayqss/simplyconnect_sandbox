package com.scnsoft.eldermark.beans.reports.model;

import java.util.List;
import java.util.Map;

public class PhoneCallTimeReport extends Report {

    private List<EncounterNoteFirstTab> firstTabList;

    private List<EncounterNoteSecondTab> secondTabList;

    private Map<String, List<TotalClientsTab>> totalClientsTabList;

    private Map<String, List<TotalServiceCoordinatorsTab>> totalServiceCoordinatorsTabList;

    public List<EncounterNoteFirstTab> getFirstTabList() {
        return firstTabList;
    }

    public void setFirstTabList(List<EncounterNoteFirstTab> firstTabList) {
        this.firstTabList = firstTabList;
    }

    public List<EncounterNoteSecondTab> getSecondTabList() {
        return secondTabList;
    }

    public void setSecondTabList(List<EncounterNoteSecondTab> secondTabList) {
        this.secondTabList = secondTabList;
    }

    public Map<String, List<TotalClientsTab>> getTotalClientsTabList() {
        return totalClientsTabList;
    }

    public void setTotalClientsTabList(Map<String, List<TotalClientsTab>> totalClientsTabList) {
        this.totalClientsTabList = totalClientsTabList;
    }

    public Map<String, List<TotalServiceCoordinatorsTab>> getTotalServiceCoordinatorsTabList() {
        return totalServiceCoordinatorsTabList;
    }

    public void setTotalServiceCoordinatorsTabList(Map<String, List<TotalServiceCoordinatorsTab>> totalServiceCoordinatorsTabList) {
        this.totalServiceCoordinatorsTabList = totalServiceCoordinatorsTabList;
    }
}
