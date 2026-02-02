package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.referral.ReferralIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralIntentDao extends JpaRepository<ReferralIntent, Long> {
}
