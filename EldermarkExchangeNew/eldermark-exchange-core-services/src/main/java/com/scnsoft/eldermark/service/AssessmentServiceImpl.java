package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.projection.OrganizationIdAware;
import com.scnsoft.eldermark.beans.security.projection.entity.AssessmentSecurityAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.CommunitySecurityAwareEntity;
import com.scnsoft.eldermark.dao.AssessmentDao;
import com.scnsoft.eldermark.dao.specification.AssessmentSpecificationGenerator;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.assessment.AssessmentSurvey;
import com.scnsoft.eldermark.entity.community.Community;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AssessmentDao assessmentDao;

    @Autowired
    private AssessmentSpecificationGenerator assessmentSpecificationGenerator;

    @Autowired
    private OrganizationService organizationService;

    @Override
    @Transactional(readOnly = true)
    public Optional<Assessment> findById(Long assessmentId) {
        return assessmentDao.findById(assessmentId);
    }

    @Override
    public Assessment findByShortName(String shortName) {
        return assessmentDao.findByShortName(shortName);
    }

    @Override
    @Transactional(readOnly = true)
    public String findSurveyJson(Long assessmentId) {
        var assessmentSurvey = assessmentDao.findById(assessmentId, AssessmentSurvey.class);
        return assessmentSurvey
                .map(AssessmentSurvey::getContent)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assessment> findTypesAllowedInCommunity(Community clientCommunity, List<String> filterBy) {
        return findTypesAllowedInAnyCommunities(Collections.singletonList(clientCommunity), filterBy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assessment> findTypesAllowedInAnyCommunities(Collection<? extends OrganizationIdAware> clientCommunities, List<String> filterBy) {
        var spec = assessmentSpecificationGenerator.typesAllowedInAnyCommunity(clientCommunities);
        if (CollectionUtils.isNotEmpty(filterBy)) {
            spec = spec.and(assessmentSpecificationGenerator.byCodes(filterBy));
        }
        return assessmentDao.findAll(spec);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Assessment> findTypesExistingForAnyClient(Collection<Long> clientIds, List<String> filterBy) {
        var spec = assessmentSpecificationGenerator.typesExistingForClients(clientIds);
        if (CollectionUtils.isNotEmpty(filterBy)) {
            spec = spec.and(assessmentSpecificationGenerator.byCodes(filterBy));
        }
        return assessmentDao.findAll(spec);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTypeAllowedForCommunity(OrganizationIdAware clientCommunity, Long assessmentId) {
        var byId = assessmentSpecificationGenerator.byId(assessmentId);
        var accessibleInCommunity = assessmentSpecificationGenerator.typesAllowedInCommunity(clientCommunity);

        return assessmentDao.exists(byId.and(accessibleInCommunity));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTypeAllowedForCommunity(OrganizationIdAware community, Assessment assessment) {
        if (assessment.getIsShared()) {
            return !assessment.getDisabledOrganizationIds().contains(community.getOrganizationId());
        } else {
            return assessment.getOrganizationIds().contains(community.getOrganizationId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends CommunitySecurityAwareEntity> boolean isTypeAllowedForAnyCommunity(Collection<T> clientCommunities, Long assessmentId) {
        var byId = assessmentSpecificationGenerator.byId(assessmentId);
        var accessibleInCommunities = assessmentSpecificationGenerator.typesAllowedInAnyCommunity(clientCommunities);

        return assessmentDao.exists(byId.and(accessibleInCommunities));
    }

    @Override
    @Transactional(readOnly = true)
    public <T extends CommunitySecurityAwareEntity> boolean isTypeExistsForAnyClient(Collection<Long> clients, Long assessmentId) {
        var byId = assessmentSpecificationGenerator.byId(assessmentId);
        var typesExistingForClients = assessmentSpecificationGenerator.typesExistingForClients(clients);

        return assessmentDao.exists(byId.and(typesExistingForClients));
    }


    @Override
    @Transactional(readOnly = true)
    public boolean existTypesAllowedInCommunity(OrganizationIdAware clientCommunity) {
        var accessibleInCommunity = assessmentSpecificationGenerator.typesAllowedInCommunity(clientCommunity);

        return assessmentDao.exists(accessibleInCommunity);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isTypeAllowedInOrganization(String assessmentShortName, Long organizationId) {
        var assessment = assessmentDao.findByShortName(assessmentShortName);
        return isTypeAllowedInOrganization(assessment, organizationId);
    }

    @Override
    @Transactional
    public void setTypeAllowedInOrganization(String assessmentShortName, Long organizationId, boolean isAllowed) {
        var assessment = assessmentDao.findByShortName(assessmentShortName);

        var wasAllowed = isTypeAllowedInOrganization(assessment, organizationId);

        if (isAllowed != wasAllowed) {
            if (isAllowed) {
                assessment.getDisabledOrganizationIds().remove(organizationId);
                if (!assessment.getIsShared()) {
                    assessment.getOrganizationIds().add(organizationId);
                }
            } else {
                assessment.getOrganizationIds().remove(organizationId);
                if (assessment.getIsShared()) {
                    assessment.getDisabledOrganizationIds().add(organizationId);
                }
            }
            assessmentDao.save(assessment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findOrganizationIdsWithEnabledAssessment(String shortName) {
        var assessment = assessmentDao.findByShortName(shortName);
        var organizationIds = assessment.getIsShared()
                ? organizationService.findAllIds()
                : assessment.getOrganizationIds();

        var disabledOrganizationIds = assessment.getDisabledOrganizationIds();

        return organizationIds.stream()
                .filter(it -> !disabledOrganizationIds.contains(it))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AssessmentSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return assessmentDao.findById(id, AssessmentSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssessmentSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return assessmentDao.findByIdIn(ids, AssessmentSecurityAwareEntity.class);
    }

    private boolean isTypeAllowedInOrganization(Assessment assessment, Long organizationId) {
        var disabledOrganizationIds = assessment.getDisabledOrganizationIds();
        if (CollectionUtils.isNotEmpty(disabledOrganizationIds) && disabledOrganizationIds.contains(organizationId)) {
            return false;
        }
        if (assessment.getIsShared()) {
            return true;
        }

        var allowedOrganizationIds = assessment.getOrganizationIds();
        return CollectionUtils.isNotEmpty(allowedOrganizationIds) && allowedOrganizationIds.contains(organizationId);
    }
}
