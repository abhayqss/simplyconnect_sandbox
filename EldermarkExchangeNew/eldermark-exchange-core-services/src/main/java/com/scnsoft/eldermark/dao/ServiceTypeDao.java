package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.DisplayableNamedEntityDao;
import com.scnsoft.eldermark.entity.marketplace.ServiceType;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceTypeDao extends DisplayableNamedEntityDao<ServiceType>, JpaSpecificationExecutor<ServiceType> {
}
