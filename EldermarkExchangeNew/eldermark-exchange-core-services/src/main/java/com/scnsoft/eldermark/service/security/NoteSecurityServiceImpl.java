package com.scnsoft.eldermark.service.security;

import com.scnsoft.eldermark.beans.AffiliatedCareTeamType;
import com.scnsoft.eldermark.beans.HieConsentCareTeamType;
import com.scnsoft.eldermark.beans.projection.ClientIdAware;
import com.scnsoft.eldermark.beans.projection.CommunityIdAware;
import com.scnsoft.eldermark.beans.security.projection.dto.NoteSecurityFieldsAware;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.entity.security.Permission;
import com.scnsoft.eldermark.exception.ValidationException;
import com.scnsoft.eldermark.service.CommunityService;
import com.scnsoft.eldermark.service.EventService;
import com.scnsoft.eldermark.service.NoteService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.entity.security.Permission.*;

@Service("noteSecurityService")
@Transactional(readOnly = true)
//todo refactor to use security aware projections
public class NoteSecurityServiceImpl extends BaseSecurityService implements NoteSecurityService {

    private static final Set<Permission> VIEW_LIST_PERMISSIONS = EnumSet.of(
            NOTE_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT,
            NOTE_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION,
            NOTE_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY,
            NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION,
            NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY,
            NOTE_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM,
            NOTE_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM,
            NOTE_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF,
            NOTE_VIEW_MERGED_IF_SELF_CLIENT_RECORD,
            NOTE_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH
    );

    private static final String ASSESSMENT_NOTE_CODE = "ASSESSMENT_NOTE";

    @Autowired
    private NoteService noteService;

    @Autowired
    private EventService eventService;

    @Autowired
    private CommunityService communityService;

    @Override
    //in order to add - must have access to all clients
    public boolean canAdd(NoteSecurityFieldsAware dto) {
        if (CareCoordinationUtils.allNull(dto.getEventId(), dto.getClientId()) && CollectionUtils.isEmpty(dto.getClientIds())) {
            throw new ValidationException("Either eventId, clientId or clientIds must be provided");
        }

        var permissionFilter = currentUserFilter();

        if (isEventNote(dto) && !eventService.isViewableForAnyEmployeeIds(permissionFilter.getAllEmployeeIds(), dto.getEventId())) {
            return false;
        }

        var requestedClients = fetchClients(dto);

        if (isGroupNote(dto) &&
                (!permissionFilter.hasPermission(GROUP_NOTE_ADD_ALLOWED) || isAnyClientOptedOut(requestedClients))) {
            return false;
        }

        if (!requestedClients.stream()
                .map(CommunityIdAware::getCommunityId)
                .distinct()
                .allMatch(this::isEligibleForDiscoveryCommunity)) {
            return false;
        }

        if (permissionFilter.hasPermission(NOTE_ADD_ALL_EXCEPT_OPTED_OUT) && areAllClientsOptedIn(requestedClients)) {
            return true;
        }

        var allowedClients = new TreeSet<>(Comparator.comparingLong(ClientSecurityAwareEntity::getId));

        if (permissionFilter.hasPermission(NOTE_ADD_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_ASSOCIATED_ORGANIZATION);
            allowedClients.addAll(
                    requestedClients.stream()
                            .filter(client -> isAnyCreatedUnderOrganization(employees, client.getOrganizationId()))
                            .collect(Collectors.toList())
            );
            if (allowedClients.size() == requestedClients.size()) { //we can compare sizes only because allowed clients are populated from requested clients only
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_ASSOCIATED_COMMUNITY);
            requestedClients.stream()
                    .filter(client -> isAnyCreatedUnderCommunity(employees, client.getCommunityId()))
                    .forEach(allowedClients::add);

            if (allowedClients.size() == requestedClients.size()) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            requestedClients.stream()
                    .filter(this::isClientOptedIn)
                    .filter(client -> isAnyInAffiliatedOrganizationOfCommunity(employees, client.getCommunityId()))
                    .forEach(allowedClients::add);

            if (allowedClients.size() == requestedClients.size()) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            requestedClients.stream()
                    .filter(this::isClientOptedIn)
                    .filter(client -> isAnyInAffiliatedCommunity(employees, client.getCommunityId()))
                    .forEach(allowedClients::add);

            if (allowedClients.size() == requestedClients.size()) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(Permission.NOTE_ADD_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.NOTE_ADD_IF_CURRENT_RP_COMMUNITY_CTM);
            requestedClients.stream()
                    .filter(client -> isAnyInCommunityCareTeam(
                            employees,
                            client.getCommunityId(),
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.currentWithOptimizations(client)
                    ))
                    .forEach(allowedClients::add);

            if (allowedClients.size() == requestedClients.size()) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(Permission.NOTE_ADD_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(Permission.NOTE_ADD_IF_CURRENT_RP_CLIENT_CTM);
            requestedClients.stream()
                    .filter(client -> isAnyInClientCareTeam(
                            employees, client.getId(),
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.currentWithOptimizations(client)
                    ))
                    .forEach(allowedClients::add);

            if (allowedClients.size() == requestedClients.size()) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            requestedClients.stream()
                    .filter(client -> isClientOptedInAndAddedBySelf(employees, client))
                    .forEach(allowedClients::add);

            if (allowedClients.size() == requestedClients.size()) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_SELF_CLIENT_RECORD);
            requestedClients.stream()
                    .filter(client -> isSelfClientRecord(employees, client))
                    .forEach(allowedClients::add);

            if (allowedClients.size() == requestedClients.size()) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                permissionFilter.containsAnyClientRecordSearchFoundIds(CareCoordinationUtils.toIdsSet(requestedClients))) {
            requestedClients.stream()
                    .filter(client -> permissionFilter.containsClientRecordSearchFoundId(client.getId()))
                    .forEach(allowedClients::add);

            if (allowedClients.size() == requestedClients.size()) {
                return true;
            }
        }

        return false;
    }

