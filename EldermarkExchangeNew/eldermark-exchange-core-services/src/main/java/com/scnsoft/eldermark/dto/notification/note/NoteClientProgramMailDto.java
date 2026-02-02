package com.scnsoft.eldermark.dto.notification.note;

public class NoteClientProgramMailDto implements NoteClientProgramViewData {

    private Long typeId;
    private String typeTitle;
    private String serviceProvider;
    private Long startDate;
    private Long endDate;

    @Override
    public Long getTypeId() {
        return typeId;
    }

    @Override
    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    @Override
    public String getTypeTitle() {
        return typeTitle;
    }

    @Override
    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    @Override
    public String getServiceProvider() {
        return serviceProvider;
    }

    @Override
    public void setServiceProvider(String serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    @Override
    public Long getStartDate() {
        return startDate;
    }

    @Override
    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    @Override
    public Long getEndDate() {
        return endDate;
    }

    @Override
    public void setEndDate(Long endDate) {
        this.endDate = endDate;
    }
}
