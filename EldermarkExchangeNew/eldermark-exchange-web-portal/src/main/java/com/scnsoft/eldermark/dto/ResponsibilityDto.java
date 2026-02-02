package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.NamedTitledEntityDto;

public class ResponsibilityDto extends NamedTitledEntityDto {

    private boolean assignable;

    public ResponsibilityDto(String name, String title) {
        super(name, title);
    }

    public ResponsibilityDto(String name, String title, boolean assignable) {
        super(name, title);
        this.assignable = assignable;
    }

    public boolean getAssignable() {
        return assignable;
    }

    public void setAssignable(boolean assignable) {
        this.assignable = assignable;
    }

}
