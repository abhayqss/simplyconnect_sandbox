package com.scnsoft.eldermark.dto.notes;

import com.scnsoft.eldermark.dto.notification.note.NoteClientProgramViewData;
import com.scnsoft.eldermark.validation.SpELAssert;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@SpELAssert(
        applyIf = "#nonNull(startDate) && #nonNull(endDate)",
        value = "#compare(startDate, endDate) <= 0",
        message = "End date should not be less than start date",
        helpers = {Objects.class, Long.class}
)
public class ClientProgramDto implements NoteClientProgramViewData {
    @NotNull
    private Long typeId;
    private String typeTitle;
    @NotEmpty
    @Size(max = 256)
    private String serviceProvider;
    @NotNull
    private Long startDate;
    @NotNull
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
