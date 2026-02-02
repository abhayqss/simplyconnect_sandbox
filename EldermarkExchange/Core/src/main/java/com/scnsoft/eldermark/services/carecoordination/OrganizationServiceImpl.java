package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.authentication.ExchangeUserDetails;
import com.scnsoft.eldermark.authentication.SecurityUtils;
import com.scnsoft.eldermark.dao.EmployeeDao;
import com.scnsoft.eldermark.dao.carecoordination.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.password.DatabasePasswordSettings;
import com.scnsoft.eldermark.services.PersonService;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.mail.ExchangeMailService;
import com.scnsoft.eldermark.services.password.DatabasePasswordSettingsService;
import com.scnsoft.eldermark.shared.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.community.CommunityListItemDto;
import com.scnsoft.eldermark.shared.carecoordination.contacts.LinkedContactDto;
import com.scnsoft.eldermark.shared.carecoordination.service.AffiliatedOrganizationNotificationDto;
import com.scnsoft.eldermark.shared.carecoordination.service.NewOrgCreatedDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.shared.exceptions.BusinessAccessDeniedException;
import com.scnsoft.eldermark.shared.exceptions.RestResourceNotFoundException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;

/**
 * @author averazub
 * @author mradzivonenka
 * @author knetkachou
 * @author phomal
 * Created by averazub on 3/21/2016.
 */
@Service
public class OrganizationServiceImpl implements OrganizationService {

    @Autowired
    CareCoordinationOrganizationDao careCoordinationOrganizationDao;

    @Autowired
    CareCoordinationOrganizationDaoCustomImpl careCoordinationOrganizationDaoCustom;

    @Autowired
    CareCoordinationCommunityDao careCoordinationCommunityDao;

    @Autowired
    EventNotificationProcessService eventNotificationProcessService;

    @Autowired
    ExchangeMailService exchangeMailService;

    @Autowired
    CommunityCrudService communityCrudService;

    @Autowired
    private CareTeamRoleDao careTeamRoleDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private CareTeamService careTeamService;

    @Autowired
    private StateService stateService;

    @Autowired
    private DatabasePasswordSettingsService databasePasswordSettingsService;

    @Value("${orgManagement.url}")
    private String orgManagementUrl;

//    @Value("${comManagement.url}")
//    private String comManagementUrl;

    @Value("${portal.url}")
    private String portalUrl;



    @Override
    public OrganizationDto getOrganization(Long id) {
        Database database = careCoordinationOrganizationDao.findOne(id);
        if (database==null) throw new RestResourceNotFoundException("Cannot find Organization with id="+id);
        return transform(database);
    }

    @Override
    public OrganizationDto getOrganizationWithAffiliatedDetails(Long id) {
        OrganizationDto organizationDto = getOrganization(id);
        organizationDto.setAffiliatedDetails(getOrganizationAffiliatedDetails(id));
        return organizationDto;
    }

    private List<OrganizationAffiliatedDetailsDto> getOrganizationAffiliatedDetails(Long id) {
        List<AffiliatedOrganizations>affiliatedOrganizationList = careCoordinationOrganizationDao.getAffiliatedOrganizations(id);
        List<OrganizationAffiliatedDetailsDto>detailsDtoList = new ArrayList<OrganizationAffiliatedDetailsDto>();
        for (AffiliatedOrganizations affiliatedOrganizations:affiliatedOrganizationList) {
            OrganizationAffiliatedDetailsDto detailsDto = findInList (affiliatedOrganizations.getAffiliatedDatabaseId(),detailsDtoList);
            Long primaryOrganizationId = ObjectUtils.firstNonNull(affiliatedOrganizations.getPrimaryOrganizationId(), 0L);
            Long affiliatedOrganizationId = ObjectUtils.firstNonNull(affiliatedOrganizations.getAffiliatedOrganizationId(), 0L);
            if (detailsDto!=null){
                if (!detailsDto.getCommunityIds().contains(primaryOrganizationId)){
                    detailsDto.getCommunityIds().add(primaryOrganizationId);
                }

                if (!detailsDto.getAffCommunitiesIds().contains(affiliatedOrganizationId)){
                    detailsDto.getAffCommunitiesIds().add(affiliatedOrganizationId);
                }
            }
            else {
                detailsDto = new OrganizationAffiliatedDetailsDto();
                List<Long> communityIds = new ArrayList<Long>();
                communityIds.add(primaryOrganizationId);
                detailsDto.setCommunityIds(communityIds);
                detailsDto.setAffOrgId(affiliatedOrganizations.getAffiliatedDatabaseId());

                List<Long> affCommunityIds = new ArrayList<Long>();
                affCommunityIds.add(affiliatedOrganizationId);
                detailsDto.setAffCommunitiesIds(affCommunityIds);

                detailsDtoList.add(detailsDto);
            }
        }
        return detailsDtoList;
    }

