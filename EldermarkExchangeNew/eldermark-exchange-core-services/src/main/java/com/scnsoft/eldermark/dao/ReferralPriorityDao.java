package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.referral.ReferralPriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralPriorityDao extends JpaRepository<ReferralPriority, Long> {
}
