package com.scnsoft.eldermark.dto.notification.note;

public interface NoteClientProgramViewData {

    Long getTypeId();

    void setTypeId(Long typeId);

    String getTypeTitle();

    void setTypeTitle(String typeTitle);

    String getServiceProvider();

    void setServiceProvider(String serviceProvider);

    Long getStartDate();

    void setStartDate(Long startDate);

    Long getEndDate();

    void setEndDate(Long endDate);
}
