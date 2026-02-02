package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.referral.ReferralRequestResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralRequestResponseDao extends JpaRepository<ReferralRequestResponse, Long>, JpaSpecificationExecutor<ReferralRequestResponse> {
}
