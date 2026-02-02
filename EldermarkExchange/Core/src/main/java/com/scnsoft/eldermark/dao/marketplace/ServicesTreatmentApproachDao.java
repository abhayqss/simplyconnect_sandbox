package com.scnsoft.eldermark.dao.marketplace;

import com.scnsoft.eldermark.entity.marketplace.ServicesTreatmentApproach;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static org.springframework.data.domain.Sort.Direction.ASC;
/**
 * @author phomal
 */
@Repository
public interface ServicesTreatmentApproachDao extends JpaRepository<ServicesTreatmentApproach, Long> {

    Sort.Order ORDER_BY_DISPLAY_NAME = new Sort.Order(ASC, "displayName");
}
