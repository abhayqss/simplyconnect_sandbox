package com.scnsoft.eldermark.web.commons.dto.basic;

import com.scnsoft.eldermark.beans.projection.IdNameAware;

public class IdentifiedNamedViewableEntityDto implements IdNameAware {
    private Long id;
    private String name;
    private boolean canView;

    public IdentifiedNamedViewableEntityDto(Long id, String name, boolean canView) {
        this.id = id;
        this.name = name;
        this.canView = canView;
    }

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

    public boolean getCanView() {
        return canView;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }
}
