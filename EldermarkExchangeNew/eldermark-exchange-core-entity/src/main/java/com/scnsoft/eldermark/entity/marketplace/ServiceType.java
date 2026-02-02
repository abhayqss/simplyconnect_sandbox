package com.scnsoft.eldermark.entity.marketplace;

import com.scnsoft.eldermark.entity.basic.DisplayableNamedKeyEntity;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Immutable
@Entity
public class ServiceType extends DisplayableNamedKeyEntity {

    @Column(name = "service_category_id", insertable = false, updatable = false, nullable = false)
    private Long serviceCategoryId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "service_category_id")
    private ServiceCategory serviceCategory;

    @Column(name = "is_client_related")
    private boolean isClientRelated;

    @Column(name = "is_business_related")
    private boolean isBusinessRelated;

    @Column(name = "can_additional_clinical_info_be_shared")
    private boolean canAdditionalClinicalInfoBeShared;

    public Long getServiceCategoryId() {
        return serviceCategoryId;
    }

    public void setServiceCategoryId(Long serviceCategoryId) {
        this.serviceCategoryId = serviceCategoryId;
    }

    public ServiceCategory getServiceCategory() {
        return serviceCategory;
    }

    public void setServiceCategory(ServiceCategory serviceCategory) {
        this.serviceCategory = serviceCategory;
    }

    public boolean getIsClientRelated() {
        return isClientRelated;
    }

    public void setIsClientRelated(boolean isClientRelated) {
        this.isClientRelated = isClientRelated;
    }

    public boolean getIsBusinessRelated() {
        return isBusinessRelated;
    }

    public void setIsBusinessRelated(boolean businessRelated) {
        isBusinessRelated = businessRelated;
    }

    public boolean getCanAdditionalClinicalInfoBeShared() {
        return canAdditionalClinicalInfoBeShared;
    }

    public void setCanAdditionalClinicalInfoBeShared(boolean canAdditionalClinicalInfoBeShared) {
        this.canAdditionalClinicalInfoBeShared = canAdditionalClinicalInfoBeShared;
    }
}
