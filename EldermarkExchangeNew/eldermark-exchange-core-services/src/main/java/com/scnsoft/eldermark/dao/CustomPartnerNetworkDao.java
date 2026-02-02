package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.PartnerNetworkCommunity;
import com.scnsoft.eldermark.beans.PartnerNetworkOrganization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

public interface CustomPartnerNetworkDao {

    Page<PartnerNetworkOrganization> findGroupedByOrganization(Specification<PartnerNetworkCommunity> specification,
                                                               Pageable pageable);
}
