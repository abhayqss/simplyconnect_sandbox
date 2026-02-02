package com.scnsoft.eldermark.dao;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.scnsoft.eldermark.dao.basic.DisplayableNamedEntityDao;
import com.scnsoft.eldermark.entity.ServicesTreatmentApproach;

@Deprecated
@Repository
public interface ServicesTreatmentApproachDao extends DisplayableNamedEntityDao<ServicesTreatmentApproach>, JpaSpecificationExecutor<ServicesTreatmentApproach> {
	List<ServicesTreatmentApproach> findByPrimaryFocusIdIn(List<Long> primaryFocusIds , Sort sort);
    List<ServicesTreatmentApproach> findByOrderByDisplayNameAsc();
    
}
