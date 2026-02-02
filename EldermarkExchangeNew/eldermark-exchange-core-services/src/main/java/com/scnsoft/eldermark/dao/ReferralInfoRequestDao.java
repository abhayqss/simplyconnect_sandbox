package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.referral.ReferralInfoRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralInfoRequestDao extends JpaRepository<ReferralInfoRequest, Long>, JpaSpecificationExecutor<ReferralInfoRequest> {

}
