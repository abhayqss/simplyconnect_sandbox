package com.scnsoft.eldermark.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.beans.ClientAccessType;
import com.scnsoft.eldermark.beans.ClientDeactivationReason;
import com.scnsoft.eldermark.beans.ClientFilter;
import com.scnsoft.eldermark.beans.ClientRecordSearchFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatClientFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.projection.*;
import com.scnsoft.eldermark.beans.reports.enums.GenderType;
import com.scnsoft.eldermark.beans.reports.enums.MaritalStatusType;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.dao.basic.evaluated.params.EvaluatedPropertyParams;
import com.scnsoft.eldermark.dao.basic.evaluated.params.FavouritePropertyParams;
import com.scnsoft.eldermark.dao.history.ClientHistoryDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.dao.specification.MergedClientViewSpecificationGenerator;
import com.scnsoft.eldermark.dto.AvatarUpdateData;
import com.scnsoft.eldermark.dto.clientactivation.ClientActivationDto;
import com.scnsoft.eldermark.dto.clientactivation.ClientDeactivationDto;
import com.scnsoft.eldermark.dto.hieconsentpolicy.ClientHieConsentPolicyData;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.client.ClientListInfo;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;
import com.scnsoft.eldermark.entity.client.ClientNameAndStatusAware;
import com.scnsoft.eldermark.entity.client.ClientPrimaryContactType;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyObtainedBy;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicySource;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.jms.producer.ClientUpdateQueueProducer;
import com.scnsoft.eldermark.projection.IsFavouriteEvaluatedAware;
import com.scnsoft.eldermark.service.pointclickcare.PointClickCarePatientMatchService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NonUniqueResultException;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Optional.ofNullable;

