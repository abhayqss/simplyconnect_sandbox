package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.dao.basic.DisplayableNamedEntityDao;
import com.scnsoft.eldermark.entity.marketplace.ServiceCategory;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceCategoryDao extends DisplayableNamedEntityDao<ServiceCategory>, AppJpaRepository<ServiceCategory, Long> {
}