    private OrganizationAffiliatedDetailsDto findInList(Long affiliatedDatabaseId, List<OrganizationAffiliatedDetailsDto> detailsDtoList) {
        for (OrganizationAffiliatedDetailsDto detailsDto:detailsDtoList){
            if (detailsDto.getAffOrgId().equals(affiliatedDatabaseId)){
                return detailsDto;
            }
        }
        return null;
    }

//    public Specification<Database> prepareFilterSpecification(final OrganizationFilterDto organizationFilterDto) {
//        return  new Specification<Database>() {
//            @Override
//            public Predicate toPredicate(Root<Database> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
//                List<Predicate> predicates = new ArrayList<Predicate>();
//                if (!StringUtils.isEmpty(organizationFilterDto.getName())) {
//                    predicates.add(criteriaBuilder.like(root.<String>get("name"), "%"+organizationFilterDto.getName()+"%"));
//                }
//                if (organizationFilterDto.getLoginCompanyId()!=null) {
//                    predicates.add(criteriaBuilder.equal(root.join("systemSetup").<String>get("loginCompanyId"), organizationFilterDto.getLoginCompanyId()));
//                }
//                if (organizationFilterDto.getId()!=null) {
//                    predicates.add(criteriaBuilder.equal(root.<String>get("id"), organizationFilterDto.getId()));
//                }
//                if (organizationFilterDto.getIsService()!=null) {
//                    predicates.add(criteriaBuilder.equal(root.<String>get("isService"), organizationFilterDto.getIsService()));
//                }
//                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
//            }
//        };
//    }

//    public Pageable preparePageRequest(Pageable pageable) {
//        List<Sort.Order> orders = new ArrayList<Sort.Order>();
//        if (pageable.getSort()!=null) {
//            for (Sort.Order sourceOrder : pageable.getSort()) {
//                String property;
//                if ("loginCompanyId".equals(sourceOrder.getProperty())) {
//                    property = "systemSetup.loginCompanyId";
//                } else if ("communityCount".equals(sourceOrder.getProperty())) {
//                    boolean isEldermarkUser = SecurityUtils.isEldermarkUser();
//                    boolean isCloudUser = SecurityUtils.isCloudUser() || SecurityUtils.isCloudManager();
//                    if (isEldermarkUser && isCloudUser) {
//                        property = "databaseOrgCountEntity.orgHieOrCloudCount";
//                    } else if (isEldermarkUser) {
//                        property = "databaseOrgCountEntity.orgHieCount";
//                    } else if (isCloudUser) {
//                        property = "databaseOrgCountEntity.orgCloudCount";
//                    } else {
//                        property = "databaseOrgCountEntity.orgCount";
//                    }
//                } else {
//                    property = sourceOrder.getProperty();
//                }
//                orders.add(new Sort.Order(sourceOrder.getDirection(), property));
//            }
//        } else {
//            orders.add(new Sort.Order("name"));
//        }
//        Sort sort = new Sort(orders);
//        Pageable result = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sort);
//        return result;
//    }

    @Override
    public Page<OrganizationListItemDto> list(final OrganizationFilterDto organizationFilterDto, Pageable pageRequest) {
//        Page<Database> sourcePage = careCoordinationOrganizationDao.findAll(prepareFilterSpecification(organizationFilterDto), preparePageRequest(pageRequest));
//        List<OrganizationListItemDto> targetList = transform(sourcePage.getContent());
//        return new PageImpl<OrganizationListItemDto>(targetList, pageRequest, sourcePage.getTotalElements());
        boolean isEldermarkUser = SecurityUtils.isEldermarkUser() || true;
        boolean isCloudUser = SecurityUtils.isCloudUser() || SecurityUtils.isCloudManager();
        List<OrganizationListItemDto> result = careCoordinationOrganizationDaoCustom.getOrganizationsList(organizationFilterDto,pageRequest,isEldermarkUser,isCloudUser);
        return new PageImpl<OrganizationListItemDto>(result, pageRequest, careCoordinationOrganizationDaoCustom.getOrganizationsCount(organizationFilterDto)) ;
    }

