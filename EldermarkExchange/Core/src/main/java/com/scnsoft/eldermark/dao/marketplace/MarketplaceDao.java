package com.scnsoft.eldermark.dao.marketplace;

import com.scnsoft.eldermark.entity.marketplace.Marketplace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author mradzivonenka
 * @author phomal
 */
@Repository
public interface MarketplaceDao extends JpaRepository<Marketplace, Long> {
    Marketplace getOneByDatabaseIdAndOrganizationIsNull(Long id);
    Marketplace getOneByOrganizationId(Long id);

    void deleteOneByDatabaseIdAndOrganizationIsNull(Long id);
    void deleteOneByOrganizationId(Long id);

}