@Service
@Transactional
public class ClientServiceImpl implements ClientService, ClientHieConsentPolicyUpdateService {

    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);

    private static final String COMPREHENSIVE_ASSESSMENT_CODE = "COMPREHENSIVE";

    private static final Sort SORT_BY_NAME = Sort.by(Client_.FIRST_NAME, Client_.LAST_NAME);

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private ClientAssessmentDao clientAssessmentDao;

    @Autowired
    private ClientSpecificationGenerator clientSpecificationGenerator;

    @Autowired
    private AdmitIntakeClientDateDao admitIntakeResidentDateDao;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private PersonService personService;

    @Autowired
    private AdmittanceHistoryDao admittanceHistoryDao;

    @Autowired
    private ClientComprehensiveAssessmentDao clientComprehensiveAssessmentDao;

    @Autowired
    private CareTeamMemberViewableSettingsDao careTeamMemberViewableSettingsDao;

    @Autowired
    private MPIService mpiService;

    @Autowired
    private AffiliatedRelationshipService affiliatedRelationshipService;

    @Autowired
    private ClientUpdateQueueProducer clientUpdateQueueProducer;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientHistoryDao clientHistoryDao;

    @Autowired
    private MergedClientViewDao mergedClientViewDao;

    @Autowired
    private MergedClientViewSpecificationGenerator mergedClientViewSpecificationGenerator;

    @Autowired
    private EventTypeService eventTypeService;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private CommunityHieConsentPolicyService communityHieConsentPolicyService;

    @Autowired
    private MpiMergedClientsDao mpiMergedClientsDao;

    @Autowired(required = false)
    private PointClickCarePatientMatchService pointClickCarePatientMatchService;

    @Override
    @Transactional(readOnly = true)
    public <P> Page<P> find(ClientFilter filter, Pageable pageable, Class<P> projectionClass) {
        var byFilter = clientSpecificationGenerator.byFilter(filter);

        Map<String, EvaluatedPropertyParams> evaluatedPropertiesParams;
        if (filter.getFavouriteOfEmployeeIdHint() == null) {
            evaluatedPropertiesParams = Collections.emptyMap();
        } else {
            if (!IsFavouriteEvaluatedAware.class.isAssignableFrom(projectionClass)) {
                throw new ValidationException("Projection class should implement " + IsFavouriteEvaluatedAware.class.getSimpleName());
            }
            var favouritesParams = new FavouritePropertyParams(
                    filter.getFavouriteOfEmployeeIdHint(),
                    Client.class,
                    Client_.ADDED_AS_FAVOURITE_TO_EMPLOYEE_IDS
            );

            evaluatedPropertiesParams = Map.of(IsFavouriteEvaluatedAware.IS_FAVOURITE_PROPERTY_NAME, favouritesParams);
        }
        return clientDao.findAll(byFilter, projectionClass, evaluatedPropertiesParams, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientListInfo> findRecords(ClientRecordSearchFilter filter, PermissionFilter permissionFilter, Pageable pageable) {
        var byFilter = clientSpecificationGenerator.recordsSearch(filter);
        var hasRecordSearchAccess = clientSpecificationGenerator.hasRecordSearchAccess(permissionFilter);
        var notOptedOut = Specification.not(clientSpecificationGenerator.isOptOutPolicy());
        var clients = clientDao.findAll(byFilter.and(hasRecordSearchAccess.and(notOptedOut)), ClientListInfo.class, pageable);
        loggedUserService.addRecordSearchFoundClientIds(clients.stream().map(ClientListInfo::getId).collect(Collectors.toList()));
        return clients;
    }

    @Override
    @Transactional
    public Client save(Client client) {

        validateClient(client);

        avatarService.update(new AvatarUpdateData(client, client.getMultipartFile(), client.getShouldRemoveAvatar()));

        var clientEntity = clientDao.save(client);
        clientEntity = updateLegacyIds(clientEntity);
        clientUpdateQueueProducer.putToResidentUpdateQueue(clientEntity.getId(), ResidentUpdateType.RESIDENT);

        if (!mpiService.existsMPI(client)) {
            mpiService.createMPI(clientEntity);
        }

        updateClientAssessments(clientEntity);

        if (pointClickCarePatientMatchService != null && pointClickCarePatientMatchService.match(client)) {
            clientEntity = clientDao.save(clientEntity);
        }

        return clientEntity;
    }

    @Override
    public Client saveWithUpdateLegacyIdsIfEmpty(Client client) {
        validateClient(client);
        avatarService.update(new AvatarUpdateData(client, client.getMultipartFile(), client.getShouldRemoveAvatar()));
        var clientEntity = clientDao.save(client);
        clientEntity = updateLegacyIdsIfEmpty(clientEntity);
        clientUpdateQueueProducer.putToResidentUpdateQueue(clientEntity.getId(), ResidentUpdateType.RESIDENT);
        updateClientAssessments(clientEntity);
        if (pointClickCarePatientMatchService != null && pointClickCarePatientMatchService.match(client)) {
            clientEntity = clientDao.save(clientEntity);
        }
        return clientEntity;
    }

    private void updateClientAssessments(Client client) {
        var pharmacyName = client.getCurrentPharmacyName();
        if (Stream.of(pharmacyName, client.getPrimaryCarePhysicianFirstName(), client.getPrimaryCarePhysicianLastName()).anyMatch(StringUtils::isNotEmpty)) {
            var clientAssessments = client.getClientComprehensiveAssessments();
            if (CollectionUtils.isNotEmpty(clientAssessments)) {
                var assessmentsToUpdate = clientAssessments.stream()
                        .filter(it -> it.getClientAssessmentResult().getAssessmentStatus() == AssessmentStatus.IN_PROCESS)
                        .peek(it -> {
                            if (StringUtils.isNotEmpty(pharmacyName)) {
                                it.setPharmacyName(pharmacyName);
                            }
                        })
                        .peek(it -> {
                            if (StringUtils.isNotEmpty(client.getPrimaryCarePhysicianFirstName()) || StringUtils.isNotEmpty(client.getPrimaryCarePhysicianLastName())) {
                                it.setPrimaryCarePhysicianFirstName(client.getPrimaryCarePhysicianFirstName());
                                it.setPrimaryCarePhysicianLastName(client.getPrimaryCarePhysicianLastName());
                            }
                        })
                        .collect(Collectors.toList());

                clientComprehensiveAssessmentDao.saveAll(assessmentsToUpdate);
            }
        }
    }

    private Client updateLegacyIds(Client client) {
        boolean updateNeeded = CareCoordinationConstants.updateLegacyId(client);
        updateNeeded |= personService.updateLegacyId(client.getPerson());

        return updateNeeded ? clientDao.save(client) : client;
    }

    private Client updateLegacyIdsIfEmpty(Client client) {
        boolean updateNeeded = StringUtils.isEmpty(client.getLegacyId()) && CareCoordinationConstants.updateLegacyId(client);
        updateNeeded |= personService.updateLegacyId(client.getPerson());
        return updateNeeded ? clientDao.save(client) : client;
    }

    private void validateClient(Client client) {
        var violations = runValidation(client, true);

        if (!violations.isEmpty()) {
            throw new BusinessException(violations.iterator().next().getErrorMessage());
        }
    }

    @Override
    public Set<ClientValidationViolation> runValidation(Client client, boolean stopOnFirstViolation) {
        var violations = new HashSet<ClientValidationViolation>();

        if (!client.getOrganizationId().equals(client.getCommunity().getOrganizationId())) {
            violations.add(ClientValidationViolation.COMMUNITY);
            if (stopOnFirstViolation) {
                return violations;
            }
        }

        var clientEmail = Optional.ofNullable(client.getPerson().getTelecoms()).orElseGet(ArrayList::new).stream()
                .filter(personTelecom -> personTelecom.getUseCode() != null && personTelecom.getUseCode().equals(PersonTelecomCode.EMAIL.name())).findFirst();
        if (clientEmail.isPresent() && Boolean.FALSE.equals(isEmailUnique(client.getId(), client.getOrganizationId(), clientEmail.get().getValue()))) {
            violations.add(ClientValidationViolation.EMAIL);
            if (stopOnFirstViolation) {
                return violations;
            }
        }

        if (StringUtils.isNotEmpty(client.getMedicareNumber()) && Boolean.FALSE
                .equals(isMedicareNumberUnique(client.getId(), client.getCommunity().getId(), client.getMedicareNumber()))) {
            violations.add(ClientValidationViolation.MEDICARE_NUMBER);
            if (stopOnFirstViolation) {
                return violations;
            }
        }

        if (StringUtils.isNotEmpty(client.getMedicaidNumber()) && Boolean.FALSE
                .equals(isMedicaidNumberUnique(client.getId(), client.getCommunity().getId(), client.getMedicaidNumber()))) {
            violations.add(ClientValidationViolation.MEDICAID_NUMBER);
            if (stopOnFirstViolation) {
                return violations;
            }
        }

        if (Boolean.FALSE.equals(isValidSsn(client.getId(), client.getCommunity().getId(), client.getSocialSecurity()))) {
            violations.add(ClientValidationViolation.SSN);
            if (stopOnFirstViolation) {
                return violations;
            }
        }

        if (Boolean.TRUE.equals(client.getSharing())
                && Boolean.FALSE.equals(client.getCommunity().getIsSharingData())) {
            violations.add(ClientValidationViolation.SHARING);
            if (stopOnFirstViolation) {
                return violations;
            }
        }

        if (client.getBirthDate() != null && client.getBirthDate().isAfter(LocalDate.now())) {
            violations.add(ClientValidationViolation.BIRTH_DATE);
            if (stopOnFirstViolation) {
                return violations;
            }
        }

        return violations;
    }

    @Override
    @Transactional(readOnly = true)
    public Client getById(Long clientId) {
        return clientDao.getOne(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAllById(Iterable<Long> clientIds) {
        return clientDao.findAllById(clientIds);
    }

    @Override
    @Transactional(readOnly = true)
    public Client findById(Long clientId) {
        return clientDao.findById(clientId).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findAllMergedClientsIds(Long clientId) {
        var mergedClients = clientSpecificationGenerator.mergedClients(clientId);
        return CareCoordinationUtils.toIds(clientDao.findAll(mergedClients, IdAware.class), Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAllMergedClients(Client client) {
        if (client == null) {
            return Collections.emptyList();
        }
        return findAllMergedClients(client.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> findAllMergedClients(Long clientId) {
        var mergedClients = clientSpecificationGenerator.mergedClients(clientId);
        return clientDao.findAll(mergedClients);
    }

    @Override
    @Transactional
    public void unmerge(Long clientId) {
        mpiMergedClientsDao.unmerge(clientId);
    }

    @Override
    public List<Client> findAllByOrganizationAlternativeId(String orgAltId) {
        var byOrganizationAlternativeId = clientSpecificationGenerator.byCompanyAlternativeId(orgAltId);
        return clientDao.findAll(byOrganizationAlternativeId);
    }

    @Override
    public Optional<Client> findFirstMergedClientByOrganizationAlternativeId(Long clientId, String orgAltId) {
        var byOrganizationAlternativeId = clientSpecificationGenerator.byCompanyAlternativeId(orgAltId);
        var mergedClients = clientSpecificationGenerator.mergedClients(clientId);

        return clientDao.findFirst(byOrganizationAlternativeId.and(mergedClients), Client.class);
    }

    @Override
    public Map<Long, Set<Long>> findMergedClientIdsAmong(Collection<Long> forClientIds, Collection<Long> amongClientIds) {
        var mergedItems = mergedClientViewDao.findAll(
                mergedClientViewSpecificationGenerator.mergedClientIdsAmong(forClientIds, amongClientIds),
                ClientIdMergedClientIdAware.class
        );

        var map = new HashMap<Long, Set<Long>>();

        for (var item : mergedItems) {
            var mergedClientIds = map.computeIfAbsent(item.getClientId(), id -> new HashSet<>());
            mergedClientIds.add(item.getMergedClientId());
        }

        return map;
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findAllMergedClients(Collection<Long> clientIds, Class<T> projection) {
        var mergedClients = clientSpecificationGenerator.mergedClients(clientIds);
        return clientDao.findAll(mergedClients, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findAllMergedClientsEligibleForDiscovery(Collection<Long> clientIds, Class<T> projection) {
        var mergedClients = clientSpecificationGenerator.mergedClients(clientIds);
        var eligible = clientSpecificationGenerator.clientsInEligibleForDiscoveryCommunity();
        return clientDao.findAll(mergedClients.and(eligible), projection);
    }

    @Override
    public List<ClientListInfo> findAllMergedClientsListItems(Long clientId) {
        var mergedClients = clientSpecificationGenerator.mergedClients(clientId);
        return clientDao.findAll(mergedClients, ClientListInfo.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Long findClientOrganizationId(Long clientId) {
        return clientDao.findClientOrganizationId(clientId);
    }

    @Override
    public List<AdmitIntakeClientDate> findAdmitIntakeDates(Long clientId) {
        final Sort sort = Sort.by(Sort.Direction.DESC, AdmitIntakeClientDate_.ADMIT_INTAKE_DATE);
        return admitIntakeResidentDateDao.getAllByClientId(clientId, sort);
    }

    @Override
    public Boolean isValidSsn(Long clientId, Long communityId, String ssn) {
        if (StringUtils.isNotEmpty(ssn)) {
            if (clientId != null) {
                var ssnNotChanged = clientDao.existsByIdAndSocialSecurity(clientId, ssn);
                return ssnNotChanged || !clientDao.existsByIdNotAndCommunityIdAndSocialSecurity(clientId, communityId, ssn);
            }
            return !clientDao.existsByAndCommunityIdAndSocialSecurity(communityId, ssn);
        }
        return null;
    }

    @Override
    public Boolean isEmailUnique(Long clientId, Long organizationId, String email) {
        if (StringUtils.isNotEmpty(email)) {
            return Optional.ofNullable(clientId).map(id -> !clientDao.existsEmailInOrganizationAndIdNot(email, organizationId, id))
                    .orElse(!clientDao.existsEmailInOrganization(email, organizationId));
        }
        return null;
    }

    @Override
    public Boolean isMedicareNumberUnique(Long clientId, Long communityId, String medicareNumber) {
        if (StringUtils.isNotEmpty(medicareNumber)) {
            return Optional.ofNullable(clientId).map(id -> !clientDao.existsByIdNotAndCommunityIdAndMedicareNumber(id, communityId, medicareNumber))
                    .orElse(!clientDao.existsByAndCommunityIdAndMedicareNumber(communityId, medicareNumber));
        }
        return null;
    }

    @Override
    public Boolean isMedicaidNumberUnique(Long clientId, Long communityId, String medicaidNumber) {
        if (StringUtils.isNotEmpty(medicaidNumber)) {
            return Optional.ofNullable(clientId).map(id -> !clientDao.existsByIdNotAndCommunityIdAndMedicaidNumber(id, communityId, medicaidNumber))
                    .orElse(!clientDao.existsByCommunityIdAndMedicaidNumber(communityId, medicaidNumber));
        }
        return null;
    }

    @Override
    public Boolean isMemberNumberUnique(Long clientId, Long communityId, String memberNumber) {
        if (StringUtils.isNotEmpty(memberNumber)) {
            return Optional.ofNullable(clientId).map(id -> !clientDao.existsByIdNotAndCommunityIdAndMemberNumber(id, communityId, memberNumber))
                    .orElse(!clientDao.existsByAndCommunityIdAndMemberNumber(communityId, memberNumber));
        }
        return null;
    }

    @Override
    public Boolean wasManuallyCreated(Client client) {
        return CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE.equals(client.getLegacyTable());
    }

    @Override
    public List<AdmittanceHistory> findClientAdmittanceHistoryInCommunity(Long clientId, Long communityId) {
        return admittanceHistoryDao.findByClient_IdAndCommunityId(clientId, communityId);
    }

    @Override
    public void updateClientAccordingToComprehensiveAssessment(Long clientId, Long assessmentResultId) {
        ClientAssessmentResult clientAssessmentResult = clientAssessmentDao.getOne(assessmentResultId);
        if (COMPREHENSIVE_ASSESSMENT_CODE.equals(clientAssessmentResult.getAssessment().getCode())) {
            try {
                var comprehensiveAssessment = mapper.readValue(clientAssessmentResult.getResult(), ComprehensiveAssessment.class);
                Client client = clientAssessmentResult.getClient();

                boolean[] clientUpdated = {false};
                if (client.getGender() == null) {
                    convertGender(comprehensiveAssessment).ifPresent(ccdCode -> {
                        logger.info("[ClientServiceImpl] Gender CcdCode display name is {}", ccdCode.getDisplayName());
                        client.setGender(ccdCode);
                        clientDao.save(client);
                        clientUpdated[0] = true;
                    });
                }

                if (client.getMaritalStatus() == null) {
                    convertMaritalStatus(comprehensiveAssessment).ifPresent(ccdCode -> {
                        logger.info("[ClientServiceImpl] Marital status CcdCode display name is {}", ccdCode.getDisplayName());
                        client.setMaritalStatus(ccdCode);
                        clientDao.save(client);
                        clientUpdated[0] = true;
                    });
                }

                if (clientUpdated[0]) {
                    clientUpdateQueueProducer.putToResidentUpdateQueue(clientId, ResidentUpdateType.RESIDENT);
                }

                updateClientComprehensiveAssessment(clientAssessmentResult, comprehensiveAssessment, client);
            } catch (IOException ex) {
                logger.error("[ClientServiceImpl] Error parsing comprehensive json result : {}", clientAssessmentResult.getResult(), ex);
            }
        }
    }

    private void updateClientComprehensiveAssessment(
            ClientAssessmentResult clientAssessmentResult,
            ComprehensiveAssessment<?> comprehensiveAssessment,
            Client client
    ) {
        var cca = clientComprehensiveAssessmentDao.findByClientAssessmentResult_Id(clientAssessmentResult.getId())
                .orElseGet(() -> {
                    var newCca = new ClientComprehensiveAssessment();
                    newCca.setClientAssessmentResult(clientAssessmentResult);
                    newCca.setClientId(client.getId());
                    return newCca;
                });

        if (StringUtils.isNotEmpty(client.getPrimaryCarePhysicianFirstName()) || StringUtils.isNotEmpty(client.getPrimaryCarePhysicianLastName())) {
            cca.setPrimaryCarePhysicianFirstName(client.getPrimaryCarePhysicianFirstName());
            cca.setPrimaryCarePhysicianLastName(client.getPrimaryCarePhysicianLastName());
        } else {
            cca.setPrimaryCarePhysicianLastName(comprehensiveAssessment.getPrimaryCarePhysicianLastName());
            cca.setPrimaryCarePhysicianFirstName(comprehensiveAssessment.getPrimaryCarePhysicianFirstName());
        }

        if (StringUtils.isNotEmpty(client.getCurrentPharmacyName())) {
            cca.setPharmacyName(client.getCurrentPharmacyName());
        } else {
            cca.setPharmacyName(comprehensiveAssessment.getPharmacyName());
        }

        clientComprehensiveAssessmentDao.saveAndFlush(cca);
    }

    private Optional<CcdCode> convertMaritalStatus(ComprehensiveAssessment comprehensiveAssessment) {
        logger.info("[ClientServiceImpl] Marital status from assessment is {}", comprehensiveAssessment.getMaritalStatus());
        return ofNullable(comprehensiveAssessment.getMaritalStatus())
                .map(maritalStatus -> {
                    MaritalStatusType maritalStatusFromAssessment = MaritalStatusType.fromAssessmentValue(maritalStatus);
                    logger.info("[ClientServiceImpl] Marital status type matched: {}", maritalStatusFromAssessment);
                    return maritalStatusFromAssessment;
                })
                .map(maritalStatusType -> ccdCodeDao.getCcdCode(maritalStatusType.getCcdCode(), CodeSystem.MARITAL_STATUS.getOid()));
    }

    private Optional<CcdCode> convertGender(ComprehensiveAssessment comprehensiveAssessment) {
        logger.info("[ClientServiceImpl] Gender from assessment is {}", comprehensiveAssessment.getGender());
        return ofNullable(comprehensiveAssessment.getGender())
                .map(gender -> {
                    GenderType genderTypeFromAssessment = GenderType.fromAssessmentValue(gender);
                    logger.info("[ClientServiceImpl] Gender type matched: {}", genderTypeFromAssessment);
                    return genderTypeFromAssessment;
                })
                .map(genderType -> ccdCodeDao.getCcdCode(genderType.getCcdCode(), CodeSystem.ADMINISTRATIVE_GENDER.getOid()));
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findSortedByName(ClientFilter filter, Class<T> projectionClass) {
        var byFilter = clientSpecificationGenerator.byFilter(filter);
        return clientDao.findAll(byFilter, projectionClass, SORT_BY_NAME);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientNameAndStatusAware> findNoteClientNames(Long noteId) {
        var ofNote = clientSpecificationGenerator.ofNote(noteId);
        return clientDao.findAll(ofNote, ClientNameAndStatusAware.class, SORT_BY_NAME);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> findNotViewableEventTypeIds(Long employeeId, Long clientId) {
        var currentEmployeeRole = loggedUserService.getCurrentEmployee().getCareTeamRole().getCode();
        var notViewableIds = eventTypeService.findDisabledIdsByRoles(List.of(currentEmployeeRole));
        notViewableIds.addAll(careTeamMemberViewableSettingsDao.findNotViewableEventTypes(employeeId, clientId));
        return notViewableIds;
    }

    @Override
    @Transactional
    public void toggleStatus(Long clientId) {
        clientDao.toggleStatus(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existInCommunity(Long communityId) {
        return clientDao.existsByAndCommunityId(communityId);
    }

    @Override
    public boolean existOptedInInCommunity(Long communityId) {
        return clientDao.exists(
                Specification.not(clientSpecificationGenerator.isOptOutPolicy())
                        .and(clientSpecificationGenerator.byCommunityId(communityId))
        );
    }

    @Override
    public boolean existOptedInInOrganization(Long organizationId) {
        return clientDao.exists(
                Specification.not(clientSpecificationGenerator.isOptOutPolicy())
                        .and(clientSpecificationGenerator.byOrganizationId(organizationId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsCreatedByAnyInCommunityIds(Collection<Employee> employees, Collection<Long> communityIds) {
        var byCommunities = clientSpecificationGenerator.byCommunityIds(communityIds);
        var byCreators = clientSpecificationGenerator.byCreators(employees);
        return clientDao.exists(byCommunities.and(byCreators));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsCreatedByAnyInCommunityId(Collection<Employee> employees, Long communityId) {
        var byCommunityId = clientSpecificationGenerator.byCommunityId(communityId);
        var byCreators = clientSpecificationGenerator.byCreators(employees);
        return clientDao.exists(byCommunityId.and(byCreators));
    }

    @Override
    public boolean existsOptedInAndCreatedByAnyInCommunityId(Collection<Employee> employees, Long communityId) {
        var optedIn = clientSpecificationGenerator.isOptedIn();
        var byCommunityId = clientSpecificationGenerator.byCommunityId(communityId);
        var byCreators = clientSpecificationGenerator.byCreators(employees);
        return clientDao.exists(byCommunityId.and(byCreators.and(optedIn)));
    }

    @Override
    public boolean existsOptedInAndCreatedByAnyInOrganizationId(Collection<Employee> employees, Long organizationId) {
        var optedIn = clientSpecificationGenerator.isOptedIn();
        var byCommunityId = clientSpecificationGenerator.byOrganizationId(organizationId);
        var byCreators = clientSpecificationGenerator.byCreators(employees);
        return clientDao.exists(byCommunityId.and(byCreators.and(optedIn)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findCreatedByAnyInCommunityId(Collection<Employee> employees, Long communityId) {
        var ids = CareCoordinationUtils.toIdsSet(employees);

        var creatorIds = new HashSet<>(clientDao.findCreatorIdsAmongEmployeeIdsAndCommunityId(ids, communityId));

        return StreamUtils.stream(employees)
                .filter(employee -> creatorIds.contains(employee.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsCreatedByAnyInOrganization(Collection<Employee> employees, Long organizationId) {
        var byOrganizationId = clientSpecificationGenerator.byOrganizationId(organizationId);
        var byCreators = clientSpecificationGenerator.byCreators(employees);
        return clientDao.exists(byOrganizationId.and(byCreators));
    }

    @Override
    public Optional<Client> findByIdentityFields(Long communityId, String ssn, LocalDate dateOfBirth, String lastName, String firstName) {
        var byIdentityFields = clientSpecificationGenerator.byIdentityFields(communityId, ssn, dateOfBirth, lastName, firstName);
        var resultList = clientDao.findAll(byIdentityFields);
        if (resultList.size() > 1) {
            throw new NonUniqueResultException();
        }
        if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public ClientSecurityAwareEntity findSecurityAwareEntity(Long id) {
        return clientDao.findById(id, ClientSecurityAwareEntity.class).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientSecurityAwareEntity> findSecurityAwareEntities(Collection<Long> ids) {
        return clientDao.findByIdIn(ids, ClientSecurityAwareEntity.class);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isExistsAffiliatedCommunities(Long clientId) {
        var mergedClients = clientSpecificationGenerator.mergedClients(clientId);
        var communityIdAwares = clientDao.findAll(mergedClients, CommunityIdAware.class);
        return affiliatedRelationshipService.existsByPrimaryCommunityIdIn(communityIdAwares.stream().map(CommunityIdAware::getCommunityId).collect(Collectors.toList()));
    }

    @Override
    @Transactional(readOnly = true)
    public <P> P findById(Long id, Class<P> projection) {
        return clientDao.findById(id, projection).orElseThrow();
    }

    @Override
    @Transactional(readOnly = true)
    public <P> List<P> findAllById(Collection<Long> ids, Class<P> projection) {
        return clientDao.findByIdIn(ids, projection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientNameAndCommunityAware> findUnassociated(Long organizationId, PermissionFilter permissionFilter) {
        var clientFilter = new ClientFilter();
        clientFilter.setOrganizationId(organizationId);
        clientFilter.setPermissionFilter(permissionFilter);
        clientFilter.setClientAccessType(ClientAccessType.DETAILS);

        var byFilter = clientSpecificationGenerator.byFilter(clientFilter);
        var noAssociatedEmployee = clientSpecificationGenerator.associatedEmployeeIsNull();

        return clientDao.findAll(byFilter.and(noAssociatedEmployee), ClientNameAndCommunityAware.class, SORT_BY_NAME);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsChatAccessible(PermissionFilter permissionFilter, ConversationParticipatingAccessibilityFilter filter) {
        var chatAccessible = clientSpecificationGenerator.chatAccessibleClients(permissionFilter, filter.getExcludedEmployeeId());
        if (!filter.getIncludeNonAssociatedClients()) {
            chatAccessible = chatAccessible.and(clientSpecificationGenerator.hasActiveAssociatedEmployee());
        }
        if (filter.getExcludeOneToOneParticipants()) {
            chatAccessible = chatAccessible.and(clientSpecificationGenerator.excludeAssociatedParticipatingInOneToOneChatWithAny(permissionFilter.getAllEmployeeIds()));
        }

        return clientDao.exists(chatAccessible);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesWithAssociatedEmployeeIdsStatusAware> findChatAccessibleClients(PermissionFilter permissionFilter,
                                                                                       AccessibleChatClientFilter filter) {
        var chatAccessible = clientSpecificationGenerator.chatAccessibleClients(permissionFilter, filter.getExcludedEmployeeId());
        if (!filter.getIncludeNonAssociatedClients()) {
            chatAccessible = chatAccessible.and(clientSpecificationGenerator.hasActiveAssociatedEmployee());
        }
        if (filter.getExcludeParticipatingInOneToOne()) {
            chatAccessible = chatAccessible.and(clientSpecificationGenerator
                    .excludeAssociatedParticipatingInOneToOneChatWithAny(permissionFilter.getAllEmployeeIds()));
        }
        var byCommunityIds = clientSpecificationGenerator.byCommunityIds(filter.getCommunityIds());
        return clientDao.findAll(chatAccessible.and(byCommunityIds), IdNamesWithAssociatedEmployeeIdsStatusAware.class, SORT_BY_NAME);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdAware> findByLoginCompanyIdAndLegacyId(String loginCompanyId, String legacyId) {
        var byLegacyId = clientSpecificationGenerator.byLegacyId(legacyId);
        var byLoginCompanyId = clientSpecificationGenerator.byLoginCompanyId(loginCompanyId);
        var resultList = clientDao.findAll(byLegacyId.and(byLoginCompanyId), IdAware.class);
        if (resultList.size() > 1) {
            throw new NonUniqueResultException();
        }
        if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        }
        return Optional.empty();
    }

    @Override
    public Optional<IdAware> findByOrganizationAlternativeIdAndLegacyId(String organizationAlternativeId, String legacyId) {
        var byLegacyId = clientSpecificationGenerator.byLegacyId(legacyId);
        var byOrgAlternativeId = clientSpecificationGenerator.byCompanyAlternativeId(organizationAlternativeId);
        var resultList = clientDao.findAll(byLegacyId.and(byOrgAlternativeId), IdAware.class);
        if (resultList.size() > 1) {
            throw new NonUniqueResultException();
        }
        if (resultList.size() == 1) {
            return Optional.of(resultList.get(0));
        }
        return Optional.empty();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientNameAndCommunityAware> findAllByIds(Collection<Long> ids) {
        var byClientIds = clientSpecificationGenerator.byIds(ids);
        return clientDao.findAll(byClientIds, ClientNameAndCommunityAware.class, Sort.by(Client_.COMMUNITY_ID, Client_.FIRST_NAME, Client_.LAST_NAME));
    }

    @Override
    public void setFavourite(Long clientId, boolean favourite, Long requestedByEmployeeId) {
        var client = clientDao.findById(clientId).orElseThrow();
        var alreadyAddedAsFavourite = client.getAddedAsFavouriteToEmployeeIds().contains(requestedByEmployeeId);
        if (alreadyAddedAsFavourite != favourite) {
            if (favourite) {
                client.getAddedAsFavouriteToEmployeeIds().add(requestedByEmployeeId);
            } else {
                client.getAddedAsFavouriteToEmployeeIds().remove(requestedByEmployeeId);
            }
            clientDao.save(client);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Instant> resolveDeactivatedDate(IdActiveCreatedLastUpdatedAware client) {
        if (Boolean.TRUE.equals(client.getActive())) {
            return Optional.empty();
        }

        var latestDeactivatedInHistory = clientHistoryDao.findLatestStatusDateByClientId(false, client.getId());

        var updatedCreatedDate = Optional.ofNullable(client.getLastUpdated()).orElse(client.getCreatedDate());

        if (latestDeactivatedInHistory.isEmpty()) {
            //there are no 'deactivated' records in history, so current modification date is deactivation date
            return Optional.ofNullable(updatedCreatedDate);
        }

        var latestActivatedInHistory = clientHistoryDao.findLatestStatusDateByClientId(true, client.getId());
        return latestActivatedInHistory.map(activatedDate ->
                        latestDeactivatedInHistory.filter(deactivatedDate -> deactivatedDate.isAfter(activatedDate))
                                .orElse(updatedCreatedDate))
                .or(() -> latestDeactivatedInHistory);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdOrganizationIdActiveAware> findHealthPartnersClient(String healthPartnersMemberIdentifier,
                                                                          Long communityId) {
        return clientDao.findFirst(((root, criteriaQuery, criteriaBuilder) ->
                        criteriaBuilder.and(
                                criteriaBuilder.equal(root.get(Client_.healthPartnersMemberIdentifier), healthPartnersMemberIdentifier),
                                criteriaBuilder.equal(root.get(Client_.communityId), communityId))
                ),
                IdOrganizationIdActiveAware.class);
    }

    @Override
    @Transactional
    public void activateClient(Long clientId, ClientActivationDto dto) {
        clientDao.activateClient(
                clientId, Instant.ofEpochMilli(dto.getIntakeDate()), dto.getProgramType(), dto.getComment(), Instant.now()
        );
    }

    @Override
    @Transactional
    public void activateClient(Long clientId) {
        var now = Instant.now();
        clientDao.activateClient(clientId, now);
    }

    @Override
    public void deactivateClient(Long clientId, ClientDeactivationDto dto) {
        var clientDeactivationReason =
                ClientDeactivationReason.fromValue(dto.getDeactivationReason());
        clientDao.deactivateClient(
                clientId, Instant.ofEpochMilli(dto.getExitDate()), clientDeactivationReason, dto.getComment(), Instant.now()
        );
    }

    @Override
    public void deactivateClient(Long clientId) {
        var now = Instant.now();
        clientDao.deactivateClient(clientId, now);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateActive(Long clientId) {
        Objects.requireNonNull(clientId);
        var byId = clientSpecificationGenerator.byId(clientId);
        var isActive = clientSpecificationGenerator.isActive();
        if (!clientDao.exists(byId.and(isActive))) {
            throw new ValidationException("Client is not active");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void validateActive(Client client) {
       if (!Boolean.TRUE.equals(client.getActive())) {
            throw new ValidationException("Client is not active");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(PermissionFilter permissionFilter, Long organizationId, Boolean canRequestSignature) {
        var specification = clientSpecificationGenerator.hasDetailsAccess(permissionFilter);

        if (organizationId != null) {
            specification = specification.and(clientSpecificationGenerator.byOrganizationId(organizationId));
        }

        if (canRequestSignature != null) {
            var canRequestSignatureSpecification = clientSpecificationGenerator.hasPermissionToRequestSignatureFrom(permissionFilter);

            specification = specification.and(
                    BooleanUtils.isTrue(canRequestSignature)
                            ? canRequestSignatureSpecification
                            : Specification.not(canRequestSignatureSpecification)
            );
        }

        return clientDao.count(specification);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdsInOrganization(Collection<Long> clientIds, Long organizationId) {
        var byOrganizationId = clientSpecificationGenerator.byOrganizationId(organizationId);
        var byIds = clientSpecificationGenerator.byIds(clientIds);
        return clientDao.exists(byOrganizationId.and(byIds));
    }

    @Override
    @Transactional(readOnly = true)
    public void validateCareTeamMemberAsPrimaryContact(Long clientId, Long clientCareTeamMemberId) {
        if (clientId == null) {
            throw new ValidationException("Care team member can't be added as a primary contact when creating a client");
        }
        var clientCareTeamMember = clientCareTeamMemberService.findById(clientCareTeamMemberId).orElse(null);
        if (clientCareTeamMember == null || !clientCareTeamMember.getClientId().equals(clientId)
                || (clientCareTeamMember.getEmployee().getCareTeamRole().getCode() != CareTeamRoleCode.ROLE_PARENT_GUARDIAN
                && clientCareTeamMember.getEmployee().getCareTeamRole().getCode() != CareTeamRoleCode.ROLE_PERSON_RECEIVING_SERVICES)) {
            throw new ValidationException("Care team member is not suitable for being primary contact");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdNamesActiveAware> findWithPrimaryContact(Long employeeId) {
        var byPrimaryContactEmployeeId = clientSpecificationGenerator.byPrimaryContactEmployeeId(employeeId);
        return clientDao.findAll(byPrimaryContactEmployeeId, IdNamesActiveAware.class);
    }

    @Override
    public void updateHieConsentPolicyByClient(Client client, HieConsentPolicyType type, HieConsentPolicySource source, Employee author) {
        var data = new ClientHieConsentPolicyData();
        data.setSource(source);
        data.setObtainedBy(HieConsentPolicyObtainedBy.CLIENT);
        data.setUpdateDateTime(Instant.now());
        data.setAuthor(author);
        data.setObtainedFrom(client.getFullName());
        data.setType(type);

        updateHieConsentPolicy(client, data);

        clientDao.save(client);
    }

    @Override
    public void updateHieConsentPolicy(Client client, ClientHieConsentPolicyData data) {
        client.setHieConsentPolicySource(data.getSource());
        client.setHieConsentPolicyObtainedBy(data.getObtainedBy());
        client.setHieConsentPolicyUpdateDateTime(data.getUpdateDateTime());
        client.setHieConsentPolicyUpdatedByEmployeeId(Optional.ofNullable(data.getAuthor()).map(BasicEntity::getId).orElse(null));
        client.setHieConsentPolicyObtainedFrom(data.getObtainedFrom());
        client.setHieConsentPolicyType(data.getType());
        if (HieConsentPolicyType.OPT_OUT.equals(data.getType())) {
            var primaryContact = client.getPrimaryContact();
            if (primaryContact != null && !ClientPrimaryContactType.SELF.equals(primaryContact.getType())) {
                client.setPrimaryContact(null);
            }

            unmerge(client.getId());
        }
    }

    @Override
    @Transactional
    public void updateHieConsentPolicyWithDefaultCommunityPolicy(Long communityId, ClientHieConsentPolicyData data) {

        clientDao.updateHieConsentPolicyByCommunityIdAndUpdatedByIdIsNull(
                data.getType(),
                data.getSource(),
                data.getObtainedBy(),
                data.getObtainedFrom(),
                Instant.now(),
                communityId
        );

        mpiMergedClientsDao.unmergeByCommunityId(communityId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IdCommunityIdAssociatedEmployeeIdsAware> findWithCommunityHieConsentPolicy(Long communityId) {
        return clientDao.findAllIdCommunityIdAssociatedEmployeeIdsAware(
                clientSpecificationGenerator.byHieConsentPolicyUpdatedByEmployeeId(null)
                        .and(clientSpecificationGenerator.byCommunityId(communityId))
        );
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findClientsInCommunity(Long communityId, Class<T> projectionClass) {
        var byCommunityId = clientSpecificationGenerator.byCommunityId(communityId);

        return clientDao.findAll(byCommunityId, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findOptOutClientsInCommunities(Collection<Long> communityIds, Class<T> projectionClass) {
        var optOutPolicy = clientSpecificationGenerator.isOptOutPolicy();
        var byCommunityIds = clientSpecificationGenerator.byCommunityIds(communityIds);

        return clientDao.findAll(byCommunityIds.and(optOutPolicy), projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsOptOutClientsInCommunity(Long communityId) {
        var optOutPolicy = clientSpecificationGenerator.isOptOutPolicy();
        var byCommunityId = clientSpecificationGenerator.byCommunityId(communityId);

        return clientDao.exists(byCommunityId.and(optOutPolicy));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOptOutPolicy(HieConsentPolicyTypeAware client) {
        return client.getHieConsentPolicyType() == HieConsentPolicyType.OPT_OUT;
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findAllByCommunityId(Long communityId, Class<T> projectionClass) {
        return clientDao.findAll(clientSpecificationGenerator.byCommunityId(communityId), projectionClass);
    }

    @Override
    public <P> List<P> find(Specification<Client> clientSpecification, Class<P> projectionClass) {
        return clientDao.findAll(clientSpecification, projectionClass);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasConfirmedHieConsentPolicy(Client client) {
        return client.getHieConsentPolicyUpdatedByEmployeeId() != null
                && communityHieConsentPolicyService.isClientHieConsentPolicyNewerThenCommunityPolicy(client);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getNotOptedOutClientIds(Collection<Long> clientIds) {
        if (!CollectionUtils.isEmpty(clientIds)) {
            var specification = clientSpecificationGenerator.byIds(clientIds)
                    .and(Specification.not(clientSpecificationGenerator.isOptOutPolicy()));
            return clientDao.findAll(specification, IdAware.class).stream()
                    .map(IdAware::getId)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
