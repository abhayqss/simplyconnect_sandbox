package com.scnsoft.eldermark.dao.marketplace;

import com.scnsoft.eldermark.entity.marketplace.AgeGroup;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * @author phomal
 */
@Repository
public interface AgeGroupDao extends JpaRepository<AgeGroup, Long> {

    Sort.Order ORDER_BY_DISPLAY_NAME = new Sort.Order(ASC, "displayName");
    Sort.Order ORDER_BY_DISPLAY_ORDER = new Sort.Order(ASC, "displayOrder");

}
