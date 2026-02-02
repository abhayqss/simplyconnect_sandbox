package com.scnsoft.eldermark.dto;

public class MedicationListItemDto {
    private Long id;
    private String name;
    private String directions;
    private Long startedDate;
    private Long stoppedDate;

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

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public Long getStartedDate() {
        return startedDate;
    }

    public void setStartedDate(Long startedDate) {
        this.startedDate = startedDate;
    }

    public Long getStoppedDate() {
        return stoppedDate;
    }

    public void setStoppedDate(Long stoppedDate) {
        this.stoppedDate = stoppedDate;
    }
}
