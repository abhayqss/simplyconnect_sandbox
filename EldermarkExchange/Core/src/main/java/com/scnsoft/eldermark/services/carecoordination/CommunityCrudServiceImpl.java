package com.scnsoft.eldermark.services.carecoordination;

import com.google.common.collect.Lists;
import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.OrganizationDaoImpl;
import com.scnsoft.eldermark.dao.carecoordination.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.SimpleDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityCreateDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityFilterDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityViewDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import com.scnsoft.eldermark.shared.carecoordination.service.NewCommunityCreatedDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.shared.exceptions.RestResourceNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * Created by averazub on 3/23/2016.
 */
@Service
public class CommunityCrudServiceImpl implements CommunityCrudService {

    @Autowired
    CareCoordinationCommunityDao careCoordinationCommunityDao;

    @Autowired
    CareCoordinationCommunityListItemDao careCoordinationCommunityListItemDao;

    @Autowired
    CareCoordinationOrganizationDao careCoordinationOrganizationDao;

    @Autowired
    InterfaxConfigurationDao interfaxConfigurationDao;

    @Autowired
    private StateService stateService;

    @Autowired
    OrganizationAddressDao organizationAddressDao;

    @Autowired
    ContactService contactService;

    @Autowired
    CareTeamRoleService careTeamRoleService;

    @Autowired
    CareTeamRoleDao careTeamRoleDao;

    @Autowired
    EmployeeDao employeeDao;

    @Autowired
    ExchangeMailService exchangeMailService;

    @Autowired
    CareTeamService careTeamService;

    @Autowired
    OrganizationCareTeamMemberDao organizationCareTeamMemberDao;

    @Autowired
    OrganizationAddressService organizationAddressService;

    @Value("${comManagement.url}")
    private String comManagementUrl;


    private static final String CARE_COORDINATION_LEGACY_TABLE = "COMPANY";

