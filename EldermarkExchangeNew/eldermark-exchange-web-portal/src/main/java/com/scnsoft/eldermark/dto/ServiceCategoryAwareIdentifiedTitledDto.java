package com.scnsoft.eldermark.dto;

import com.scnsoft.eldermark.web.commons.dto.basic.IdentifiedTitledEntityDto;

public class ServiceCategoryAwareIdentifiedTitledDto extends IdentifiedTitledEntityDto {

    private Long serviceCategoryId;
    private String serviceCategoryTitle;

    public ServiceCategoryAwareIdentifiedTitledDto(
            Long id,
            String title,
            Long serviceCategoryId,
            String serviceCategoryTitle
    ) {
        super(id, title);
        this.serviceCategoryId = serviceCategoryId;
        this.serviceCategoryTitle = serviceCategoryTitle;
    }

    public Long getServiceCategoryId() {
        return serviceCategoryId;
    }

    public void setServiceCategoryId(Long serviceCategoryId) {
        this.serviceCategoryId = serviceCategoryId;
    }

    public String getServiceCategoryTitle() {
        return serviceCategoryTitle;
    }

    public void setServiceCategoryTitle(String serviceCategoryTitle) {
        this.serviceCategoryTitle = serviceCategoryTitle;
    }
}
