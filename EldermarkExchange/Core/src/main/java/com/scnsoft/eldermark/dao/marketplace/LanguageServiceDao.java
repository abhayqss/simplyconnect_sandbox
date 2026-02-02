package com.scnsoft.eldermark.dao.marketplace;

import com.scnsoft.eldermark.entity.marketplace.LanguageService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import static org.springframework.data.domain.Sort.Direction.ASC;

/**
 * @author phomal
 */
@Repository
public interface LanguageServiceDao extends JpaRepository<LanguageService, Long> {

    Sort.Order ORDER_BY_DISPLAY_NAME = new Sort.Order(ASC, "displayName");

}
