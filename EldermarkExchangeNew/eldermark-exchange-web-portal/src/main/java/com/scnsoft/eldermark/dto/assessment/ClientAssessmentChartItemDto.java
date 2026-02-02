package com.scnsoft.eldermark.dto.assessment;

import com.scnsoft.eldermark.dto.TypeDto;

public class ClientAssessmentChartItemDto {

    private Long id;

    private String name;

    private TypeDto status;

    private Long dateStarted;

    private Long dateCompleted;

    private Long points;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TypeDto getStatus() {
        return status;
    }

    public Long getDateStarted() {
        return dateStarted;
    }

    public Long getDateCompleted() {
        return dateCompleted;
    }

    public Long getPoints() {
        return points;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(TypeDto status) {
        this.status = status;
    }

    public void setDateStarted(Long dateStarted) {
        this.dateStarted = dateStarted;
    }

    public void setDateCompleted(Long dateCompleted) {
        this.dateCompleted = dateCompleted;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

}
