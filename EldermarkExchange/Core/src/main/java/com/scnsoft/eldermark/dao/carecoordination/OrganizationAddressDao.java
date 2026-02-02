package com.scnsoft.eldermark.dao.carecoordination;

import com.scnsoft.eldermark.entity.OrganizationAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by averazub on 3/21/2016.
 */
@Repository
public interface OrganizationAddressDao extends JpaRepository<OrganizationAddress, Long>, JpaSpecificationExecutor<OrganizationAddress> {
    @Query("select a from OrganizationAddress a where a.locationUpToDate is null or locationUpToDate = false")
    List<OrganizationAddress> findWithNotUptodateLocation();
}
