package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.referral.ReferralDeclineReason;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralDeclineReasonDao extends JpaRepository<ReferralDeclineReason, Long> {
}
