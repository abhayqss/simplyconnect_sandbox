package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientAssessmentCount;
import com.scnsoft.eldermark.beans.projection.AssessmentDataAware;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;
import com.scnsoft.eldermark.beans.reports.model.NorCalComprehensiveAssessmentHouseHoldMembers;
import com.scnsoft.eldermark.beans.reports.model.assessment.EmergencyContactsAware;
import com.scnsoft.eldermark.beans.reports.model.assessment.hmis.HmisAdultChildIntakeAssessment;
import com.scnsoft.eldermark.beans.reports.model.assessment.NorCalComprehensiveAssessment;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientAssessmentResultSecurityAwareEntity;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.service.basic.AuditableEntityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public interface ClientAssessmentResultService extends AuditableEntityService<ClientAssessmentResult>,
        SecurityAwareEntityService<ClientAssessmentResultSecurityAwareEntity, Long>,
        ProjectingService<Long> {

    Long count(PermissionFilter permissionFilter, Long clientId);

    Long count(PermissionFilter permissionFilter);

    List<ClientAssessmentCount> countGroupedByStatus(Long clientId, PermissionFilter permissionFilter);

    List<ClientAssessmentCount> countGroupedByStatus(PermissionFilter permissionFilter);

    Page<ClientAssessmentResult> find(Long clientId, String searchString, PermissionFilter permissionFilter, Pageable pageable);

    Page<ClientAssessmentResult> findHistoryById(Long assessmentResultId, Pageable pageable);

    ClientAssessmentResult findById(Long assessmentResultId);

    void createEventForAssessmentWithRiskIdentified(Long assessmentResultId, Long previousAssessmentResultId);

    void createEventNoteForAssessmentWithRiskIdentified(Long previousAssessmentResultId, Long assessmentResultId);

    Optional<ComprehensiveAssessment> findLatestNotEmptyInProgressOrCompletedComprehensiveByClientIdWithMerged(Long clientId, PermissionFilter permissionFilter, Predicate<ComprehensiveAssessment> notEmptyData);

    Optional<NorCalComprehensiveAssessmentHouseHoldMembers> findLatestNotEmptyInProgressOrCompletedNorCalComprehensiveHouseHoldMembersByClientIdWithMerged(
        Long clientId, PermissionFilter permissionFilter, Predicate<NorCalComprehensiveAssessmentHouseHoldMembers> notEmptyData
    );

    boolean hasPharmacyData(ComprehensiveAssessment assessment);

    <T extends EmergencyContactsAware> boolean hasEmergencyContactData(T assessment);

    boolean hasMedicalContactData(ComprehensiveAssessment assessment);

    boolean existsCreatedByAnyOfClient(Collection<Long> employees, Long clientId);

    Long hide(Long id, String comment);

    Long restore(Long id, String comment);

    boolean hasHouseholdMembersData(NorCalComprehensiveAssessmentHouseHoldMembers assessment);

    Optional<NorCalComprehensiveAssessment> findLatestInProgressOrCompletedNorCalComprehensiveByClientId(Long clientId, PermissionFilter permissionFilter);

    Optional<NorCalComprehensiveAssessment> findLatestNotEmptyInProgressOrCompletedNorCalComprehensiveByClientIdWithMergedAndNotEmptyData(Long clientId, PermissionFilter permissionFilter, Predicate<NorCalComprehensiveAssessment> notEmptyData);

    Optional<HmisAdultChildIntakeAssessment> findHmisAdultChildIntakeAssessmentById(Long parentAssessmentResultId);

    boolean existsInProcess(Long clientId, Long typeId);

    <P> List<P> find(Specification<ClientAssessmentResult> specification, Class<P> projectionClass);
}
