package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.beans.security.projection.entity.AssessmentSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.CommunitySecurityAwareEntity;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.community.Community;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AssessmentService extends SecurityAwareEntityService<AssessmentSecurityAwareEntity, Long> {

    Optional<Assessment> findById(Long assessmentId);

    Assessment findByShortName(String shortName);

    String findSurveyJson(Long assessmentId);

    List<Assessment> findTypesAllowedInCommunity(Community clientCommunity, List<String> filterBy);

    List<Assessment> findTypesAllowedInAnyCommunities(Collection<? extends OrganizationIdAware> clientCommunities, List<String> filterBy);

    List<Assessment> findTypesExistingForAnyClient(Collection<Long> clientIds, List<String> filterBy);

    boolean isTypeAllowedForCommunity(OrganizationIdAware clientCommunity, Long assessmentId);

    boolean isTypeAllowedForCommunity(OrganizationIdAware clientCommunity, Assessment assessment);

    <T extends CommunitySecurityAwareEntity> boolean isTypeAllowedForAnyCommunity(Collection<T> clientCommunities, Long assessmentId);

    <T extends CommunitySecurityAwareEntity> boolean isTypeExistsForAnyClient(Collection<Long> clients, Long assessmentId);

    boolean existTypesAllowedInCommunity(OrganizationIdAware clientCommunity);

    boolean isTypeAllowedInOrganization(String assessmentShortName, Long organizationId);

    void setTypeAllowedInOrganization(String assessmentShortName, Long organizationId, boolean isAllowed);

    List<Long> findOrganizationIdsWithEnabledAssessment(String shortName);

}
