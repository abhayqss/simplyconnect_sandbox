package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.referral.Referral;
import com.scnsoft.eldermark.entity.referral.ReferralRequest;
import com.scnsoft.eldermark.entity.referral.ReferralResponse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReferralRequestDao extends
        AppJpaRepository<ReferralRequest, Long> {

    @Query("select case when count(r)>0 then true else false end from ReferralRequest r left join r.lastResponse lr where r.referral=:referral and (lr is null or lr.response != :response)")
    boolean existsByReferralAndResponseNot(@Param("referral") Referral referral, @Param("response") ReferralResponse referralResponse);

}