    private Set<ClientSecurityAwareEntity> fetchClients(NoteSecurityFieldsAware dto) {
        var requestedClients = CareCoordinationUtils.<ClientSecurityAwareEntity>idsComparingSet();
        if (isEventNote(dto)) {
            var event = eventService.findById(dto.getEventId(), ClientIdAware.class);
            requestedClients.add(clientService.findSecurityAwareEntity(event.getClientId()));
            return requestedClients;
        }

        if (isGroupNote(dto)) {
            requestedClients.addAll(clientService.findSecurityAwareEntities(dto.getClientIds()));
            return requestedClients;
        }

        if (isClientNote(dto)) {
            requestedClients.add(clientService.findSecurityAwareEntity(dto.getClientId()));
            return requestedClients;
        }

        return requestedClients;
    }

    private boolean isEventNote(NoteSecurityFieldsAware dto) {
        return dto.getEventId() != null;
    }

    private boolean isGroupNote(NoteSecurityFieldsAware dto) {
        return dto.getEventId() == null && CollectionUtils.isNotEmpty(dto.getClientIds());
    }

    private boolean isClientNote(NoteSecurityFieldsAware dto) {
        return dto.getEventId() == null && CollectionUtils.isEmpty(dto.getClientIds());
    }

    @Override
    //there is at least one client for which user can add note
    public boolean canAddGroupNoteToCommunity(Long communityId) {
        if (!isEligibleForDiscoveryCommunity(communityId)) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (!permissionFilter.hasPermission(GROUP_NOTE_ADD_ALLOWED)) {
            return false;
        }

        var anyClientExistsInCommunity = Lazy.of(() -> clientService.existInCommunity(communityId));
        var anyOptedInClientExistsInCommunity = Lazy.of(() -> clientService.existOptedInInCommunity(communityId));

        if (permissionFilter.hasPermission(NOTE_ADD_ALL_EXCEPT_OPTED_OUT) && anyOptedInClientExistsInCommunity.get()) {
            return true;
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_ASSOCIATED_ORGANIZATION) && anyClientExistsInCommunity.get()) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_ASSOCIATED_ORGANIZATION);
            var community = communityService.findSecurityAwareEntity(communityId);

            if (isAnyCreatedUnderOrganization(employees, community.getOrganizationId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_ASSOCIATED_COMMUNITY) && anyClientExistsInCommunity.get()) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_ASSOCIATED_COMMUNITY);
            if (isAnyCreatedUnderCommunity(employees, communityId)) {
                return true;
            }
        }
        if (permissionFilter.hasPermission(NOTE_ADD_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION) && anyOptedInClientExistsInCommunity.get()) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            if (isAnyInAffiliatedOrganizationOfCommunity(employees, communityId)) {
                return true;
            }
        }
        if (permissionFilter.hasPermission(NOTE_ADD_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY) && anyOptedInClientExistsInCommunity.get()) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (isAnyInAffiliatedCommunity(employees, communityId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_CURRENT_RP_COMMUNITY_CTM);
            if (isAnyInCommunityCareTeam(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_CURRENT_RP_CLIENT_CTM);
            if (isAnyInAnyClientCareTeamOfCommunity(
                    employees,
                    communityId,
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.current(HieConsentCareTeamType.ANY_TARGET_CLIENT_ID))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (isAnyClientOptedInAndAddedBySelfInCommunity(employees, communityId)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_ADD_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(NOTE_ADD_IF_SELF_CLIENT_RECORD);
            if (existsSelfClientRecordInCommunity(employees, communityId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    //in order to edit - must have access to any client
    public boolean canEdit(Long noteId) {
        var note = noteService.findSecurityAwareEntity(noteId);

        var clients = clientService.findSecurityAwareEntities(note.resolveNoteClientIds());
        var clientCommunityIds = CareCoordinationUtils.getCommunityIdsSet(clients);

        if (!clientCommunityIds.stream().allMatch(this::isEligibleForDiscoveryCommunity)) {
            return false;
        }

        if (BooleanUtils.isFalse(note.getSubTypeManual())) {
            return false;
        }

        var permissionFilter = currentUserFilter();

        if (note.getEventId() != null &&
                !eventService.isViewableForAnyEmployeeIds(permissionFilter.getAllEmployeeIds(), note.getEventId())) {
            return false;
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_ALL_EXCEPT_OPTED_OUT)) {
            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_ALL_EXCEPT_OPTED_OUT);
            if (areAllClientsOptedIn(clients) && isSelfEmployeeRecord(employees, note.getEmployeeId())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_IF_ASSOCIATED_ORGANIZATION)) {
            var clientOrganizations = CareCoordinationUtils.getOrganizationIdsSet(clients);

            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_IF_ASSOCIATED_ORGANIZATION);
            if (isSelfEmployeeRecord(employees, note.getEmployeeId()) &&
                    isAnyCreatedUnderAnyOrganization(employees, clientOrganizations)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_IF_ASSOCIATED_COMMUNITY);
            if (isSelfEmployeeRecord(employees, note.getEmployeeId()) &&
                    isAnyCreatedUnderAnyCommunity(employees, clientCommunityIds)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);
            if (areAllClientsOptedIn(clients) &&
                    isSelfEmployeeRecord(employees, note.getEmployeeId()) &&
                    isAnyInAffiliatedOrganizationOfAnyCommunity(employees, clientCommunityIds)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (areAllClientsOptedIn(clients) &&
                    isSelfEmployeeRecord(employees, note.getEmployeeId()) &&
                    isAnyInAffiliatedCommunityOfAny(employees, clientCommunityIds)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_IF_CURRENT_RP_COMMUNITY_CTM);
            if (isSelfEmployeeRecord(employees, note.getEmployeeId()) &&
                    isAnyInAnyCommunityCareTeam(
                            employees,
                            clientCommunityIds,
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.currentForAny(clients))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_IF_CURRENT_RP_CLIENT_CTM);
            if (isSelfEmployeeRecord(employees, note.getEmployeeId()) &&
                    isAnyInAnyClientCareTeam(
                            employees,
                            clients,
                            AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                            HieConsentCareTeamType.currentForAny(clients))) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_OPTED_IN_IF_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_OPTED_IN_IF_ADDED_BY_SELF);
            if (areAllClientsOptedIn(clients) &&
                    isSelfEmployeeRecord(employees, note.getEmployeeId()) &&
                    isAnyClientAddedBySelf(employees, clients)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_IF_SELF_CLIENT_RECORD);
            if (isSelfEmployeeRecord(employees, note.getEmployeeId()) && isAnySelfClientRecord(employees, clients)) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_EDIT_ADDED_BY_SELF_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                permissionFilter.containsAnyClientRecordSearchFoundIds(CareCoordinationUtils.toIdsSet(clients))) {
            var employees = permissionFilter.getEmployees(NOTE_EDIT_ADDED_BY_SELF_IF_CLIENT_FOUND_IN_RECORD_SEARCH);
            if (isSelfEmployeeRecord(employees, note.getEmployeeId()) && isAnySelfClientRecord(employees, clients)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canViewList() {
        return hasAnyPermission(VIEW_LIST_PERMISSIONS);
    }

    @Override
    //in order to view - must have access to any client
    public boolean canView(Long noteId) {
        var permissionFilter = currentUserFilter();

        var note = noteService.findSecurityAwareEntity(noteId);

        if (note.getEventId() != null && !eventService.isViewableForAnyEmployeeIds(permissionFilter.getAllEmployeeIds(),
                note.getEventId())) {
            return false;
        }

        var clientIds = note.resolveNoteClientIds();
        var clients = clientService.findSecurityAwareEntities(clientIds)
                .stream()
                .filter(this::isInEligibleForDiscoveryCommunity)
                .collect(Collectors.toList());

        if (clients.isEmpty()) {
            return false;
        }

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_ALL_EXCEPT_OPTED_OUT) && isClientOptedIn(note)) {
            return true;
        }

        var mergedClients = lazyMergedSecurityClientsEligibleForDiscovery(clients);
        var mergedCommunities = lazyCommunityIdsSet(mergedClients);
        var mergedOrganizations = lazyOrganizationIdsSet(mergedClients);

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(NOTE_VIEW_MERGED_IF_ASSOCIATED_ORGANIZATION);

            if (isAnyCreatedUnderAnyOrganization(employees, mergedOrganizations.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(NOTE_VIEW_MERGED_IF_ASSOCIATED_COMMUNITY);

            if (isAnyCreatedUnderAnyCommunity(employees, mergedCommunities.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION)) {
            var employees = permissionFilter.getEmployees(NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_ORGANIZATION);

            if (isClientOptedIn(note) && isAnyInAffiliatedOrganizationOfAnyCommunity(employees, mergedCommunities.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY)) {
            var employees = permissionFilter.getEmployees(NOTE_VIEW_MERGED_OPTED_IN_IF_FROM_AFFILIATED_COMMUNITY);
            if (isClientOptedIn(note) && isAnyInAffiliatedCommunityOfAny(employees, mergedCommunities.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM)) {
            var employees = permissionFilter.getEmployees(NOTE_VIEW_MERGED_IF_CURRENT_RP_COMMUNITY_CTM);

            if (isAnyInAnyCommunityCareTeam(
                    employees,
                    mergedCommunities.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentForAny(mergedClients.get())
            )) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM)) {
            var employees = permissionFilter.getEmployees(NOTE_VIEW_MERGED_IF_CURRENT_RP_CLIENT_CTM);
            if (isAnyInAnyClientCareTeam(
                    employees,
                    mergedClients.get(),
                    AffiliatedCareTeamType.REGULAR_AND_PRIMARY,
                    HieConsentCareTeamType.currentForAny(mergedClients.get())
            )) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF)) {
            var employees = permissionFilter.getEmployees(NOTE_VIEW_MERGED_IF_OPTED_IN_CLIENT_ADDED_BY_SELF);
            if (areAllClientsOptedIn(mergedClients.get()) && isAnyClientAddedBySelf(employees, mergedClients.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_IF_SELF_CLIENT_RECORD)) {
            var employees = permissionFilter.getEmployees(NOTE_VIEW_MERGED_IF_SELF_CLIENT_RECORD);

            if (isAnySelfClientRecord(employees, mergedClients.get())) {
                return true;
            }
        }

        if (permissionFilter.hasPermission(NOTE_VIEW_MERGED_IF_CLIENT_FOUND_IN_RECORD_SEARCH) &&
                permissionFilter.containsAnyClientRecordSearchFoundIds(CareCoordinationUtils.toIdsSet(mergedClients.get()))) {
            return true;
        }

        return false;
    }

}
