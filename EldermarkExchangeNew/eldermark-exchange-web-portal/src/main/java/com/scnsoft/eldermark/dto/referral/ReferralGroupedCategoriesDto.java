package com.scnsoft.eldermark.dto.referral;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedNamedTitledEntityDto;

import java.util.List;

public class ReferralGroupedCategoriesDto {

    private Long id;
    private String groupName;
    private String groupTitle;
    private List<IdentifiedNamedTitledEntityDto> options;

    public ReferralGroupedCategoriesDto(Long id, String groupName, String groupTitle, List<IdentifiedNamedTitledEntityDto> options) {
        this.id = id;
        this.groupName = groupName;
        this.groupTitle = groupTitle;
        this.options = options;
    }

    public ReferralGroupedCategoriesDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupTitle() {
        return groupTitle;
    }

    public void setGroupTitle(String groupTitle) {
        this.groupTitle = groupTitle;
    }

    public List<IdentifiedNamedTitledEntityDto> getOptions() {
        return options;
    }

    public void setOptions(List<IdentifiedNamedTitledEntityDto> options) {
        this.options = options;
    }
}
