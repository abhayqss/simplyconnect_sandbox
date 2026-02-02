package com.scnsoft.eldermark.dto.directory;

import com.fasterxml.jackson.annotation.JsonInclude;

public class DirOrganizationListItemDto {
    private Long id;
    private String label;
    private boolean areLabsEnabled;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean hasCommunities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean getAreLabsEnabled() {
        return areLabsEnabled;
    }

    public void setAreLabsEnabled(boolean areLabsEnabled) {
        this.areLabsEnabled = areLabsEnabled;
    }

    public Boolean getHasCommunities() {
        return hasCommunities;
    }

    public void setHasCommunities(Boolean hasCommunities) {
        this.hasCommunities = hasCommunities;
    }
}