    private Organization getCommunityDbo(Long id) {
        Organization organization = careCoordinationCommunityDao.findOne(id);
        Long userDatabaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        if (organization.getDatabaseId() != userDatabaseId)
            if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
//                SecurityUtils.getAuthenticatedUser().setCurrentDatabaseId(careCoordinationOrganizationDao.findOne(organization.getDatabaseId()));
            } else {
                throw new RestResourceNotFoundException("Cannot find Community with id=" + id + " in organization " + userDatabaseId);
            }
        return organization;
    }

    @Override
    public CommunityViewDto getCommunityDetails(Long id) {
        Organization organization = getCommunityDbo(id);
        CommunityViewDto communityViewDto = transform(organization);
        List<Object[]> affiliatedOrganizations = careCoordinationCommunityDao.getAffiliatedOrganizations(id, organization.getDatabaseId());

        List<SimpleDto> affiliatedOrganizationDtoList = new ArrayList<SimpleDto>();
        for (Object[] affiliatedOrganization : affiliatedOrganizations) {
            affiliatedOrganizationDtoList.add(new SimpleDto(affiliatedOrganization[0], affiliatedOrganization[1], affiliatedOrganization[2]));
        }
        communityViewDto.setAffiliatedCommunities(affiliatedOrganizationDtoList);
        List<Object[]> affiliatedDatabases = careCoordinationCommunityDao.getAffiliatedDatabases(id, organization.getDatabaseId());

        List<SimpleDto> affiliatedDatabaseDtoList = new ArrayList<SimpleDto>();
        for (Object[] affiliatedDatabase : affiliatedDatabases) {
            affiliatedDatabaseDtoList.add(new SimpleDto(affiliatedDatabase[0], affiliatedDatabase[1]));
        }
        communityViewDto.setAffiliatedDatabases(affiliatedDatabaseDtoList);

        communityViewDto.setHasAffiliated(CollectionUtils.isNotEmpty(affiliatedOrganizations) ||
                CollectionUtils.isNotEmpty(affiliatedDatabases));

        List<Object[]> initialOrganizations = careCoordinationCommunityDao.getInitialOrganizations(id, organization.getDatabaseId());
        List<SimpleDto> initialOrganizationDtoList = new ArrayList<SimpleDto>();
        for (Object[] initialOrganization : initialOrganizations) {
            initialOrganizationDtoList.add(new SimpleDto(initialOrganization[0], initialOrganization[1], initialOrganization[2]));
        }
        communityViewDto.setInitialCommunities(initialOrganizationDtoList);

        List<Object[]> initialDatabases = careCoordinationCommunityDao.getInitialDatabases(id, organization.getDatabaseId());
        List<SimpleDto> initialDatabaseDtoList = new ArrayList<SimpleDto>();
        for (Object[] initialDatabase : initialDatabases) {
            initialDatabaseDtoList.add(new SimpleDto(initialDatabase[0], initialDatabase[1]));
        }
        communityViewDto.setInitialDatabases(initialDatabaseDtoList);
//
//        if (SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_ADMINISTRATORS)) {
//            if (communityViewDto.getAffiliatedView() || SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
//                List<Long> allAffiliatedOrgIds = new ArrayList<Long>();
//                for (SimpleDto affiliatedOrg: affiliatedOrganizationDtoList) {
//                    allAffiliatedOrgIds.add(affiliatedOrg.getId());
//                }
//                for (SimpleDto affiliatedDatabase: affiliatedDatabaseDtoList) {
//                    for (CommunityListItemDto  communityListItemDto: listDto(affiliatedDatabase.getId())) {
//                        allAffiliatedOrgIds.add(communityListItemDto.getId());
//                    }
//                }
//                organizationCareTeamMemberDao.hasAffiliatedCareTeamMembers(allAffiliatedOrgIds, SecurityUtils.getAuthenticatedUser().getEmployee().getDatabaseId());
//            }
//        }
//        Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();

//        if (SecurityUtils.hasRole(CareTeamRoleCode.ADMINISTRATOR) && communityViewDto.getAffiliatedView() &&
//                !organizationCareTeamMemberDao.hasAffiliatedCareTeamMembers(id, organization.getDatabaseId(), employee.getDatabaseId(), false)&&
//         organizationCareTeamMemberDao.hasAffiliatedCareTeamMembers(id, organization.getDatabaseId(), employee.getDatabaseId(), true)) {
        communityViewDto.setCopySettings(isShowCopySettings(id, organization.getDatabaseId()));
//        }

        return communityViewDto;
    }

    public Boolean isShowCopySettings(Long communityId, Long databaseId) {
        if (SecurityUtils.hasRole(CareTeamRoleCode.ADMINISTRATOR) && SecurityUtils.isAffiliatedView()) {
            Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
            if (databaseId == null) {
                databaseId = careCoordinationCommunityDao.getDatabaseId(communityId);
            }
//        !organizationCareTeamMemberDao.hasAffiliatedCareTeamMembers(id, employee.getDatabaseId()) &&
//                organizationCareTeamMemberDao.hasAffiliatedCareTeamMembersInDb(organization.getDatabaseId(), employee.getDatabaseId())) {
            if (!organizationCareTeamMemberDao.hasAffiliatedCareTeamMembers(communityId, databaseId, employee.getDatabaseId(), false) &&
                    organizationCareTeamMemberDao.hasAffiliatedCareTeamMembers(communityId, databaseId, employee.getDatabaseId(), true)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAffiliatedCommunitiesForResident(Long residentId) {
        return careCoordinationCommunityDao.getAffiliatedCommunitiesForResidentCount(residentId) > 0;
    }

    @Override
    public List<SimpleDto> getCopySettingsCommunities(Long communityId) {
        return organizationCareTeamMemberDao.getOtherOrganizationsWithAffiliatedMembers(communityId, SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId(),
                SecurityUtils.getAuthenticatedUser().getEmployee().getDatabaseId());
    }

    @Override
    public void copySettings(Long communityId, Long copyFromCommunityId) {
        List<OrganizationCareTeamMember> affiliatedCareTeamMembers = organizationCareTeamMemberDao.getOrganizationCareTeamMembers(copyFromCommunityId, false, SecurityUtils.getAuthenticatedUser().getEmployee().getDatabaseId(), null);
        for (OrganizationCareTeamMember careTeamMember : affiliatedCareTeamMembers) {
            //if (careTeamMember.getEmployee().getDatabaseId() == SecurityUtils.getAuthenticatedUser().getEmployee().getDatabaseId())
            for (CareTeamMemberNotificationPreferences careTeamMemberNotificationPreferences : careTeamMember.getCareTeamMemberNotificationPreferencesList()) {
                organizationCareTeamMemberDao.detach(careTeamMemberNotificationPreferences);
                careTeamMemberNotificationPreferences.setId(null);
            }
            organizationCareTeamMemberDao.detach(careTeamMember);
            careTeamMember.setOrganization(careCoordinationCommunityDao.findOne(communityId));
            careTeamMember.setId(null);

            organizationCareTeamMemberDao.create(careTeamMember);
        }
    }

//    private List<OrganizationBriefInfo> getAffiliatedCommunities(Long id) {
//        return null;
//    }

    @Override
    public CommunityCreateDto getCommunityCrudDetails(Long id) {
        Organization source = getCommunityDbo(id);
        return transformToCreateDto(source);
    }

    @Override
    public String getCommunityName(Long id) {
        return careCoordinationCommunityDao.getCommunityName(id);
    }

    /**
     * Get List of Ids of communities user has access to.
     * If user has access to all communities in Organization he logged in, result will be null
     *
     * @param filter
     * @return list of ids of communities user has access to, or 'null' in case user has access to all communities
     */
    @Override
    public List<Long> getUserCommunityIds(boolean filter, Long employeeId, boolean throwIfCantView) {
        ExchangeUserDetails user = SecurityUtils.getAuthenticatedUser();
        if (filter) {
//            List<Long> result = user.getAvaliableCommunityIdsIfNotExpired();
//            if (result == null) {
            List<Long> result = getUserCommunityIds(user, filter, employeeId, throwIfCantView);
            if (result != null && !SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_ADMINISTRATORS)) {
                result.addAll(organizationCareTeamMemberDao.getPatientOrganizationIdsForEmployee(employeeId));
            }
            user.setAvailableCommunityIds(result);
//            }
            return result;
        } else {
            return getUserCommunityIds(user, filter, employeeId, throwIfCantView);
        }
    }

    private List<Long> getUserCommunityIds(ExchangeUserDetails user, boolean filter, Long currentEmployeeId) {
        return getUserCommunityIds(user, filter, currentEmployeeId, true);
    }

    private List<Long> getUserCommunityIds(ExchangeUserDetails user, boolean filter, Long currentEmployeeId, boolean throwIfCantView) {
        Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(currentEmployeeId);
        if (!SecurityUtils.isAffiliatedView()) {
            if (SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_VIEW_ALL_COMMUNITIES)) {
                return null;
            }
            if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                List<Long> communityIds = organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(currentEmployeeId, user.getCurrentDatabaseId());
                communityIds.add(user.getLinkedEmployeeById(currentEmployeeId).getCommunityId());
                return communityIds;
            }
//            else if (filter || SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES)) {
//                return organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(user.getEmployeeId(), user.getDatabaseId());
//            }
        } else {
            if (SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_VIEW_ALL_COMMUNITIES)) {
                return checkArrayForNull(careCoordinationCommunityDao.getInitialOrganizationIds(user.getCurrentDatabaseId(), user.getLinkedEmployeeById(currentEmployeeId).getDatabaseId()));
            } else if (SecurityUtils.hasRole(currentEmployeeAuthorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                Set<Long> result;
                List<Long> comAdminCommunityIds = checkArrayForNull(careCoordinationCommunityDao.getInitialOrganizationIds(user.getLinkedEmployeeById(currentEmployeeId).getCommunityId(), user.getCurrentDatabaseId(), user.getLinkedEmployeeById(currentEmployeeId).getDatabaseId()));
                if (comAdminCommunityIds == null) {
                    return null;
                }
                result = new HashSet<Long>(comAdminCommunityIds);
                //add communities in which CA is set as CCT member
                result.addAll(organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(currentEmployeeId, user.getCurrentDatabaseId()));
                return new ArrayList<Long>(result);
            }
//            else if (filter || SecurityUtils.hasAnyRole(CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES)) {
//                return organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(user.getEmployeeId(), user.getDatabaseId());
//            }
        }
        if (filter || SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES)) {
            return organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(currentEmployeeId, user.getCurrentDatabaseId());
        }
        if (throwIfCantView) {
            throw new BusinessAccessDeniedException("User do not have enough privileges for that operation");
        } else {
            return new ArrayList<Long>();
        }

    }

    private List<Long> checkArrayForNull(List<Long> initialOrganizationIds) {
        if (initialOrganizationIds.contains(null)) {
            return null;
        }
        return initialOrganizationIds;
    }

    @Override
    public Page<CommunityListItemDto> listDto() {
        return listDto(new PageRequest(0, 1000), null);
    }

    @Override
    public Page<CommunityListItemDto> filterListDto() {
        return listDto(new PageRequest(0, 1000), SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId(), true);
    }

    @Override
    public Page<CommunityListItemDto> listDto(Pageable pageRequest, CommunityFilterDto communityFilterDto) {
        return listDto(pageRequest, SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId(), false, communityFilterDto);
    }

    @Override
    public Page<CommunityListItemDto> listDto(Pageable pageRequest, final Long databaseId, boolean filter) {
        return listDto(pageRequest, databaseId, false, null);
    }

    @Override
    public Page<CommunityListItemDto> listDto(Pageable pageRequest, final Long databaseId, boolean filter, final CommunityFilterDto communityFilterDto) {

//        if (SecurityUtils.hasRole(CareTeamRoleCode.COMMUNITY_ADMINISTRATOR) && !SecurityUtils.isAffiliatedView()) {
//            Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
////          if (employee.getDatabaseId() == SecurityUtils.getAuthenticatedUser().getDatabaseId()) {
//            CommunityListItemDto communityListItemDto = getCommunityListItem(employee.getCommunityId());
//            return new PageImpl<CommunityListItemDto>(Arrays.asList(communityListItemDto), new PageRequest(0, 1), 1);
////            }
//        }


        Set<Long> currentEmployeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds();
        Set<Long> ids = new HashSet<Long>();
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            ids = null;
        } else {
            for (Long currentEmployeeId : currentEmployeeIds) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(currentEmployeeId);
                if (filter || SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES)) {
                    List<Long> currentCommunityIds = getUserCommunityIds(filter, currentEmployeeId, true);
                    //in case one of linked accounts have access to all communities - we will have full list
                    if (currentCommunityIds == null) {
                        ids = null;
                        break;
                    }
                    ids.addAll(currentCommunityIds);
                }
            }
        }
        final List<Long> userCommunityIds = ids == null ? null : new ArrayList<Long>(ids);

        Page<OrganizationBriefInfo> sourcePage = null;

        if ((userCommunityIds != null) && (userCommunityIds.size() == 0)) {
            sourcePage = new PageImpl<OrganizationBriefInfo>(new ArrayList<OrganizationBriefInfo>(), pageRequest, 0);
        } else {
            if (pageRequest.getSort() == null) {
                Sort sort = new Sort(new Sort.Order("name"));
                pageRequest = new PageRequest(pageRequest.getPageNumber(), pageRequest.getPageSize(), sort);
            }

            Specification<OrganizationBriefInfo> specification = new Specification<OrganizationBriefInfo>() {
                @Override
                public Predicate toPredicate(Root<OrganizationBriefInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                    List<Predicate> predicates = new ArrayList<Predicate>();
                    predicates.add(criteriaBuilder.equal(root.<Long>get("databaseId"), databaseId));
                    if (userCommunityIds != null) {
                        predicates.add(root.<Long>get("id").in(userCommunityIds));
                    }
                    predicates.addAll(OrganizationDaoImpl.eligibleForDiscovery(criteriaBuilder, root));
                    if (communityFilterDto != null && StringUtils.isNotBlank(communityFilterDto.getName())) {
                        String searchStr = "%" + communityFilterDto.getName() + "%";
                        Predicate nameLike = criteriaBuilder.like(root.<String>get("name"), searchStr);
                        Predicate oidLike = criteriaBuilder.like(root.<String>get("oid"), searchStr);
                        predicates.add(criteriaBuilder.or(nameLike, oidLike));
                    }
                    return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
                }
            };

            sourcePage = careCoordinationCommunityListItemDao.findAll(specification, pageRequest);
        }
        List<CommunityListItemDto> targetList = new ArrayList<CommunityListItemDto>();
        for (OrganizationBriefInfo source : sourcePage.getContent()) {
            targetList.add(new CommunityListItemDto(source.getId(), source.getName(), source.getOid(), source.getCreatedAutomatically(), source.getLastModified()));
        }
        return new PageImpl<CommunityListItemDto>(targetList, pageRequest, sourcePage.getTotalElements());
    }


    @Override
    public List<CommunityListItemDto> listDto(final Long databaseId) {

        if (!SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            throw new BusinessAccessDeniedException("User do not have enough privileges for that operation");
        }

        Specification<OrganizationBriefInfo> specification = new Specification<OrganizationBriefInfo>() {
            @Override
            public Predicate toPredicate(Root<OrganizationBriefInfo> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(criteriaBuilder.equal(root.<Long>get("databaseId"), databaseId));
                predicates.addAll(OrganizationDaoImpl.eligibleForDiscovery(criteriaBuilder, root));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        List<OrganizationBriefInfo> organizationBriefInfos = careCoordinationCommunityListItemDao.findAll(specification);
        List<CommunityListItemDto> targetList = new ArrayList<CommunityListItemDto>();
        for (OrganizationBriefInfo source : organizationBriefInfos) {
            targetList.add(new CommunityListItemDto(source.getId(), source.getName(), source.getOid()));
        }
        return targetList;
    }


    private CommunityListItemDto getCommunityListItem(Long id) {
        Organization source = getCommunityDbo(id);
        return transformToListItemDto(source);
    }

    @Override
    public CommunityViewDto create(Long databaseId, CommunityCreateDto community, boolean createdAutomatically) {
        CommunityViewDto communityViewDto = createOrUpdate(databaseId, null, community, createdAutomatically);
        if (createdAutomatically) {
            List<Employee> employees = careTeamRoleDao.getAdministratorsForCompany(databaseId);
            for (Employee employee : employees) {
                NewCommunityCreatedDto dto = new NewCommunityCreatedDto(community.getName(), employee.getFullName(), comManagementUrl, employee.getLoginName());
                exchangeMailService.sendNewCommunityNotification(dto);
            }
        }
        return communityViewDto;
    }

    @Override
    public CommunityViewDto update(Long databaseId, Long communityId, CommunityCreateDto community) {
        return createOrUpdate(databaseId, communityId, community, null);
    }

    @Override
    public CommunityViewDto updateData(Long databaseId, Long communityId, CommunityCreateDto community) {
        return updateCommunityData(databaseId, communityId, community);
    }

    private CommunityViewDto updateCommunityData(Long databaseId, Long communityId, CommunityCreateDto community) {
        Organization target;
        if (communityId==0) {
            target = new Organization();
            target.setDatabase(careCoordinationOrganizationDao.findOne(databaseId));
            target.setInactive(false);
            target.setCreatedAutomatically(null);
            target.setModuleCloudStorage(false);
            target.setModuleHie(true);
            target.setTestingTraining(false);
            target.setLegacyId(UUID.randomUUID().toString());
            target.setLegacyTable(CARE_COORDINATION_LEGACY_TABLE);
        } else {
            target = getCommunityDbo(communityId);
        }
        transform(community, target);

        if (communityId==null) {
            target.setLegacyId(target.getId().toString());
        }

        OrganizationAddress address = target.getAddresses().get(0);
        if (communityId==null) {
            address.setLegacyId(target.getId().toString());
        }

        getOrganizationAddressService().populateAllLocationForOutdatedAddresses();
        return transform(target);
    }

    private CommunityViewDto createOrUpdate(Long databaseId, Long communityId, CommunityCreateDto community, Boolean createdAutomatically) {
        Organization target;
        if (communityId == null) {
            target = new Organization();
            target.setDatabase(careCoordinationOrganizationDao.findOne(databaseId));
            target.setInactive(false);
            target.setCreatedAutomatically(createdAutomatically);
            target.setModuleCloudStorage(false);
            target.setModuleHie(true);
            target.setTestingTraining(false);
            target.setLegacyId(UUID.randomUUID().toString());
            target.setLegacyTable(CARE_COORDINATION_LEGACY_TABLE);
        } else {
            target = getCommunityDbo(communityId);
        }
        transform(community, target);

        if (communityId == null) {
            careCoordinationCommunityDao.save(target);
            target.setLegacyId(target.getId().toString());
        }

        OrganizationAddress address = target.getAddresses().get(0);
        if (communityId == null) {
            address.setLegacyId(target.getId().toString());
        }
        organizationAddressDao.save(address);

        careCoordinationCommunityDao.save(target);
        getOrganizationAddressService().populateAllLocationForOutdatedAddresses();
        return transform(target);
    }

    @Override
    public void deleteCommunity(Long communityId) {
        Organization org = getCommunityDbo(communityId);
        if (org.getInterfaxConfiguration() != null) interfaxConfigurationDao.delete(org.getInterfaxConfiguration());
        careCoordinationCommunityDao.delete(communityId);
    }

    public CommunityViewDto transform(Organization organization) {
        final CommunityViewDto result = new CommunityViewDto();
        result.setId(organization.getId());
        result.setName(organization.getName());

        result.setDatasourceName(organization.getDatabase().getName());
        result.setTelecom(organization.getTelecom() == null ? null : organization.getTelecom().getValue());
        result.setEmail(organization.getEmail());
        result.setOid(organization.getOid());
        result.setMainLogoPath(organization.getMainLogoPath());
        result.setPhone(organization.getPhone());

        if (CollectionUtils.isNotEmpty(organization.getAddresses())) {
            final AddressDto addressDto = new AddressDto();
            final OrganizationAddress organizationAddress = organization.getAddresses().get(0);
            if (!StringUtils.isEmpty(organizationAddress.getState())) {
                addressDto.setState(CareCoordinationUtils.createKeyValueDto(stateService.findByAbbr(organizationAddress.getState())));
            }
            addressDto.setCity(organizationAddress.getCity());
            addressDto.setZip(organizationAddress.getPostalCode());
            addressDto.setStreet(organizationAddress.getStreetAddress());
            result.setAddress(addressDto);
        } else {
            result.setAddress(null);
        }

//        result.setAffiliatedView(SecurityUtils.isAffiliatedView());

        return result;
    }

    public CommunityCreateDto transformToCreateDto(Organization source) {
        CommunityCreateDto target = new CommunityCreateDto();
        target.setId(source.getId());
        target.setOid(source.getOid());
        target.setEmail(source.getEmail());
        target.setName(source.getName());
        target.setPhone(source.getPhone());
        target.setMainLogoPath(source.getMainLogoPath());
        OrganizationAddress address = ((source.getAddresses() != null) && (source.getAddresses().size() > 0)) ? source.getAddresses().get(0) : null;

        if (address != null) {
            target.setCity(address.getCity());
            target.setPostalCode(address.getPostalCode());
            target.setStreet(address.getStreetAddress());
            if ((address.getState() != null) && (!address.getState().isEmpty())) {
                target.setStateId(stateService.findByAbbr(address.getState()).getId());
            }

        }
        return target;
    }

    public CommunityListItemDto transformToListItemDto(Organization source) {
        CommunityListItemDto target = new CommunityListItemDto();
        target.setId(source.getId());
        target.setOid(source.getOid());
        target.setName(source.getName());
//        target.setCreatedAutomatically(source.getCreatedAutomatically());
        return target;
    }

    protected void transform(CommunityCreateDto source, Organization target) {
        if ((source == null) || (target == null)) return;
        target.setName(source.getName());
        target.setOid(source.getOid());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        OrganizationAddress address;
        if ((target.getAddresses() == null) || (target.getAddresses().size() == 0)) {
            address = new OrganizationAddress();
            address.setDatabase(target.getDatabase());
            address.setOrganization(target);
            address.setLegacyTable(CARE_COORDINATION_LEGACY_TABLE);
            address.setLegacyId(UUID.randomUUID().toString());
            if (target.getAddresses() == null) target.setAddresses(new ArrayList<OrganizationAddress>());
            target.getAddresses().add(address);
        } else {
            address = target.getAddresses().get(0);
        }
        address.setCity(source.getCity());
        address.setPostalCode(source.getPostalCode());
        address.setStreetAddress(source.getStreet());
        if (source.getStateId() != null) {
            address.setState(stateService.get(source.getStateId()).getAbbr());
        } else {
            address.setState(null);
        }
        target.setLastModified(new Date());

    }

    @Override
    public Boolean checkIfUnique(final CommunityCreateDto data) {
        final Long userDatabaseId = SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId();
        //TODO check what to do with communities that are not eligible
        Specification<Organization> spec = new Specification<Organization>() {
            @Override
            public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (data.getId() != null) {
                    predicates.add(criteriaBuilder.notEqual(root.<Integer>get("id"), data.getId()));
                }
                if (StringUtils.isNotEmpty(data.getName())) {
                    predicates.add(criteriaBuilder.like(root.<String>get("name"), data.getName()));
                    predicates.add(criteriaBuilder.equal(root.join("database").<Long>get("id"), userDatabaseId));
                }

                if (StringUtils.isNotEmpty(data.getOid())) {
                    predicates.add(criteriaBuilder.like(root.<String>get("oid"), data.getOid()));
                }


                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        return careCoordinationCommunityDao.count(spec) == 0;
    }


    public Long getOrCreateCommunityFromSchema(Long databaseId, com.scnsoft.eldermark.schema.Community source) {
        List<Organization> communitiesFound = careCoordinationCommunityDao.searchCommunityByOid(source.getID(), databaseId);
        if (communitiesFound.size() == 0) {
//            Long contactId = null;
            Database database = careCoordinationOrganizationDao.findOne(databaseId);

            CommunityCreateDto communityCreateDto = new CommunityCreateDto();
            communityCreateDto.setName(source.getName());
            communityCreateDto.setOid(source.getID());
            communityCreateDto.setEmail(source.getEmail());
            communityCreateDto.setPhone(source.getPhone());
            CommunityViewDto communityDto = create(databaseId, communityCreateDto, true);

            return communityDto.getId();
        }

        if (communitiesFound.size() == 1) {
            return communitiesFound.get(0).getId();
        } else {
            throw new NonUniqueResultException("Found more than 1 community by OID " + source.getID());
        }
    }

    public void checkViewAccessToCommunitiesOrThrow(Long communityId) {
        checkViewAccessToCommunitiesOrThrow(Lists.newArrayList(communityId), false);
    }

    public void checkViewAccessToCommunitiesOrThrow(Long communityId, Long databaseId) {
        checkViewAccessToCommunitiesOrThrow(Lists.newArrayList(communityId), databaseId, false);
    }

    public void checkViewAccessToCommunitiesOrThrow(List<Long> communityIds, boolean filter) {
        checkViewAccessToCommunitiesOrThrow(communityIds, SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId(), false);
    }

    private void checkViewAccessToCommunitiesOrThrow(List<Long> communityIds, Long databaseId, boolean filter) {
        if ((communityIds == null) || (communityIds.size() == 0)) return;
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return;
        }
        ExchangeUserDetails details = SecurityUtils.getAuthenticatedUser();
        long countOfIncorrect = careCoordinationCommunityDao.countCommunitiesNotBelongingToOrganization(communityIds, databaseId);
        if (countOfIncorrect > 0)
            throw new BusinessAccessDeniedException("User do not have enough privileges to specified communities");
//        if (countOfIncorrect>0 && !careCoordinationCommunityDao.isInitialCommunity()) {
//            throw new BusinessAccessDeniedException("User do not have enough privileges to specified communities");
//        }
        Set<Long> employeeIds = details.getEmployeeAndLinkedEmployeeIds();
        List<Long> currentStepCommunityIds = new ArrayList<Long>(communityIds);
        List<Long> unauthorizedCommunityIds = new ArrayList<Long>();
        for (Long employeeId : employeeIds) {
            for (Long currentCommunityId : currentStepCommunityIds) {
                if (doHaveAccess(employeeId, currentCommunityId, filter)) {
                    continue;
                } else {
                    unauthorizedCommunityIds.add(currentCommunityId);
                }
            }
            if (CollectionUtils.isEmpty(unauthorizedCommunityIds)) {
                return;
            }
            currentStepCommunityIds = unauthorizedCommunityIds;
            unauthorizedCommunityIds = new ArrayList<Long>();
        }
        throw new BusinessAccessDeniedException("User do not have enough privileges to specified community");

    }


    private Boolean doHaveAccess(Long employeeId, Long communityId, boolean filter) {
        Set<GrantedAuthority> authorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(employeeId);
        LinkedContactDto currentEmployee = SecurityUtils.getAuthenticatedUser().getLinkedEmployeeById(employeeId);
        if (SecurityUtils.hasAnyRole(authorities, CareTeamRoleCode.ROLES_CAN_VIEW_ALL_COMMUNITIES)) {
            return Boolean.TRUE;
        } else if (SecurityUtils.hasRole(authorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
            if (!SecurityUtils.isAffiliatedView()) {
                if (communityId.equals(currentEmployee.getCommunityId()) || checkCctPctCommunities(employeeId, communityId, true)) {
                    return Boolean.TRUE;
                }
            } else {
                List<Long> initialCommunityIds = careCoordinationCommunityDao.getInitialOrganizationIds(currentEmployee.getCommunityId(), SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId(), currentEmployee.getDatabaseId());
                if (initialCommunityIds.contains(communityId) || initialCommunityIds.contains(null)) {
                    return Boolean.TRUE;
                }
                //also check CareTeam communities in which Community Admin is set as member
                if (checkCctPctCommunities(employeeId, communityId, filter)) {
                    return Boolean.TRUE;
                }
            }
        } else if ((filter || SecurityUtils.hasAnyRole(authorities, CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES)) && checkCctPctCommunities(employeeId, communityId, filter)) {
            return Boolean.TRUE;

        }
        return Boolean.FALSE;
    }

    private boolean checkCctPctCommunities(Long employeeId, Long communityId, boolean checkPct) {
        ExchangeUserDetails details = SecurityUtils.getAuthenticatedUser();
        List<Long> orgIds = organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(employeeId, details.getCurrentDatabaseId());
        if (checkPct) {
            orgIds.addAll(organizationCareTeamMemberDao.getPatientOrganizationIdsForEmployee(employeeId));
        }
        return orgIds.contains(communityId);
    }

    public boolean checkAddEditCareTeamAccessToCommunity(Long communityId, Long careTeamId, Long careTeamEmployeeSelectId) {
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return false;
        }
        if (communityId == null)
            throw new BusinessAccessDeniedException("User does not have enough privileges for that operation");
        ExchangeUserDetails details = SecurityUtils.getAuthenticatedUser();
        Long databaseId = details.getCurrentDatabaseId();
        if (!databaseId.equals(getCommunityDbo(communityId).getDatabaseId()))
            throw new BusinessAccessDeniedException("User does not have enough privileges for that database");

        boolean canEditSelf = false;
        Set<Long> employeeIds = details.getEmployeeAndLinkedEmployeeIds();
        for (Long employeeId : employeeIds) {
            Pair<Boolean, Boolean> result = canEditOnlySelfAndHaveAccess(employeeId, communityId, careTeamId, careTeamEmployeeSelectId);
            //if one of employees have access - we are done
            if (result.getSecond() == true) {
                return result.getFirst();
            } else {
                canEditSelf = canEditSelf || result.getFirst();
            }
        }
        if (canEditSelf) {
            throw new SecurityException("Your System Role allow editing yourself only");
        } else {
            throw new BusinessAccessDeniedException("User do not have enough privileges for that operation");
        }
    }

    /**
     * @return first is "can edit only self", second is "have add/edit access"
     */
    private Pair<Boolean, Boolean> canEditOnlySelfAndHaveAccess(Long employeeId, Long communityId, Long careTeamId, Long careTeamEmployeeSelectId) {
        ExchangeUserDetails details = SecurityUtils.getAuthenticatedUser();
        Long databaseId = details.getCurrentDatabaseId();
        Set<GrantedAuthority> authorities = details.getEmployeeAuthoritiesMap().get(employeeId);
        LinkedContactDto currentEmployee = details.getLinkedEmployeeById(employeeId);
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return new Pair<Boolean, Boolean>(false, true);
        }
        if (SecurityUtils.isAffiliatedView()) {
            if (SecurityUtils.hasRole(authorities, CareTeamRoleCode.ADMINISTRATOR)) {
                List<Long> initialOrgIds = careCoordinationCommunityDao.getInitialOrganizationIds(details.getCurrentDatabaseId(), currentEmployee.getDatabaseId());
                if (initialOrgIds.contains(communityId) || initialOrgIds.contains(null)) {
                    return new Pair<Boolean, Boolean>(false, true);
                }
            }
            if (SecurityUtils.hasRole(authorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                List<Long> initialOrgIds = careCoordinationCommunityDao.getInitialOrganizationIds(details.getCommunityId(), details.getCurrentDatabaseId(), currentEmployee.getDatabaseId());
                if (initialOrgIds.contains(communityId) || initialOrgIds.contains(null)) {
                    if (initialOrgIds.contains(communityId) || initialOrgIds.contains(null)) {
                        Long memberCommunityId;
                        if (careTeamEmployeeSelectId == null) {
                            CareTeamMember member = organizationCareTeamMemberDao.get(careTeamId);
                            memberCommunityId = member.getEmployee().getCommunityId();
                        } else {
                            memberCommunityId = contactService.getEmployeeCommunityId(careTeamEmployeeSelectId);
                        }
                        if (currentEmployee.getCommunityId().equals(memberCommunityId)) {
                            return new Pair<Boolean, Boolean>(false, true);
                        }
                    }
                } else {
                    List<Long> orgIds = organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(employeeId, databaseId);
                    if (orgIds.contains(communityId)) return new Pair<Boolean, Boolean>(false, true);
                }
            }
        } else {
            if (SecurityUtils.hasRole(authorities, CareTeamRoleCode.ADMINISTRATOR) && currentEmployee.getDatabaseId().equals(databaseId)) {
                return new Pair<Boolean, Boolean>(false, true);
            }
            if (SecurityUtils.hasRole(authorities, CareTeamRoleCode.COMMUNITY_ADMINISTRATOR)) {
                if (careTeamId != null) {
                    CareTeamMember member = organizationCareTeamMemberDao.get(careTeamId);
                    if (communityId.equals(currentEmployee.getCommunityId()) && member.getEmployee().getCommunityId().equals(currentEmployee.getCommunityId())) {
                        return new Pair<Boolean, Boolean>(false, true);
                    } else {
                        List<Long> orgIds = organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(employeeId, databaseId);
                        if (orgIds.contains(communityId) && member.getEmployee().getCommunityId().equals(currentEmployee.getCommunityId())) {
                            return new Pair<Boolean, Boolean>(false, true);
                        }
                    }
                } else if (communityId.equals(currentEmployee.getCommunityId())) {
                    return new Pair<Boolean, Boolean>(false, true);
                }
            }
        }
        if (SecurityUtils.hasAnyRole(authorities, CareTeamRoleCode.ROLES_CAN_ADD_EDIT_COMMUNITY_CARE_TEAM_MEMBERS)) {
            List<Long> orgIds = organizationCareTeamMemberDao.getCctOrganizationIdsForEmployee(employeeId, databaseId);
            if (orgIds.contains(communityId)) return new Pair<Boolean, Boolean>(false, true);  //TODO!!!!!!!!!!!
        }
        if (SecurityUtils.hasAnyRole(authorities, CareTeamRoleCode.ROLES_CAN_EDIT_SELF_CARE_TEAM_MEMBERS)) {
            if (careTeamId == null)
                return new Pair<Boolean, Boolean>(false, false);// throw new BusinessAccessDeniedException("User do not have enough privileges for that operation");
            final Long employeeIdCtm = careTeamService.getEmployeeIdForCareTeamMember(careTeamId);
            if (employeeIdCtm != null && !employeeIdCtm.equals(employeeId)) {
                return new Pair<Boolean, Boolean>(true, false);
                //throw new SecurityException("Your System Role allow editing yourself only");
            } else {
                return new Pair<Boolean, Boolean>(true, true);
                //return true;
            }
        }
        return new Pair<Boolean, Boolean>(false, false);
        //throw new BusinessAccessDeniedException("User do not have enough privileges for that operation");
    }

    @Override
    public Integer getCommunityCountForCurrentUser() {
        Set<Long> currentEmployeeIds = SecurityUtils.getAuthenticatedUser().getEmployeeAndLinkedEmployeeIds();
        Set<Long> ids = new HashSet<Long>();
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            ids = null;
        } else {
            for (Long currentEmployeeId : currentEmployeeIds) {
                Set<GrantedAuthority> currentEmployeeAuthorities = SecurityUtils.getAuthenticatedUser().getEmployeeAuthoritiesMap().get(currentEmployeeId);
                if (SecurityUtils.hasAnyRole(currentEmployeeAuthorities, CareTeamRoleCode.ROLES_CAN_VIEW_OWN_COMMUNITIES)) {
                    List<Long> currentCommunityIds = getUserCommunityIds(false, currentEmployeeId, true);
                    //in case one of linked accounts have access to all communities - we will have full list
                    if (currentCommunityIds == null) {
                        ids = null;
                        break;
                    }
                    ids.addAll(currentCommunityIds);
                }
            }
        }
        return ids == null ? getCommunityCountForDatabase(SecurityUtils.getAuthenticatedUser().getCurrentDatabaseId()) : ids.size();
    }


    public Integer getCommunityCountForDatabase(Long databaseId) {
        boolean isEldermarkUser = SecurityUtils.isEldermarkUser() || true;
        boolean isCloudUser = SecurityUtils.isCloudUser() || SecurityUtils.isCloudManager();
        Long communityCount = 0l;
        DatabaseOrgCountEntity databaseOrgCountEntity = careCoordinationOrganizationDao.getDatabaseOrgCount(databaseId);
        if (databaseOrgCountEntity != null) {
            if (isEldermarkUser && isCloudUser) {
                communityCount = databaseOrgCountEntity.getOrgHieOrCloudCount();
            } else if (isEldermarkUser) {
                communityCount = databaseOrgCountEntity.getOrgHieCount();
            } else if (isCloudUser) {
                communityCount = databaseOrgCountEntity.getOrgCloudCount();
            }
        }
        return communityCount == null ? null : communityCount.intValue();
    }


    @Override
    public Long getCommunityIdByOrgAndCommunityOid(String orgOid, String communityOid) {
        try {
            return careCoordinationCommunityDao.getCommunityIdByOrgAndCommunityOid(orgOid, communityOid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public OrganizationAddressService getOrganizationAddressService() {
        return organizationAddressService;
    }

    public void setOrganizationAddressService(OrganizationAddressService organizationAddressService) {
        this.organizationAddressService = organizationAddressService;
    }
}