    @Override
    public List<Pair<Long, String>> listBrief() {
        if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return careCoordinationOrganizationDaoCustom.getBriefList();
        }
        else {
            Set<Pair<Long, String>> set = new HashSet<Pair<Long, String>>();
//            list.add(new Pair(SecurityUtils.getAuthenticatedUser().getDatabaseId(),SecurityUtils.getAuthenticatedUser().getCurrentDatabaseName()));
//            list.addAll(careCoordinationOrganizationDaoCustom.getPrimaryBriefList(SecurityUtils.getAuthenticatedUser().getDatabaseId()));
            Employee employee = SecurityUtils.getAuthenticatedUser().getEmployee();
            List<LinkedContactDto> linkedEmployees =SecurityUtils.getAuthenticatedUser().getLinkedEmployees();
            set.add(new Pair(employee.getDatabaseId(), employee.getDatabase().getName()));

            Set<Long> employeeDatabaseIds = new HashSet<Long>();
            employeeDatabaseIds.add(employee.getDatabaseId());
            if (CollectionUtils.isNotEmpty(linkedEmployees)) {
                for (LinkedContactDto linkedEmployee : linkedEmployees) {
                    employeeDatabaseIds.add(linkedEmployee.getDatabaseId());
                    set.add(new Pair<Long, String>(linkedEmployee.getDatabaseId(), linkedEmployee.getOrganization()));
                }
            }
            set.addAll(careCoordinationOrganizationDaoCustom.getPrimaryBriefList(employeeDatabaseIds));

            List<Pair<Long, String>> result = new ArrayList<Pair<Long, String>>(set);
            Collections.sort(result, new Comparator<Pair<Long, String>>() {
                @Override
                public int compare(Pair<Long, String> o1, Pair<Long, String> o2) {
                    String name1 = o1.getSecond();
                    String name2 = o2.getSecond();
                    return name1.compareTo(name2);
                }
            });
            return result;
        }
    }

    @Override
    public Boolean isSingleOrganizationAccessible() {
        if (SecurityUtils.getAuthenticatedUser().getCurrentAndLinkedDatabaseIds().size() > 1) {
            return Boolean.FALSE;
        }
        else if (SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR)) {
            return careCoordinationOrganizationDaoCustom.getCount() == 1;
        }
        else {
            return careCoordinationOrganizationDaoCustom.getPrimaryCount(SecurityUtils.getAuthenticatedUser().getCurrentAndLinkedDatabaseIds()) == 0;
        }
    }

    public boolean checkDatabaseAccess(long databaseId, Set<Long> employeeDbIds) {
        return careCoordinationOrganizationDaoCustom.checkDatabaseAccess(databaseId,employeeDbIds);
    }


    @Override
    public OrganizationDto create(OrganizationDto organization, Boolean createdAutomatically) {
        return createOrUpdate(null, organization, createdAutomatically);
    }

    @Override
    public OrganizationDto update(Long organizationId, OrganizationDto organization) {
        return createOrUpdate(organizationId, organization, null);
    }


    private OrganizationDto createOrUpdate(Long organizationId, OrganizationDto organization, Boolean createdAutomatically) {
        Database target;
        boolean isNew=false;
        if (organizationId==null) {
            isNew = true;
            target = new Database();
            target.setCreatedAutomatically(createdAutomatically);
            target.setAlternativeId(organization.getLoginCompanyId());
            //TODO add to UI
            target.setMaxDaysToProcessAppointment(3);
        } else {
            target = careCoordinationOrganizationDao.findOne(organizationId);
            if (target==null) throw new RestResourceNotFoundException("Cannot find Organization with id="+organizationId);
        }
        transform(organization, target);
        careCoordinationOrganizationDao.save(target);
        transformSystemSetup(organization, target);
        careCoordinationOrganizationDao.save(target);

        List<AffiliatedOrganizations> oldAffiliatedOrganizationList = new ArrayList<AffiliatedOrganizations>();
        List<AffiliatedOrganizations> newAffiliatedOrganizationList = new ArrayList<AffiliatedOrganizations>();
        List<Long> sentToDatabaseIds = new ArrayList<Long>();

        if (!isNew) {
            oldAffiliatedOrganizationList = careCoordinationOrganizationDao.getAffiliatedOrganizations(target.getId());
        }
        careCoordinationOrganizationDao.clearAffiliatedDetails(target.getId());
        if (organization.getAffiliatedDetails()!=null) {
            for (OrganizationAffiliatedDetailsDto affDetailsDto : organization.getAffiliatedDetails()) {
//            boolean orgSent = false;
                for (AffiliatedOrganizations affiliatedOrganizations : createAffiliatedOrganizations(affDetailsDto, target.getId())) {
                    careCoordinationOrganizationDaoCustom.addAffiliatedDetails(affiliatedOrganizations);
                    newAffiliatedOrganizationList.add(affiliatedOrganizations);
                    if (isNew || !findInList(affiliatedOrganizations, oldAffiliatedOrganizationList)) {
                        sendAffiliatedOrganizationNotification(affiliatedOrganizations, oldAffiliatedOrganizationList, organization, sentToDatabaseIds, true);
                    }
                }
            }
        }
        if (!isNew) {
            sentToDatabaseIds.clear();
            for (AffiliatedOrganizations affOrg : oldAffiliatedOrganizationList) {
                if (!findInList(affOrg, newAffiliatedOrganizationList)) {
                    sendAffiliatedOrganizationNotification(affOrg, newAffiliatedOrganizationList, organization, sentToDatabaseIds, false);
                }
            }

            careTeamService.deleteCareTeamMembersAssociatedWithDeletedAffiliatedRelation();
        }

        organization.setId(target.getId());
        List<DatabasePasswordSettings> databasePasswordSettings = databasePasswordSettingsService.getOrganizationPasswordSettings(target.getId());
        if (org.springframework.util.CollectionUtils.isEmpty(databasePasswordSettings)) {
            databasePasswordSettingsService.createDefaultDatabasePasswordSettings(target.getId());
        }
        return organization;
    }

    private void sendAffiliatedOrganizationNotification(AffiliatedOrganizations affOrg, List<AffiliatedOrganizations> oldAffiliatedOrganizationList, OrganizationDto primaryOrganization, List<Long> sentToDatabaseIds, boolean add) {
        List<Employee> administrators = null;
        if (!sentToDatabaseIds.contains(affOrg.getAffiliatedDatabaseId())) {
            administrators = employeeDao.getAdministrators(affOrg.getAffiliatedDatabaseId());
            sentToDatabaseIds.add(affOrg.getAffiliatedDatabaseId());
        }
        String affOrgName = careCoordinationOrganizationDao.getOrganizationName(affOrg.getAffiliatedDatabaseId());
        if (affOrg.getAffiliatedOrganizationId()!=null) {
            sendAffiliatedOrganizationNotification(affOrg.getAffiliatedOrganizationId(), administrators, primaryOrganization, affOrgName, add);
        }
        else {
            List<CommunityListItemDto> communityList = communityCrudService.listDto(affOrg.getAffiliatedDatabaseId());
            for (CommunityListItemDto community : communityList) {
                if (!findInList(community.getId(), affOrg, oldAffiliatedOrganizationList)) {
                    sendAffiliatedOrganizationNotification(community.getId(), administrators, primaryOrganization, affOrgName, add);
                    administrators = null;
                }
            }
        }
    }

    private boolean findInList(Long communityId, AffiliatedOrganizations affOrg, List<AffiliatedOrganizations> affiliatedOrganizationList) {
        for (AffiliatedOrganizations ao : affiliatedOrganizationList) {
            if (ao.getAffiliatedDatabaseId().equals(affOrg.getAffiliatedDatabaseId()) && communityId.equals(ao.getAffiliatedOrganizationId())) {
                return true;
            }
        }
        return false;
    }



    private void sendAffiliatedOrganizationNotification(Long affOrgId, List<Employee> administrators, OrganizationDto primaryOrg, String affOrgName, boolean add) {
        if (administrators!=null) {
            for (Employee admin : administrators) {
                sendAffiliatedOrganizationNotification(admin, primaryOrg, affOrgName, add);
            }
        }

        List<Employee> communityAdministrators = employeeDao.getCommunityAdministrators(affOrgId);
        for (Employee communityAdmin : communityAdministrators) {
            sendAffiliatedOrganizationNotification(communityAdmin, primaryOrg, affOrgName,  add);
        }
    }


    private void sendAffiliatedOrganizationNotification(Employee recipient, OrganizationDto primaryOrg, String affOrgName, boolean add) {
        if (recipient.getPerson() != null) {
            String email = PersonService.getPersonTelecomValue(recipient.getPerson(), PersonTelecomCode.EMAIL);
            AffiliatedOrganizationNotificationDto dto = new AffiliatedOrganizationNotificationDto();
//            dto.setPrimaryComunity(comName);
            dto.setPrimaryOrganization(primaryOrg.getName());
            dto.setAffiliatedOrganization(affOrgName);
//            dto.setAffiliatedCommunity(affCommunityName);
            dto.setFullName(SecurityUtils.getAuthenticatedUser().getEmployee().getFullName());
            dto.setRecipientFullName(recipient.getFullName());
            dto.setEmail(email);
            dto.setLink(portalUrl+ "?startPage=care-coordination/templates/communities&orgId=" + primaryOrg.getId());
            if (add) {
                exchangeMailService.sendAddedAffiliatedOrganizationNotification(dto);
            }
            else {
                exchangeMailService.sendDeletedAffiliatedOrganizationNotification(dto);
            }
        }
    }

    private boolean findInList(AffiliatedOrganizations affiliatedOrganizations, List<AffiliatedOrganizations> affiliatedOrganizationList) {
        for (AffiliatedOrganizations affOrg: affiliatedOrganizationList) {
            if (affOrg.getAffiliatedOrganizationId()!=null && affOrg.getAffiliatedOrganizationId().equals(affiliatedOrganizations.getAffiliatedOrganizationId()) &&
                    affOrg.getAffiliatedDatabaseId().equals(affiliatedOrganizations.getAffiliatedDatabaseId())) {
                return true;

            }
            else if (affOrg.getAffiliatedOrganizationId()==null && affOrg.getAffiliatedDatabaseId().equals(affiliatedOrganizations.getAffiliatedDatabaseId())) {
                return true;
            }
        }
        return false;
    }

