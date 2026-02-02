package com.scnsoft.eldermark.shared.carecoordination.community;

import com.scnsoft.eldermark.entity.BasicEntity;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;

import java.util.List;

public class CommunityFilterDto extends BasicEntity {
    private String name;
    private Boolean isInactive;

    public CommunityFilterDto() {
    }

    public CommunityFilterDto(String name, Boolean isInactive) {
        this.name = name;
        this.isInactive = isInactive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsInactive() {
        return isInactive;
    }

    public void setIsInactive(Boolean isInactive) {
        this.isInactive = isInactive;
    }
}
