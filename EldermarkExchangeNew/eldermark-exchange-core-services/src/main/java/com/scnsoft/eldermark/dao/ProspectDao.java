package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ProspectDeactivationReason;
import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.prospect.Prospect;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface ProspectDao extends AppJpaRepository<Prospect, Long> {
    @Modifying
    @Query("update Prospect p set p.active = true, p.activationDate = :currentDate, p.activationComment = :activationComment where p.id=:prospectId ")
    void activateProspect(
            @Param("prospectId") Long prospectId,
            @Param("activationComment") String activationComment,
            @Param("currentDate") Instant currentDate
    );

    @Modifying
    @Query("update Prospect p set p.active = false, p.deactivationDate = :currentDate, p.deactivationReason = :deactivationReason, p.deactivationComment = :deactivationComment where p.id=:prospectId ")
    void deactivateProspect(
            @Param("prospectId") Long prospectId,
            @Param("deactivationReason") ProspectDeactivationReason deactivationReason,
            @Param("deactivationComment") String deactivationComment,
            @Param("currentDate") Instant currentDate
    );

    boolean existsByIdAndSocialSecurity(Long prospectId, String socialSecurity);

    boolean existsByIdNotAndCommunityIdAndSocialSecurity(Long prospectId, Long communityId, String socialSecurity);

    boolean existsByAndCommunityIdAndSocialSecurity(Long communityId, String socialSecurity);

    @Query("select case when count(pr)> 0 then true else false end from Prospect pr join pr.person p join p.telecoms pt where pt.useCode='EMAIL' and pt.value = :email and pt.organizationId = :organizationId")
    Boolean existsEmailInOrganization(@Param("email") String email, @Param("organizationId") Long organizationId);

    @Query("select case when count(pr)> 0 then true else false end from Prospect pr join pr.person p join p.telecoms pt where pt.useCode='EMAIL' and pt.value = :email and pt.organizationId = :organizationId and pr.id != :id")
    Boolean existsEmailInOrganizationAndIdNot(@Param("email") String email, @Param("organizationId") Long organizationId, @Param("id") Long id);
}