//    private boolean isDeletedAffiliatedOrganization(List<AffiliatedOrganizations> newAffiliatedOrganizationList, AffiliatedOrganizations oldAffiliatedOrganizations) {
//        for (AffiliatedOrganizations affOrg: newAffiliatedOrganizationList) {
//            if (affOrg.getAffiliatedOrganizationId()!=null && affOrg.getAffiliatedOrganizationId().equals(oldAffiliatedOrganizations.getAffiliatedOrganizationId()) &&
//                    affOrg.getAffiliatedDatabaseId().equals(oldAffiliatedOrganizations.getAffiliatedDatabaseId())) {
//                return false;
//
//            }
//            else if (affOrg.getAffiliatedOrganizationId()==null && affOrg.getAffiliatedDatabaseId().equals(oldAffiliatedOrganizations.getAffiliatedDatabaseId())) {
//                return false;
//            }
//        }
//        return true;
//    }

    private List<AffiliatedOrganizations> createAffiliatedOrganizations(OrganizationAffiliatedDetailsDto affDetailsDto, Long databaseId) {
        List<AffiliatedOrganizations> affiliatedOrganizationsList = new ArrayList<AffiliatedOrganizations>();
        for(Long communityId: affDetailsDto.getCommunityIds()) {
            for (Long affCommunityId : affDetailsDto.getAffCommunitiesIds()) {
                AffiliatedOrganizations affiliatedOrganizations = new AffiliatedOrganizations();
                affiliatedOrganizations.setPrimaryDatabaseId(databaseId);
                if (communityId != 0) {
                    affiliatedOrganizations.setPrimaryOrganizationId(communityId);
                }
                affiliatedOrganizations.setAffiliatedDatabaseId(affDetailsDto.getAffOrgId());
                if (affCommunityId != 0) {
                    affiliatedOrganizations.setAffiliatedOrganizationId(affCommunityId);
                }
                affiliatedOrganizationsList.add(affiliatedOrganizations);
            }
        }
        return  affiliatedOrganizationsList;
    }

    @Override
    public void deleteOrganization(Long organizationId) {
        careCoordinationOrganizationDao.delete(organizationId);
    }

    protected OrganizationDto transform(Database database) {
        if (database==null) return null;
        OrganizationDto dto = new OrganizationDto();
        dto.setId(database.getId());
        dto.setName(database.getName());
        dto.setOid(database.getOid());
        dto.setMainLogoPath(database.getMainLogoPath());
        dto.setCopyEventNotificationsForPatients(database.getCopyEventNotificationsForPatients());
        if (database.getAddressAndContacts()!=null) {
            dto.setPostalCode(database.getAddressAndContacts().getPostalCode());
            dto.setCity(database.getAddressAndContacts().getCity());
            dto.setStreet(database.getAddressAndContacts().getStreetAddress());
            Long stateId = database.getAddressAndContacts().getStateId();
            dto.setStateId(stateId);
            if (stateId != null) {
                dto.setState(CareCoordinationUtils.createKeyValueDto(stateService.get(stateId.longValue())));
            }
            dto.setPhone(database.getAddressAndContacts().getPhone());
            dto.setEmail(database.getAddressAndContacts().getEmail());
        }
        if (database.getSystemSetup()!=null) {
            dto.setLoginCompanyId(database.getSystemSetup().getLoginCompanyId());
        }
        dto.setCommunities(communityCrudService.listDto(database.getId()));
        return dto;
    }

