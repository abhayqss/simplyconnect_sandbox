package com.scnsoft.eldermark.facade;

import com.scnsoft.eldermark.beans.ClientFilter;
import com.scnsoft.eldermark.beans.ClientRecordSearchFilter;
import com.scnsoft.eldermark.beans.PharmacyFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatClientFilter;
import com.scnsoft.eldermark.beans.projection.DirectoryClientListItemFieldsAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.PersonTelecomsAware;
import com.scnsoft.eldermark.beans.reports.model.ComprehensiveAssessment;
import com.scnsoft.eldermark.beans.reports.model.NorCalComprehensiveAssessmentHouseHoldMembers;
import com.scnsoft.eldermark.beans.reports.model.assessment.NorCalComprehensiveAssessment;
import com.scnsoft.eldermark.beans.security.projection.PharmacyNameAware;
import com.scnsoft.eldermark.beans.security.projection.dto.ClientSecurityFieldsAwareImpl;
import com.scnsoft.eldermark.converter.assessment.AssessmentResultDefaultDtoAggregator;
import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.converter.entity2dto.household.HouseholdMemberItemDtoConverter;
import com.scnsoft.eldermark.converter.entity2dto.organization.EmergencyContactItemDtoConverter;
import com.scnsoft.eldermark.converter.entity2dto.organization.MedicalContactDtoConverter;
import com.scnsoft.eldermark.dto.*;
import com.scnsoft.eldermark.dto.assessment.AssessmentDefaultsDto;
import com.scnsoft.eldermark.dto.client.EmergencyContactListItemDto;
import com.scnsoft.eldermark.dto.client.HouseHoldMemberListItemDto;
import com.scnsoft.eldermark.dto.client.MedicalContactDto;
import com.scnsoft.eldermark.dto.client.ProspectivePrimaryContactDto;
import com.scnsoft.eldermark.dto.clientactivation.ClientActivationDto;
import com.scnsoft.eldermark.dto.clientactivation.ClientDeactivationDto;
import com.scnsoft.eldermark.dto.conversation.ConversationClientListItemDto;
import com.scnsoft.eldermark.dto.filter.ClientFilterDto;
import com.scnsoft.eldermark.dto.hieconsentpolicy.ClientHieConsentPolicyData;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.client.ClientCareTeamMember;
import com.scnsoft.eldermark.entity.client.ClientListInfo;
import com.scnsoft.eldermark.entity.client.ClientNameCommunityOrganizationAware;
import com.scnsoft.eldermark.entity.client.ClientPharmacyFilterView;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.exception.BusinessExceptionType;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.*;
import com.scnsoft.eldermark.service.client.SecuredClientProperty;
import com.scnsoft.eldermark.service.security.ClientSecurityService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.service.security.PermissionFilterService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import com.scnsoft.eldermark.util.PermissionFilterUtils;
import com.scnsoft.eldermark.util.StreamUtils;
import com.scnsoft.eldermark.utils.PersonTelecomUtils;
import com.scnsoft.eldermark.web.commons.utils.PaginationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ClientFacadeImpl implements ClientFacade {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ListAndItemConverter<ClientListInfo, ClientListItemDto> clientListItemDtoConverter;

    @Autowired
    private Converter<Client, ClientDto> clientDtoConverter;

    @Autowired
    private Converter<ClientDto, Client> clientEntityConverter;

    @Autowired
    private Converter<ClientEssentialsDto, Client> clientEssentialsEntityConverter;

    @Autowired
    private Converter<ClientFilterDto, ClientFilter> clientFilterConverter;

    @Autowired
    private Converter<ClientDto, ClientHieConsentPolicyData> clientHieConsentPolicyDataConverter;

    @Autowired
    private ClientCareTeamMemberService clientCareTeamMemberService;

    @Autowired
    private ListAndItemConverter<ClientCareTeamMember, EmergencyContactListItemDto> emergencyContactListDtoConverter;

    @Autowired
    private PermissionFilterService permissionFilterService;

    @Autowired
    private ClientSecurityService clientSecurityService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientAssessmentResultService clientAssessmentResultService;

    @Autowired
    private EmergencyContactItemDtoConverter emergencyContactItemDtoConverter;

    @Autowired
    private ClientPharmacyService clientPharmacyService;

    @Autowired
    private MedicalContactService medicalContactService;

    @Autowired
    private MedicalContactDtoConverter medicalContactDtoConverter;

    @Autowired
    private HouseholdMemberItemDtoConverter householdMemberItemDtoConverter;

    @Autowired
    private ClientPharmacyFilterViewService clientPharmacyFilterViewService;

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private List<AssessmentResultDefaultDtoAggregator<? extends AssessmentDefaultsDto>> assessmentDefaultsAggregators;

    @Autowired
    private HieConsentPolicyUpdateService hieConsentPolicyUpdateService;

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canViewList()")
    public Page<ClientListItemDto> find(ClientFilterDto filter, Pageable pageable) {
        var clientFilter = Objects.requireNonNull(clientFilterConverter.convert(filter));

        var clients = clientService.find(
                clientFilter,
                PaginationUtils.applyEntitySort(pageable, ClientListItemDto.class),
                ClientListInfo.class
        );

        return new PageImpl<>(
                clientListItemDtoConverter.convertList(clients.getContent()),
                pageable,
                clients.getTotalElements()
        );
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canViewRecordSearchList()")
    public Page<ClientListItemDto> findRecords(ClientRecordSearchFilter clientFilter, Pageable pageable) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientService.findRecords(clientFilter, permissionFilter, PaginationUtils.applyEntitySort(pageable, ClientListItemDto.class))
                .map(clientListItemDtoConverter::convert);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canViewList()")
    public List<DirectoryClientListItemDto> findClientsWithNonBlankNames(ClientFilterDto filter) {
        var clientFilter = clientFilterConverter.convert(filter);
        return clientService.findSortedByName(clientFilter, DirectoryClientListItemFieldsAware.class).stream()
                .filter(client -> StringUtils.isNotBlank(client.getFirstName()) && StringUtils.isNotBlank(client.getLastName()))
                .map(client -> new DirectoryClientListItemDto(
                        client.getId(),
                        client.getFirstName(),
                        client.getLastName(),
                        client.getFullName(),
                        client.getCommunityId(),
                        client.getHieConsentPolicyType(),
                        client.getPrimaryContactType()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canViewList()")
    public List<ClientNameDto> findNamesWithoutRecordSearchPermissions(ClientFilterDto filter) {
        var clientFilter = Objects.requireNonNull(clientFilterConverter.convert(filter));
        clientFilter.setPermissionFilter(
                PermissionFilterUtils.excludePermissions(
                        clientFilter.getPermissionFilter(),
                        Permission.EVENT_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH,
                        Permission.NOTE_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH
                )
        );
        return findNames(clientFilter);
    }

    private List<ClientNameDto> findNames(ClientFilter clientFilter) {
        return clientService.findSortedByName(clientFilter, IdNamesAware.class)
                .stream()
                .map(clientBasic -> new ClientNameDto(
                        clientBasic.getId(),
                        clientBasic.getFirstName(),
                        clientBasic.getLastName(),
                        clientBasic.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public ClientDto findById(@P("clientId") Long clientId) {
        Client client = clientService.findById(clientId);
        if (client != null) {
            var clientDto = clientDtoConverter.convert(client);
            Objects.requireNonNull(clientDto).setPharmacies(populatePharmacy(client));
            return clientDto;
        } else
            return null;
    }

    @Override
    @Transactional
    @PreAuthorize("@clientSecurityService.canAdd(#clientDto)")
    public Long add(@P("clientDto") ClientDto clientDto) {
        return save(clientDto);
    }

    @Override
    @Transactional
    @PreAuthorize("@clientSecurityService.canEdit(#clientDto.id)")
    public Long edit(@P("clientDto") ClientDto clientDto) {
        return save(clientDto);
    }

    @Override
    @Transactional
    @PreAuthorize("@clientSecurityService.canEdit(#clientDto.id)")
    public Long editEssentials(ClientEssentialsDto clientDto) {
        var client = clientEssentialsEntityConverter.convert(clientDto);
        return clientService.save(client).getId();
    }

    private Long save(ClientDto clientDto) {
        //todo too many business logic in facade level, should be moved to service layer

        if (clientDto.getId() != null) {
            clientService.validateActive(clientDto.getId());
        }

        Client client = Objects.requireNonNull(clientEntityConverter.convert(clientDto));

        if (clientService.wasManuallyCreated(client)
                && StringUtils.isEmpty(client.getSocialSecurity())
                && StringUtils.isEmpty(client.getMedicareNumber())
                && StringUtils.isEmpty(client.getMedicaidNumber())
        ) {
            throw new ValidationException("Medicare number or Medicaid number is required");
        }

        if (clientDto.getPrimaryContact() != null && clientDto.getPrimaryContact().getCareTeamMemberId() != null) {
            clientService.validateCareTeamMemberAsPrimaryContact(clientDto.getId(), clientDto.getPrimaryContact().getCareTeamMemberId());
        }

        if (isHieConsentPolicyDataChanged(clientDto, client)) {
            var hieConsentPolicyData = clientHieConsentPolicyDataConverter.convert(clientDto);
            hieConsentPolicyUpdateService.updateHieConsentPolicyByStaff(client, hieConsentPolicyData);
        }

        return clientService.save(client).getId();
    }

    private boolean isHieConsentPolicyDataChanged(ClientDto clientDto, Client client) {
        return !Objects.equals(clientDto.getHieConsentPolicyObtainedBy(), client.getHieConsentPolicyObtainedBy())
                || !Objects.equals(clientDto.getHieConsentPolicyObtainedFrom(), client.getHieConsentPolicyObtainedFrom())
                || !Objects.equals(clientDto.getHieConsentPolicyName(), client.getHieConsentPolicyType())
                || !Objects.equals(DateTimeUtils.toInstant(clientDto.getHieConsentPolicyObtainedDate()), client.getHieConsentPolicyUpdateDateTime());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId) && @clientAssessmentResultSecurityService.canViewList()")
    public List<EmergencyContactListItemDto> findEmergencyContacts(Long clientId, Pageable pageable) {
        var emergencyContactDtosByClientCareTeams = findEmergencyContactsByClientCareTeams(clientId, pageable);
        emergencyContactDtosByClientCareTeams.addAll(
                emergencyContactItemDtoConverter.convert(getComprehensiveAssessment(clientId, clientAssessmentResultService::hasEmergencyContactData).orElse(null))
        );
        emergencyContactDtosByClientCareTeams.addAll(
                emergencyContactItemDtoConverter.convert(getNorCalComprehensiveAssessment(clientId, clientAssessmentResultService::hasEmergencyContactData).orElse(null))
        );
        return emergencyContactDtosByClientCareTeams;
    }

    private List<EmergencyContactListItemDto> findEmergencyContactsByClientCareTeams(Long clientId, Pageable pageable) {
        //do we need to pass permissionFilter as well?
        var clientCareTeams = clientCareTeamMemberService.findEmergencyContacts(clientId).stream()
                .filter(StreamUtils.distinctByKey(ClientCareTeamMember::getEmployee)).collect(Collectors.toList());
        return emergencyContactListDtoConverter.convertList(clientCareTeams);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public List<MedicalContactDto> findMedicalContacts(Long clientId) {
        Client client = clientService.findById(clientId);
        if (client != null) {
            var assessment = getComprehensiveAssessment(clientId, clientAssessmentResultService::hasMedicalContactData).orElse(null);
            var contactWithRoles = medicalContactService.findMedicalContactsByClientId(clientId);
            return medicalContactDtoConverter.convert(client, assessment, contactWithRoles);
        }
        return Collections.emptyList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canAdd(Long organizationId) {
        return clientSecurityService.canAdd(new ClientSecurityFieldsAwareImpl(organizationId, ClientSecurityService.ANY_TARGET_COMMUNITY));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#clientId == null ? " +
            "@clientSecurityService.canAdd(new com.scnsoft.eldermark.beans.security.projection.dto.ClientSecurityFieldsAwareImpl(null, #communityId)) : " +
            "@clientSecurityService.canEdit(#clientId)")
    public ClientCommunityUniquenessDto validateUniqueInCommunity(@P("clientId") Long clientId, @P("communityId") Long communityId,
                                                                  String ssn, String medicareNumber,
                                                                  String medicaidNumber, String memberNumber) {
        Boolean ssnUnique = clientService.isValidSsn(clientId, communityId, ssn);
        Boolean medicareNumberUnique = clientService.isMedicareNumberUnique(clientId, communityId, medicareNumber);
        Boolean medicaidNumberUnique = clientService.isMedicaidNumberUnique(clientId, communityId, medicaidNumber);
        Boolean memberNumberUnique = clientService.isMemberNumberUnique(clientId, communityId, memberNumber);
        return new ClientCommunityUniquenessDto(ssnUnique, medicareNumberUnique, medicaidNumberUnique, memberNumberUnique);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("#clientId == null ? " +
            "@clientSecurityService.canAdd(new com.scnsoft.eldermark.beans.security.projection.dto.ClientSecurityFieldsAwareImpl(#organizationId, @clientSecurityService.ANY_TARGET_COMMUNITY)) : " +
            "@clientSecurityService.canEdit(#clientId)")
    public ClientOrganizationUniquenessDto validateUniqueInOrganization(@P("clientId") Long clientId, @P("organizationId") Long organizationId, String email) {
        Boolean emailUnique = clientService.isEmailUnique(clientId, organizationId, email);
        return new ClientOrganizationUniquenessDto(emailUnique);
    }

    @Override
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    @Transactional(readOnly = true)
    public List<Long> findNotViewableEventTypeIds(Long clientId) {
        Long employeeId = loggedUserService.getCurrentEmployeeId();
        return clientService.findNotViewableEventTypeIds(employeeId, clientId);
    }

    @Override
    @Transactional
    @PreAuthorize("@clientSecurityService.canEdit(#clientId)")
    public void toggleStatus(@P("clientId") Long clientId) {
        clientService.toggleStatus(clientId);
    }

    @Override
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    @Transactional(readOnly = true)
    public boolean isExistsAffiliatedCommunities(Long clientId) {
        return clientService.isExistsAffiliatedCommunities(clientId);
    }

    private Optional<ComprehensiveAssessment> getComprehensiveAssessment(Long clientId, Predicate<ComprehensiveAssessment> notEmptyData) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientAssessmentResultService.findLatestNotEmptyInProgressOrCompletedComprehensiveByClientIdWithMerged(clientId, permissionFilter, notEmptyData);
    }

    private Optional<NorCalComprehensiveAssessment> getNorCalComprehensiveAssessment(Long clientId, Predicate<NorCalComprehensiveAssessment> notEmptyData) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientAssessmentResultService.findLatestNotEmptyInProgressOrCompletedNorCalComprehensiveByClientIdWithMergedAndNotEmptyData(clientId, permissionFilter, notEmptyData);
    }

    private List<String> populatePharmacy(Client client) {
        Map<String, String> pharmacies = new HashMap<>();
        Optional.ofNullable(client.getCurrentPharmacyName())
                .filter(StringUtils::isNotEmpty)
                .map(String::trim)
                .ifPresent(n -> pharmacies.put(n, n));
        var assessmentPharmacy = getComprehensiveAssessment(client.getId(), clientAssessmentResultService::hasPharmacyData);
        assessmentPharmacy.ifPresent(ph -> {
            var data = CareCoordinationUtils.concat(", ",
                    CareCoordinationUtils.trimAndRemoveMultipleSpaces(ph.getPharmacyName()),
                    CareCoordinationUtils.trimAndRemoveMultipleSpaces(ph.getPharmacyPhoneNumber()),
                    CareCoordinationUtils.concat(" ",
                            CareCoordinationUtils.trimAndRemoveMultipleSpaces(ph.getPharmacyAddressStreet()),
                            CareCoordinationUtils.trimAndRemoveMultipleSpaces(ph.getPharmacyAddressCity()),
                            CareCoordinationUtils.trimAndRemoveMultipleSpaces(ph.getPharmacyAddressState()),
                            CareCoordinationUtils.trimAndRemoveMultipleSpaces(ph.getPharmacyAddressZipCode())));
            if (StringUtils.isNotEmpty(data)) {
                pharmacies.put(CareCoordinationUtils.trimAndRemoveMultipleSpaces(ph.getPharmacyName()), data);
            }
        });
        clientPharmacyService.findPharmaciesAsCommunitiesByClientId(client.getId())
                .forEach(c -> {
                    var data = CareCoordinationUtils.concat(", ",
                            CareCoordinationUtils.trimAndRemoveMultipleSpaces(c.getName()),
                            c.getPhone(),
                            CollectionUtils.isNotEmpty(c.getAddresses()) ? c.getAddresses().get(0).getDisplayAddress() : null);
                    if (StringUtils.isNotEmpty(data)) {
                        pharmacies.put(CareCoordinationUtils.trimAndRemoveMultipleSpaces(c.getName()), data);
                    }
                });
        return MapUtils.isNotEmpty(pharmacies) ? new ArrayList<>(pharmacies.values()) : null;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canViewList()")
    public List<String> findClientPharmacyNames(PharmacyFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientPharmacyFilterViewService.findClientPharmacyNames(permissionFilter, filter).stream()
                .map(PharmacyNameAware::getPharmacyName)
                .filter(s -> !ClientPharmacyFilterView.NO_PHARMACY.equals(s))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@clientSecurityService.canViewList()")
    @Transactional(readOnly = true)
    public List<ConversationClientListItemDto> findChatAccessibleClients(AccessibleChatClientFilter filter) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        filter.setExcludedEmployeeId(loggedUserService.getCurrentEmployeeId());

        return clientService.findChatAccessibleClients(permissionFilter, filter).stream()
                .map(clientData -> new ConversationClientListItemDto(clientData.getId(),
                        EmployeeStatus.ACTIVE == clientData.getAssociatedEmployeeStatus() ?
                                clientData.getAssociatedEmployeeId() : null,
                        clientData.getFullName()))
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("@clientSecurityService.canViewList()")
    @Transactional(readOnly = true)
    public List<ClientNameCommunityIdListItemDto> findUnassociated(Long organizationId) {
        return clientService.findUnassociated(organizationId, permissionFilterService.createPermissionFilterForCurrentUser())
                .stream()
                .map(clientNameAndCommunity -> new ClientNameCommunityIdListItemDto(clientNameAndCommunity.getId(),
                        clientNameAndCommunity.getFullName(), clientNameAndCommunity.getCommunityId(), clientNameAndCommunity.getCommunityName()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@chatSecurityService.existsConversationWithClient(#clientId)")
    public ClientNameCommunityOrganizationDto findChatClient(@P("clientId") Long clientId) {
        var client = clientService.findById(clientId, ClientNameCommunityOrganizationAware.class);

        return new ClientNameCommunityOrganizationDto(client.getId(), client.getFullName(),
                client.getCommunityId(), client.getCommunityName(),
                client.getOrganizationId(), client.getOrganizationName());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<IdAware> findByLoginCompanyIdAndLegacyId(String loginCompanyId, String legacyId) {
        return clientService.findByLoginCompanyIdAndLegacyId(loginCompanyId, legacyId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canView(Long clientId) {
        return clientSecurityService.canView(clientId);
    }

    @Override
    @PreAuthorize("@clientSecurityService.canViewList()")
    @Transactional(readOnly = true)
    public List<String> findInaccessibleClientProperties() {
        var accessibleSecuredProperties = EnumSet.copyOf(clientSecurityService.getAccessibleSecuredProperties());
        return EnumSet.complementOf(accessibleSecuredProperties).stream()
                .map(SecuredClientProperty::getName)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public List<HouseHoldMemberListItemDto> findHouseHoldMembers(Long clientId) {
        var norCalComprehensiveAssessment = getNorCalComprehensiveAssessmentHouseHoldMembers(clientId, clientAssessmentResultService::hasHouseholdMembersData).orElse(null);
        return householdMemberItemDtoConverter.convert(norCalComprehensiveAssessment);
    }

    private Optional<NorCalComprehensiveAssessmentHouseHoldMembers> getNorCalComprehensiveAssessmentHouseHoldMembers(
            Long clientId, Predicate<NorCalComprehensiveAssessmentHouseHoldMembers> notEmptyData
    ) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientAssessmentResultService.findLatestNotEmptyInProgressOrCompletedNorCalComprehensiveHouseHoldMembersByClientIdWithMerged(
                clientId, permissionFilter, notEmptyData
        );
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canEdit(#clientId)")
    public void activateClient(Long clientId, ClientActivationDto dto) {
        clientService.activateClient(clientId, dto);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canEdit(#clientId)")
    public void deactivateClient(Long clientId, ClientDeactivationDto dto) {
        clientService.deactivateClient(clientId, dto);
    }

    @Override
    @PreAuthorize("@clientAssessmentResultSecurityService.canAdd(#clientId, #assessmentTypeId)")
    @Transactional(readOnly = true)
    public AssessmentDefaultsDto assessmentDefaults(
            @P("clientId") Long clientId, @P("assessmentTypeId") Long assessmentTypeId, @P("assessmentId") Long parentAssessmentResultId
    ) {
        var assessment = assessmentService.findById(assessmentTypeId).orElseThrow();
        var client = clientService.findById(clientId);
        for (AssessmentResultDefaultDtoAggregator assessmentResultDefaultDtoAggregator : assessmentDefaultsAggregators) {
            if (assessmentResultDefaultDtoAggregator.getAssessmentShortName().equals(assessment.getShortName())) {
                return assessmentResultDefaultDtoAggregator.aggregateDefaults(client, parentAssessmentResultId);
            }
        }
        throw new BusinessException(BusinessExceptionType.ASSESSMENT_DEFAULT_DATA_NOT_ENABLED);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canViewList()")
    public Long count(Long organizationId, Boolean canRequestSignature) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        return clientService.count(permissionFilter, organizationId, canRequestSignature);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public List<ProspectivePrimaryContactDto> getProspectivePrimaryContacts(Long clientId) {
        var permissionFilter = permissionFilterService.createPermissionFilterForCurrentUser();
        var prospectivePrimaryContacts = clientCareTeamMemberService.findProspectivePrimaryContacts(permissionFilter, clientId);
        return prospectivePrimaryContacts.stream()
                .map(clientCareTeamMember -> {
                    var prospectivePrimaryContact = new ProspectivePrimaryContactDto();
                    prospectivePrimaryContact.setCareTeamMemberId(clientCareTeamMember.getId());
                    prospectivePrimaryContact.setFullName(clientCareTeamMember.getEmployee().getFullName());
                    prospectivePrimaryContact.setChatEnabled(clientCareTeamMember.getEmployee().getOrganization().isChatEnabled());
                    var email = PersonTelecomUtils.findValue(clientCareTeamMember.getEmployee().getPerson(), PersonTelecomCode.EMAIL).orElse(null);
                    prospectivePrimaryContact.setHasEmail(StringUtils.isNoneBlank(email));
                    return prospectivePrimaryContact;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canViewList()")
    public List<ClientNameBirthdayDto> findNonBlankNamesWithBirthdays(ClientFilterDto filter) {
        var clientFilter = clientFilterConverter.convert(filter);
        return clientService.findSortedByName(clientFilter, IdNamesBirthdayAware.class)
                .stream()
                .filter(client -> StringUtils.isNotBlank(client.getFirstName()) && StringUtils.isNotBlank(client.getLastName()))
                .map(clientBasic -> new ClientNameBirthdayDto(
                        clientBasic.getId(),
                        clientBasic.getFirstName(),
                        clientBasic.getLastName(),
                        clientBasic.getFullName(),
                        DateTimeUtils.formatLocalDate(clientBasic.getBirthDate())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("@clientSecurityService.canView(#clientId)")
    public ClientTelecomDto findTelecom(Long clientId) {
        var telecoms = Optional.ofNullable(clientService.findById(clientId, PersonTelecomsAware.class))
                .map(PersonTelecomsAware::getPersonTelecoms)
                .orElseThrow();
        var result = new ClientTelecomDto();
        result.setId(clientId);
        for (PersonTelecom telecom : telecoms) {
            if (PersonTelecomCode.EMAIL.name().equalsIgnoreCase(telecom.getUseCode())) {
                result.setEmail(telecom.getValue());
            }
            if (PersonTelecomCode.MC.name().equalsIgnoreCase(telecom.getUseCode())) {
                result.setCellPhone(telecom.getValue());
            }
        }
        return result;
    }
}
