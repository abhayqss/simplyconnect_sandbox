package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.OrganizationFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipantAccessibilityFilter;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNameAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.OrganizationFilterListItemAwareEntity;
import com.scnsoft.eldermark.beans.security.projection.entity.OrganizationSecurityAwareEntity;
import com.scnsoft.eldermark.dao.AffiliatedRelationshipDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.SystemSetupDao;
import com.scnsoft.eldermark.dao.specification.AffiliatedOrganizationSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.LabResearchOrderSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.OrganizationSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.SdohReportLogSpecificationGenerator;
import com.scnsoft.eldermark.dto.organization.OrganizationFeatures;
import com.scnsoft.eldermark.entity.AffiliatedOrganizationRelationship;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Organization_;
import com.scnsoft.eldermark.entity.SystemSetup;
import com.scnsoft.eldermark.entity.assessment.Assessment;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.category.DocumentCategory;
import com.scnsoft.eldermark.entity.password.OrganizationPasswordSettings;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.service.document.category.DocumentCategoryService;
import com.scnsoft.eldermark.service.document.signature.DocumentSignatureRequestService;
import com.scnsoft.eldermark.service.password.OrganizationPasswordSettingsService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.storage.ImageFileStorage;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrganizationServiceImpl implements OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private static final Map<String, String> DEFAULT_CATEGORIES = Map.of(
            "Rental Agreement", "#e53935",
            "Advanced Directive", "#0d47a1",
            "Resident Bill of Rights", "#388e3c"
    );

    private final OrganizationDao organizationDao;
    private final SystemSetupDao systemSetupDao;
    private final OrganizationSpecificationGenerator organizationSpecificationGenerator;
    private final OrganizationPasswordSettingsService organizationPasswordSettingsService;
    private final ImageFileStorage imageFileStorage;
    private final LabResearchOrderSpecificationGenerator labResearchOrderSpecificationGenerator;
    private final SdohReportLogSpecificationGenerator sdohSpecificationGenerator;
    private final AffiliatedRelationshipDao affiliatedRelationshipDao;
    private final AffiliatedOrganizationSpecificationGenerator affiliatedOrganizationSpecificationGenerator;
    private final DocumentCategoryService documentCategoryService;
    private final AssessmentService assessmentService;
    private final DocumentSignatureRequestService signatureRequestService;
    private final LoggedUserService loggedUserService;
    private final AppointmentFeatureNotificationService appointmentFeatureNotificationService;

    //this done to resolve circular dependencies
    public OrganizationServiceImpl(
            OrganizationDao organizationDao,
            SystemSetupDao systemSetupDao,
            OrganizationSpecificationGenerator organizationSpecificationGenerator,
            OrganizationPasswordSettingsService organizationPasswordSettingsService,
            ImageFileStorage imageFileStorage,
            LabResearchOrderSpecificationGenerator labResearchOrderSpecificationGenerator,
            SdohReportLogSpecificationGenerator sdohSpecificationGenerator,
            AffiliatedRelationshipDao affiliatedRelationshipDao,
            AffiliatedOrganizationSpecificationGenerator affiliatedOrganizationSpecificationGenerator,
            @Lazy DocumentCategoryService documentCategoryService,
            @Lazy AssessmentService assessmentService,
            @Lazy DocumentSignatureRequestService signatureRequestService,
            LoggedUserService loggedUserService,
            @Lazy AppointmentFeatureNotificationService appointmentFeatureNotificationService
    ) {
        this.organizationDao = organizationDao;
        this.systemSetupDao = systemSetupDao;
        this.organizationSpecificationGenerator = organizationSpecificationGenerator;
        this.organizationPasswordSettingsService = organizationPasswordSettingsService;
        this.imageFileStorage = imageFileStorage;
        this.labResearchOrderSpecificationGenerator = labResearchOrderSpecificationGenerator;
        this.sdohSpecificationGenerator = sdohSpecificationGenerator;
        this.affiliatedRelationshipDao = affiliatedRelationshipDao;
        this.affiliatedOrganizationSpecificationGenerator = affiliatedOrganizationSpecificationGenerator;
        this.documentCategoryService = documentCategoryService;
        this.assessmentService = assessmentService;
        this.signatureRequestService = signatureRequestService;
        this.loggedUserService = loggedUserService;
        this.appointmentFeatureNotificationService = appointmentFeatureNotificationService;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Organization> findAll(String name, PermissionFilter permissionFilter, Pageable pageable) {
        var byFilter = organizationSpecificationGenerator.byFilter(name);
        var hasAccess = organizationSpecificationGenerator.hasAccess(permissionFilter);

        return organizationDao.findAll(byFilter.and(hasAccess), pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizationFilterListItemAwareEntity> findForFilter(OrganizationFilter filter, PermissionFilter permissionFilter) {
        var hasAccess = organizationSpecificationGenerator.hasAccess(permissionFilter);

        var resultSpecification = hasAccess.and(
                organizationSpecificationGenerator.hasEligibleForDiscoveryCommunities()
        );

        if (BooleanUtils.isTrue(filter.getExcludeAffiliated())) {
            resultSpecification = resultSpecification.and(organizationSpecificationGenerator.byAssociatedEmployeeOrganizations(permissionFilter));
        }

        if (filter.getCanViewSdohReportsOnly()) {
            resultSpecification = resultSpecification.and(sdohSpecificationGenerator.canViewInOrganization(permissionFilter));
        }

        if (filter.getExcludeExternal()) {
            resultSpecification = resultSpecification.and(organizationSpecificationGenerator.excludeExternal());
        }

        if (filter.getIsChatEnabled() != null) {
            resultSpecification = resultSpecification.and(organizationSpecificationGenerator.withEnabledChat(filter.getIsChatEnabled()));
        }

        if (filter.getIsESignEnabled() != null) {
            resultSpecification = resultSpecification.and(organizationSpecificationGenerator.withESignEnabled(filter.getIsESignEnabled()));
        }

        if (BooleanUtils.isTrue(filter.getAreAppointmentsEnabled())) {
            resultSpecification = resultSpecification.and(organizationSpecificationGenerator.withEnabledAppointments());
        }

        return organizationDao.findAll(resultSpecification, OrganizationFilterListItemAwareEntity.class, Sort.by(Direction.ASC, Organization_.NAME));
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAvailableForSignatureRequest(PermissionFilter permissionFilter, Class<P> projectionClass) {
        var hasAccess = organizationSpecificationGenerator.hasAccess(permissionFilter);
        var hasEligibleForDiscoveryCommunities = organizationSpecificationGenerator.hasEligibleForDiscoveryCommunities();
        var hasClientsForSignatureRequest = organizationSpecificationGenerator.hasClientsAvailableForSignatureRequest(permissionFilter);

        var spec = hasAccess
                .and(hasEligibleForDiscoveryCommunities)
                .and(hasClientsForSignatureRequest);

        return organizationDao.findAll(spec, projectionClass);
    }

    @Override
    public List<Long> findAllIds() {
        return organizationDao.findAll((root, query, criteriaBuilder) -> criteriaBuilder.and(), IdAware.class).stream()
            .map(IdAware::getId)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Organization save(Organization organization, Boolean createdAutomatically) {
        checkUniqueValues(organization);

        boolean isNew = organization.getId() == null;

        SystemSetup systemSetup = organization.getSystemSetup();
        if (isNew) {
            organization.setMaxDaysToProcessAppointment(3);
            organization.setCreatedAutomatically(createdAutomatically);

            //system setup has organizationId as primary key, which is currently not present, so organization should be saved first
            organization.setSystemSetup(null);
        }

        Organization result = organizationDao.save(organization);

        if (isNew) {
            organization.setSystemSetup(systemSetup);
            systemSetup.setOrganization(result);
            systemSetup.setOrganizationId(result.getId());
            organizationDao.save(organization);
        }

        List<OrganizationPasswordSettings> databasePasswordSettings = organizationPasswordSettingsService
                .getOrganizationPasswordSettings(result.getId());
        if (CollectionUtils.isEmpty(databasePasswordSettings)) {
            organizationPasswordSettingsService.createDefaultOrganizationPasswordSettings(result.getId());
        }

        if (isNew) {
            DEFAULT_CATEGORIES.forEach((name, color) -> {
                var category = new DocumentCategory();
                category.setOrganizationId(result.getId());
                category.setName(name);
                category.setColor(color);
                documentCategoryService.saveOrUpdate(category);
            });
        }

        return result;
    }

    private void checkUniqueValues(Organization organization) {
        if (organization.getOid() != null && (organization.getId() != null
                ? organizationDao.existsByOidAndIdNot(organization.getOid(), organization.getId())
                : organizationDao.existsByOid(organization.getOid()))) {
            throw new BusinessException("Organization OID already exists");
        }

        if (organization.getId() != null
                ? organizationDao.existsByNameAndIdNot(organization.getName(), organization.getId())
                : organizationDao.existsByName(organization.getName())) {
            throw new BusinessException("Organization name already exists");
        }

        if (organization.getId() != null
                ? organizationDao.existsByAlternativeIdAndIdNot(organization.getAlternativeId(), organization.getId())
                : organizationDao.existsByAlternativeId(organization.getAlternativeId())) {
            throw new BusinessException("Company ID already exists");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Organization findById(Long id) {
        return organizationDao.findById(id).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organization> findByIds(List<Long> ids) {
        return organizationDao.getByIdInOrderByName(ids);
    }

    @Override
    public Organization getOne(Long id) {
        return organizationDao.getOne(id);
    }

    public Long count(PermissionFilter permissionFilter) {
        var accessSpecification = organizationSpecificationGenerator.hasAccess(permissionFilter);
        return organizationDao.count(accessSpecification);
    }

    @Override
    public void uploadOrganizationLogo(Long organizationId, MultipartFile logo, boolean isDeletedLogo) {
        if (isDeletedLogo) {
            deleteOrganizationLogo(organizationId);
        } else {
            Organization organization = organizationDao.findById(organizationId).orElseThrow();
            if (StringUtils.isNotEmpty(organization.getMainLogoPath())) {
                imageFileStorage.delete(organization.getMainLogoPath());
            }
            String ext = FilenameUtils.getExtension(logo.getOriginalFilename());
            String mainLogoPath = "logo_" + organizationId + "_" + System.currentTimeMillis() + "." + ext;
            try (var inputStream = logo.getInputStream()) {
                imageFileStorage.save(inputStream, mainLogoPath);
            } catch (IOException e) {
                logger.error("Error during image upload", e);
                throw new BusinessException(
                        "There were some error while uploading logo for organization id=" + organizationId);
            }
            organization.setMainLogoPath(mainLogoPath);
            organizationDao.save(organization);
        }
    }

    private void deleteOrganizationLogo(Long organizationId) {
        Organization organization = organizationDao.findById(organizationId).orElseThrow();
        if (StringUtils.isNotEmpty(organization.getMainLogoPath())) {
            imageFileStorage.delete(organization.getMainLogoPath());
            organization.setMainLogoPath(null);
            organizationDao.save(organization);
        }
    }

    @Override
    public Pair<byte[], MediaType> downloadLogo(Long id) {
        Organization organization = organizationDao.findById(id).orElseThrow();
        if (imageFileStorage.exists(organization.getMainLogoPath())) {
            return imageFileStorage.loadAsBytesWithMediaType(organization.getMainLogoPath());
        } else {
            return null;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByOid(String oid) {
        return organizationDao.existsByOid(oid);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByName(String name) {
        return organizationDao.existsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByCompanyId(String companyId) {
        return systemSetupDao.existsByLoginCompanyId(companyId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existAccessibleOrganizationsWithLabsEnabled(PermissionFilter permissionFilter) {
        var accessibleOrgs = labResearchOrderSpecificationGenerator.accessibleLabsOrganizations(permissionFilter);
        return organizationDao.exists(accessibleOrgs);
    }

    @Override
    @Transactional(readOnly = true)
    public OrganizationSecurityAwareEntity findSecurityAware(Long id) {
        return organizationDao.findById(id, OrganizationSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AffiliatedOrganizationRelationship> findAffiliatedOrganizations(long id) {
        var affiliated = affiliatedOrganizationSpecificationGenerator.byPrimaryOrganizationId(id);
        return affiliatedRelationshipDao.findAll(affiliated, AffiliatedOrganizationRelationship.class);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNameAware> findAllowedReferralMarketplaceOrganizations(PermissionFilter permissionFilter, Community targetCommunity) {
        var allowedOrganizations = organizationSpecificationGenerator.allowedReferralMarketplaceOrganizations(permissionFilter, targetCommunity);
        return organizationDao.findAll(allowedOrganizations, IdNameAware.class, Sort.by(Direction.ASC, Organization_.NAME));
    }

    @Override
    public List<IdNameAware> findChatAccessible(PermissionFilter permissionFilter, ConversationParticipantAccessibilityFilter filter) {
        var hasAccess = organizationSpecificationGenerator.hasAccess(permissionFilter);
        var byFilter = organizationSpecificationGenerator.byAccessibleChatFilter(permissionFilter, filter);
        return organizationDao.findAll(hasAccess.and(byFilter), IdNameAware.class, Sort.by(Direction.ASC, Organization_.NAME));
    }

    @Override
    @Transactional
    public void updateOrganizationFeatures(Long organizationId, OrganizationFeatures features) {

        var organization = findById(organizationId);

        organization.setChatEnabled(features.getIsChatEnabled());
        organization.setVideoEnabled(features.getIsVideoEnabled());
        assessmentService.setTypeAllowedInOrganization(
                Assessment.COMPREHENSIVE,
                organizationId,
                features.getAreComprehensiveAssessmentsEnabled()
        );
        organization.setIsPaperlessHealthcareEnabled(features.getIsPaperlessHealthcareEnabled());

        updateIsSignatureFeatureEnabled(organization, Boolean.TRUE.equals(features.getIsSignatureEnabled()));

        var areAppointmentsEnabled = features.getAreAppointmentsEnabled();
        var areAppointmentsCurrentlyEnabledInOrganization = Boolean.TRUE.equals(organization.getIsAppointmentsEnabled());
        organization.setIsAppointmentsEnabled(areAppointmentsEnabled);
        organizationDao.save(organization);

        if (!areAppointmentsEnabled && areAppointmentsCurrentlyEnabledInOrganization) {
            appointmentFeatureNotificationService.send(organization);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long aLong, Class<P> projection) {
        return organizationDao.findById(aLong, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> longs, Class<P> projection) {
        return organizationDao.findByIdIn(longs, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public Organization findByAlternativeId(String alternativeId) {
        var byAlternativeId = organizationSpecificationGenerator.byAlternativeId(alternativeId);
        return organizationDao.findOne(byAlternativeId).orElseThrow(() -> new BusinessException(BusinessExceptionType.NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEligibleForDiscoveryOrNoVisibleCommunities(Long organizationId) {
        var byId = organizationSpecificationGenerator.byId(organizationId);
        var hasEligibleForDiscoveryOrNoCommunities = organizationSpecificationGenerator.hasEligibleForDiscoveryOrNoVisibleCommunities();
        return organizationDao.exists(byId.and(hasEligibleForDiscoveryOrNoCommunities));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasEligibleForDiscoveryCommunities(Long organizationId) {
        var byId = organizationSpecificationGenerator.byId(organizationId);
        var hasEligibleForDiscoveryCommunities = organizationSpecificationGenerator.hasEligibleForDiscoveryCommunities();
        return organizationDao.exists(byId.and(hasEligibleForDiscoveryCommunities));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsAccessibleOrganizationsWithAppointmentsEnabled(PermissionFilter permissionFilter) {
        var hasAccess = organizationSpecificationGenerator.hasAccess(permissionFilter);
        var hasEligibleCommunities = organizationSpecificationGenerator.hasEligibleForDiscoveryCommunities();
        var withEnabledAppointments = organizationSpecificationGenerator.withEnabledAppointments();
        return organizationDao.exists(hasAccess.and(hasEligibleCommunities).and(withEnabledAppointments));
    }

    private void updateIsSignatureFeatureEnabled(Organization organization, boolean isSignatureFeatureEnabled) {
        if (!isSignatureFeatureEnabled && organization.isSignatureEnabled()) {
            var currentEmployeeId = loggedUserService.getCurrentEmployeeId();
            signatureRequestService.cancelRequestedByOrganizationIdAsync(organization.getId(), currentEmployeeId);
        }

        organization.setSignatureEnabled(isSignatureFeatureEnabled);
    }
}
