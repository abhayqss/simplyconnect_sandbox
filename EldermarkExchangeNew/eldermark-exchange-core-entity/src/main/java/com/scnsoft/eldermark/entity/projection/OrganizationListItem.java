package com.scnsoft.eldermark.entity.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface OrganizationListItem {
    Long getId();
    String getName();
    Date getLastModified();
    Boolean getCreatedAutomatically();

    
    @Value("#{(target.databaseOrgCountEntity != null ? target.databaseOrgCountEntity.orgHieCount : 0)}")
    Long getOrgCount();

    @Value("#{(target.databaseOrgCountEntity != null ? target.databaseOrgCountEntity.affiliatedOrgCount : 0)}")
    Long getAffiliatedCount();
    
    
    
}
