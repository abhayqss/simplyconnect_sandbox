package com.scnsoft.eldermark.dao;

import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.dao.basic.AppJpaRepository;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyObtainedBy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Set;


@Repository
public interface ClientDao extends AppJpaRepository<Client, Long>, CustomClientDao {

    boolean existsByIdNotAndCommunityIdAndMemberNumber(Long id, Long communityId, String memberNumber);

    boolean existsByAndCommunityIdAndMemberNumber(Long communityId, String memberNumber);

    boolean existsByIdNotAndCommunityIdAndMedicaidNumber(Long id, Long communityId, String medicaidNumber);

    boolean existsByCommunityIdAndMedicaidNumber(Long communityId, String medicaidNumber);

    boolean existsByIdNotAndCommunityIdAndMedicareNumber(Long id, Long communityId, String medicareNumber);

    boolean existsByAndCommunityIdAndMedicareNumber(Long communityId, String medicareNumber);

    boolean existsByAndCommunityId(Long communityId);

    boolean existsByAndCommunityIdAndSocialSecurity(Long communityId, String socialSecurity);

    boolean existsByIdNotAndCommunityIdAndSocialSecurity(Long id, Long communityId, String socialSecurity);

    boolean existsByIdAndSocialSecurity(Long id, String socialSecurity);

    Page<Client> findByOrganization_IdAndCommunity_IdIn(Long organizationId, List<Long> communityId, Pageable pageable);

    Page<Client> findByOrganization_Id(Long organizationId, Pageable pageable);

    @Query("Select c.organizationId from Client c where c.id=:clientId")
    Long findClientOrganizationId(@Param("clientId") Long clientId);

    @Query("select case when count(c)> 0 then true else false end from Client c join c.person p join p.telecoms pt where pt.useCode='EMAIL' and pt.value = :email and pt.organizationId = :organizationId")
    Boolean existsEmailInOrganization(@Param("email") String email, @Param("organizationId") Long organizationId);

    @Query("select case when count(c)> 0 then true else false end from Client c join c.person p join p.telecoms pt where pt.useCode='EMAIL' and pt.value = :email and pt.organizationId = :organizationId and c.id != :id")
    Boolean existsEmailInOrganizationAndIdNot(@Param("email") String email, @Param("organizationId") Long organizationId, @Param("id") Long id);

    @Modifying
    @Query("update Client c set c.active = case c.active when true then false else true end where c.id=:id ")
    void toggleStatus(@Param("id") Long id);

    @Query("Select c.createdById from Client c where c.createdById in :employeeIds and c.communityId = :communityId")
    List<Long> findCreatorIdsAmongEmployeeIdsAndCommunityId(@Param("employeeIds") Iterable<Long> employeeIds,
                                                            @Param("communityId") Long communityId);

    @Modifying
    @Query("update Client c set c.active = true, c.programType = :programType, c.intakeDate = :intakeDate, c.activationDate = :currentDate, c.comment = :comment where c.id=:clientId ")
    void activateClient(
            @Param("clientId") Long clientId,
            @Param("intakeDate") Instant intakeDate,
            @Param("programType") String programType,
            @Param("comment") String comment,
            @Param("currentDate") Instant currentDate
    );

    @Modifying
    @Query("update Client c set c.active = true, c.intakeDate = :currentDate, c.activationDate = :currentDate where c.id=:clientId ")
    void activateClient(
            @Param("clientId") Long clientId,
            @Param("currentDate") Instant currentDate
    );

    @Modifying
    @Query("update Client c set c.active = false, c.exitDate = :exitDate, c.deactivationReason = :deactivationReason, c.deactivationDate = :currentDate, c.exitComment = :comment where c.id=:clientId ")
    void deactivateClient(
            @Param("clientId") Long clientId,
            @Param("exitDate") Instant exitDate,
            @Param("deactivationReason") ClientDeactivationReason deactivationReason,
            @Param("comment") String comment,
            @Param("currentDate") Instant currentDate
    );

    @Modifying
    @Query("update Client c set c.active = false, c.exitDate = :currentDate, c.deactivationDate = :currentDate where c.id=:clientId ")
    void deactivateClient(
            @Param("clientId") Long clientId,
            @Param("currentDate") Instant currentDate
    );

    @Modifying
    @Query("update Client c set c.pccPatientId = :pccPatientId where c.id=:clientId")
    void updatePccPatientId(
            @Param("clientId") Long clientId,
            @Param("pccPatientId") Long pccPatientId
    );

    List<Client> findAllByIdIn(Set<Long> ids);

    @Modifying
    @Query("update Client c set c.primaryContact = null where c.primaryContactId=:primaryContactId")
    void deletePrimaryContact(@Param("primaryContactId") Long primaryContactId);

    @Modifying
    @Query("update Client c set c.hieConsentPolicyType = :policy," +
            " c.hieConsentPolicySource = :source," +
            " c.hieConsentPolicyObtainedBy = :obtainedBy," +
            " c.hieConsentPolicyObtainedFrom = :obtainedFrom," +
            " c.hieConsentPolicyUpdateDateTime = :updateDateTime" +
            " where c.communityId = :communityId and c.hieConsentPolicyUpdatedByEmployeeId is null")
    void updateHieConsentPolicyByCommunityIdAndUpdatedByIdIsNull(
            @Param("policy") HieConsentPolicyType policy,
            @Param("source") HieConsentPolicySource source,
            @Param("obtainedBy") HieConsentPolicyObtainedBy obtainedBy,
            @Param("obtainedFrom") String obtainedFrom,
            @Param("updateDateTime") Instant updateDateTime,
            @Param("communityId") Long communityId
    );

    <T> List<T> findAllByCommunityId(Long communityId, Class<T> projectionClass);
}
