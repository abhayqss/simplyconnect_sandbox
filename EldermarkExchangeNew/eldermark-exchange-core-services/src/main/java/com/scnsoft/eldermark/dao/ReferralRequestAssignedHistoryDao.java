package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.entity.referral.ReferralRequestAssignedHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralRequestAssignedHistoryDao extends JpaRepository<ReferralRequestAssignedHistory, ReferralRequestAssignedHistory.Id> {
}