//    protected List<OrganizationListItemDto> transform(List<Database> databaseList) {
//        List<OrganizationListItemDto> results = new ArrayList<OrganizationListItemDto>();
//        for (Database database: databaseList) {
//            if (database == null) return null;
//            OrganizationListItemDto dto = new OrganizationListItemDto();
//            dto.setId(database.getId());
//            dto.setName(database.getName());
////            dto.setCreatedAutomatically(database.getCreatedAutomatically());
//            dto.setLastModified(database.getLastModified());
//            dto.setCommunityCount(communityCrudService.getCommunityCountForDatabase(database.getId()));
//            results.add(dto);
//        }
//        return results;
//    }

    protected static void transform(OrganizationDto database, Database target) {
        if ((database==null) || (target==null)) return;
        target.setService(Boolean.FALSE);
        target.setName(database.getName());
        target.setEldermark(Boolean.TRUE);
        SourceDatabaseAddressAndContacts addressAndContacts = target.getAddressAndContacts();
        if (addressAndContacts==null) {
            addressAndContacts = new SourceDatabaseAddressAndContacts();
            target.setAddressAndContacts(addressAndContacts);
        }
        addressAndContacts.setEmail(database.getEmail());
        addressAndContacts.setPhone(database.getPhone());
        addressAndContacts.setCity(database.getCity());
        addressAndContacts.setPostalCode(database.getPostalCode());
        addressAndContacts.setStateId(database.getStateId());
        addressAndContacts.setStreetAddress(database.getStreet());
        target.setOid(database.getOid());
        target.setLastModified(new Date());
        target.setCopyEventNotificationsForPatients(database.getCopyEventNotificationsForPatients());
    }

    protected static void transformSystemSetup(OrganizationDto database, Database target) {
        if ((target.getSystemSetup()==null) && (database.getLoginCompanyId()!=null)) {
            target.setSystemSetup(new SystemSetup());
            target.getSystemSetup().setDatabaseId(target.getId());
        }
        if (target.getSystemSetup()!=null) {
            target.getSystemSetup().setLoginCompanyId(database.getLoginCompanyId());
        }
    }

    @Override
    public Boolean checkIfUnique(final OrganizationDto data) {

        Specification<Database> spec = new Specification<Database>() {
            @Override
            public Predicate toPredicate(Root<Database> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (data.getId()!=null) {
                    predicates.add(criteriaBuilder.notEqual(root.<Integer>get("id"), data.getId()));
                }
                if (StringUtils.isNotEmpty(data.getName())) {
                    predicates.add(criteriaBuilder.like(root.<String>get("name"), data.getName()));
                }
                if (StringUtils.isNotEmpty(data.getLoginCompanyId())) {
                    predicates.add(criteriaBuilder.equal(root.join("systemSetup").<String>get("loginCompanyId"), data.getLoginCompanyId()));
                }

                if (StringUtils.isNotEmpty(data.getOid())) {
                    predicates.add(criteriaBuilder.like(root.<String>get("oid"), data.getOid()));
                }


                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        return careCoordinationOrganizationDao.count(spec)==0;
    }


    public Long getOrCreateOrganizationFromSchema(com.scnsoft.eldermark.schema.Organization source) {
        List<Database> organizationsFound = careCoordinationOrganizationDao.searchOrganizationByOid(source.getID());
        if (organizationsFound.size()==0) {
            OrganizationDto dto = new OrganizationDto();
            dto.setOid(source.getID());
            dto.setEmail(source.getEmail());
            dto.setName(source.getName());
            dto.setPhone(source.getPhone());
            dto.setLoginCompanyId(generateLoginCompanyId(source.getName()));
            create(dto, true);

            sendAutoCreatedOrgNotification(dto.getName());

            return dto.getId();
            //  throw new NoResultException("Organization OID: "+source.getID() + "no found");
        }
        else if (organizationsFound.size()==1) {
            return organizationsFound.get(0).getId();
        } else {
            throw new NonUniqueResultException("Found more than 1 organization by OID "+source.getID());
        }

    }

    private void sendAutoCreatedOrgNotification(String orgName) {
        List<Employee> employees = careTeamRoleDao.getSuperAdministrators();
        for (Employee employee: employees) {
            String targetName = employee.getFullName();
            String toEmail = employee.getLoginName();
            exchangeMailService.sendNewOrgNotification(new NewOrgCreatedDto(orgName, targetName, orgManagementUrl, toEmail));
        }
    }

    private String generateLoginCompanyId(String companyName) {
        String tryCode = companyName.replaceAll("[^A-Za-z]+", "").toUpperCase();
        if (tryCode.length()<=10) {
            if (careCoordinationOrganizationDao.countOrganizationsByLoginCompanyId(tryCode)==0) return tryCode;
        }
        int commonCodeLength = Math.min(tryCode.length(), 10);
        tryCode = tryCode.substring(0,commonCodeLength);
        if (careCoordinationOrganizationDao.countOrganizationsByLoginCompanyId(tryCode)==0) return tryCode;
        commonCodeLength = Math.min(tryCode.length(), 8);
        int counter = 0;
        while (counter<100) {
            counter++;
            tryCode = tryCode.substring(0,commonCodeLength) + (counter<10?"0":"") + counter;
            if (careCoordinationOrganizationDao.countOrganizationsByLoginCompanyId(tryCode)==0) return tryCode;
        }

        throw new RuntimeException("More than 100 Similar oganizations. Organization not created");
    }

    @Override
    public List<GroupedAffiliatedOrganizationDto> getAffiliatedOrganizationsInfo(Long primaryDatabaseId) {
        return groupAffiliatedOrganizations(careCoordinationOrganizationDaoCustom.getAffiliatedOrganizationsInfo(primaryDatabaseId));
    }

    @Override
    public List<GroupedAffiliatedOrganizationDto> getPrimaryOrganizationsInfo(Long affiliatedDatabaseId) {
        return groupAffiliatedOrganizations(careCoordinationOrganizationDaoCustom.getPrimaryOrganizationsInfo(affiliatedDatabaseId));
    }


    //group orgs/communities that are affiliated for specific
    private List<GroupedAffiliatedOrganizationDto> groupAffiliatedOrganizations(List<AffiliatedOrganizationDto> affiliatedOrganizations) {
        List<GroupedAffiliatedOrganizationDto> result = new ArrayList<GroupedAffiliatedOrganizationDto>();
        List<AffiliatedOrganizationDto> processed = new ArrayList<AffiliatedOrganizationDto>();
        for (AffiliatedOrganizationDto affiliatedOrganization : affiliatedOrganizations) {
            if (processed.contains(affiliatedOrganization)) {
                continue;
            }
            GroupedAffiliatedOrganizationDto groupedAffiliatedOrganizationDto = createGroupedAffiliatedOrganizationDto(affiliatedOrganization);
            processed.add(affiliatedOrganization);

            for (AffiliatedOrganizationDto affiliatedOrganizationToGroup : affiliatedOrganizations) {
                if (processed.contains(affiliatedOrganizationToGroup)) {
                    continue;
                }
                // in case affiliated community is null we will group organizations
                if (CollectionUtils.isEmpty(groupedAffiliatedOrganizationDto.getAffiliatedCommunityNames()) && affiliatedOrganizationToGroup.getAffiliatedCommunityName() == null) {
                    //in case primary community is set we will group orgs for single community (would be 1 community in primary list)
                    if (CollectionUtils.isNotEmpty(groupedAffiliatedOrganizationDto.getPrimaryCommunityNames())
                            && affiliatedOrganizationToGroup.getPrimaryCommunityName() != null
                            //&& groupedAffiliatedOrganizationDto.getPrimaryCommunityNames().get(0).equals(affiliatedOrganizationToGroup.getPrimaryCommunityName())
                            && groupedAffiliatedOrganizationDto.getPrimaryOrganizationNames().get(0).equals(affiliatedOrganizationToGroup.getPrimaryOrganizationName())) {
                        groupedAffiliatedOrganizationDto = addGroupedAffiliatedOrganizationDto(groupedAffiliatedOrganizationDto, affiliatedOrganizationToGroup);
                        processed.add(affiliatedOrganizationToGroup);
                        continue;
                    }
                    //in case primary community is not set, we will group orgs for single org
                    if (CollectionUtils.isEmpty(groupedAffiliatedOrganizationDto.getPrimaryCommunityNames())
                            && affiliatedOrganizationToGroup.getPrimaryCommunityName() == null) {
                        groupedAffiliatedOrganizationDto = addGroupedAffiliatedOrganizationDto(groupedAffiliatedOrganizationDto, affiliatedOrganizationToGroup);
                        processed.add(affiliatedOrganizationToGroup);
                        continue;
                    }
                }
                //in case community is set we will group communities of specific organization
                if (CollectionUtils.isNotEmpty(groupedAffiliatedOrganizationDto.getAffiliatedCommunityNames()) && affiliatedOrganizationToGroup.getAffiliatedCommunityName() != null
                        && groupedAffiliatedOrganizationDto.getAffiliatedOrganizationNames().get(0).equals(affiliatedOrganizationToGroup.getAffiliatedOrganizationName())) {
                    //in case primary community is set we will group communities for all single community (would be 1 community in primary list)
                    if (CollectionUtils.isNotEmpty(groupedAffiliatedOrganizationDto.getPrimaryCommunityNames())
                            && affiliatedOrganizationToGroup.getPrimaryCommunityName() != null
                            //&& groupedAffiliatedOrganizationDto.getPrimaryCommunityNames().get(0).equals(affiliatedOrganizationToGroup.getPrimaryCommunityName())
                            && groupedAffiliatedOrganizationDto.getPrimaryOrganizationNames().get(0).equals(affiliatedOrganizationToGroup.getPrimaryOrganizationName())) {
                        groupedAffiliatedOrganizationDto = addGroupedAffiliatedOrganizationDto(groupedAffiliatedOrganizationDto, affiliatedOrganizationToGroup);
                        processed.add(affiliatedOrganizationToGroup);
                        continue;
                    }
                    //in case primary community is not set, we will group communities for single org
                    if (CollectionUtils.isEmpty(groupedAffiliatedOrganizationDto.getPrimaryCommunityNames())
                            && affiliatedOrganizationToGroup.getPrimaryCommunityName() == null) {
                        groupedAffiliatedOrganizationDto = addGroupedAffiliatedOrganizationDto(groupedAffiliatedOrganizationDto, affiliatedOrganizationToGroup);
                        processed.add(affiliatedOrganizationToGroup);
                        continue;
                    }
                }
            }
            result.add(groupedAffiliatedOrganizationDto);
        }
        return result;
    }



    private static GroupedAffiliatedOrganizationDto createGroupedAffiliatedOrganizationDto(AffiliatedOrganizationDto affiliatedOrganizationDto) {
        GroupedAffiliatedOrganizationDto groupedAffiliatedOrganizationDto = new GroupedAffiliatedOrganizationDto();
        groupedAffiliatedOrganizationDto.setAffiliatedCommunityNames(createAndAddIfNotNull(affiliatedOrganizationDto.getAffiliatedCommunityName()));
        groupedAffiliatedOrganizationDto.setAffiliatedOrganizationNames(createAndAddIfNotNull(affiliatedOrganizationDto.getAffiliatedOrganizationName()));
        groupedAffiliatedOrganizationDto.setPrimaryCommunityNames(createAndAddIfNotNull(affiliatedOrganizationDto.getPrimaryCommunityName()));
        groupedAffiliatedOrganizationDto.setPrimaryOrganizationNames(createAndAddIfNotNull(affiliatedOrganizationDto.getPrimaryOrganizationName()));
        return groupedAffiliatedOrganizationDto;
    }

    private static GroupedAffiliatedOrganizationDto addGroupedAffiliatedOrganizationDto(GroupedAffiliatedOrganizationDto groupedAffiliatedOrganizationDto, AffiliatedOrganizationDto affiliatedOrganizationDto) {
        if (affiliatedOrganizationDto.getAffiliatedCommunityName() != null
                && !groupedAffiliatedOrganizationDto.getAffiliatedCommunityNames().contains(affiliatedOrganizationDto.getAffiliatedCommunityName())) {
            groupedAffiliatedOrganizationDto.getAffiliatedCommunityNames().add(affiliatedOrganizationDto.getAffiliatedCommunityName());
        }
        if (affiliatedOrganizationDto.getAffiliatedOrganizationName() != null
                && !groupedAffiliatedOrganizationDto.getAffiliatedOrganizationNames().contains(affiliatedOrganizationDto.getAffiliatedOrganizationName())) {
            groupedAffiliatedOrganizationDto.getAffiliatedOrganizationNames().add(affiliatedOrganizationDto.getAffiliatedOrganizationName());
        }
        if (affiliatedOrganizationDto.getPrimaryCommunityName() != null
                && !groupedAffiliatedOrganizationDto.getPrimaryCommunityNames().contains(affiliatedOrganizationDto.getPrimaryCommunityName())) {
            groupedAffiliatedOrganizationDto.getPrimaryCommunityNames().add(affiliatedOrganizationDto.getPrimaryCommunityName());
        }
        if (affiliatedOrganizationDto.getPrimaryOrganizationName() != null
                && !groupedAffiliatedOrganizationDto.getPrimaryOrganizationNames().contains(affiliatedOrganizationDto.getPrimaryOrganizationName())) {
            groupedAffiliatedOrganizationDto.getPrimaryOrganizationNames().add(affiliatedOrganizationDto.getPrimaryOrganizationName());
        }
        return groupedAffiliatedOrganizationDto;
    }

    private static List<String> createAndAddIfNotNull(String value) {
        List<String> result = new ArrayList<String>();
        if (value != null) {
            result.add(value);
        }
        return result;
    }

    @Override
    public Set<Long> getPrimaryOrgIds(Long affiliatedOrgId) {
        return getPrimaryAffiliatedOrgIds(affiliatedOrgId, true);
    }

    @Override
    public Set<Long> getAffiliatedOrgIds(Long primaryOrgId) {
        return getPrimaryAffiliatedOrgIds(primaryOrgId, false);
    }

    private Set<Long> getPrimaryAffiliatedOrgIds(Long orgId, Boolean searchPrimary) {
        Set<Long> result = new HashSet<Long>();
        Set<Long> dbs = new HashSet<Long>();
        dbs.add(orgId);
        List<Pair<Long,String>> affil = searchPrimary ? careCoordinationOrganizationDaoCustom.getPrimaryBriefList(dbs) : careCoordinationOrganizationDaoCustom.getAffiliatedBriefList(dbs);
        if (CollectionUtils.isNotEmpty(affil)) {
            for (Pair<Long,String> org : affil) {
                result.add(org.getFirst());
            }
        }
        return result;
    }

    @Override
    public void setCurrentOrganization(Long databaseId) {
        ExchangeUserDetails userDetails = SecurityUtils.getAuthenticatedUser();
        Set<Long> employeeDatabaseIds = userDetails.getCurrentAndLinkedDatabaseIds();
        if (!SecurityUtils.hasRole(CareTeamRoleCode.SUPER_ADMINISTRATOR) && !employeeDatabaseIds.contains(databaseId)
                &&!checkDatabaseAccess(databaseId,employeeDatabaseIds)) {
            throw new BusinessAccessDeniedException("You have no permission to access this organization!");
        }
        userDetails.setCurrentDatabaseId(careCoordinationOrganizationDao.findOne(databaseId));
    }
}
