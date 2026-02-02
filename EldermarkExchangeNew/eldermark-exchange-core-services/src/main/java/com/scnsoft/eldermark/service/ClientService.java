package com.scnsoft.eldermark.service;

import com.scnsoft.eldermark.beans.ClientFilter;
import com.scnsoft.eldermark.beans.ClientRecordSearchFilter;
import com.scnsoft.eldermark.beans.conversation.AccessibleChatClientFilter;
import com.scnsoft.eldermark.beans.conversation.ConversationParticipatingAccessibilityFilter;
import com.scnsoft.eldermark.beans.projection.IdActiveCreatedLastUpdatedAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.beans.projection.IdNamesActiveAware;
import com.scnsoft.eldermark.beans.projection.IdOrganizationIdActiveAware;
import com.scnsoft.eldermark.beans.security.PermissionFilter;
import com.scnsoft.eldermark.beans.security.projection.entity.ClientSecurityAwareEntity;
import com.scnsoft.eldermark.dto.clientactivation.ClientActivationDto;
import com.scnsoft.eldermark.dto.clientactivation.ClientDeactivationDto;
import com.scnsoft.eldermark.entity.AdmitIntakeClientDate;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.IdNamesWithAssociatedEmployeeIdsStatusAware;
import com.scnsoft.eldermark.entity.client.ClientListInfo;
import com.scnsoft.eldermark.entity.client.ClientNameAndCommunityAware;
import com.scnsoft.eldermark.entity.client.ClientNameAndStatusAware;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

public interface ClientService extends SecurityAwareEntityService<ClientSecurityAwareEntity, Long>,
        ProjectingService<Long>,
        OptOutPolicyCheckingClientService {

    Client save(Client client);

    Client saveWithUpdateLegacyIdsIfEmpty(Client client);

    <P> Page<P> find(ClientFilter filter, Pageable pageable, Class<P> projectionClass);

    Page<ClientListInfo> findRecords(ClientRecordSearchFilter filter, PermissionFilter permissionFilter, Pageable pageable);

    Set<ClientValidationViolation> runValidation(Client client, boolean stopOnFirstViolation);

    Client getById(Long clientId);

    List<Client> findAllById(Iterable<Long> clientIds);

    Client findById(Long clientId);

    List<Long> findAllMergedClientsIds(Long clientId);

    List<Client> findAllMergedClients(Client client);

    List<Client> findAllMergedClients(Long clientId);

    void unmerge(Long clientId);

    List<Client> findAllByOrganizationAlternativeId(String orgAltId);

    Optional<Client> findFirstMergedClientByOrganizationAlternativeId(Long clientId, String orgAltId);

    Map<Long, Set<Long>> findMergedClientIdsAmong(Collection<Long> forClientIds, Collection<Long> amongClientIds);

    <T> List<T> findAllMergedClients(Collection<Long> clientIds, Class<T> projection);

    <T> List<T> findAllMergedClientsEligibleForDiscovery(Collection<Long> clientIds, Class<T> projection);

    List<ClientListInfo> findAllMergedClientsListItems(Long clientId);

    Long findClientOrganizationId(Long clientId);

    List<AdmitIntakeClientDate> findAdmitIntakeDates(Long clientId);

    Boolean isValidSsn(Long clientId, Long communityId, String ssn);

    Boolean isEmailUnique(Long clientId, Long organizationId, String email);

    Boolean isMedicareNumberUnique(Long clientId, Long communityId, String medicareNumber);

    Boolean isMedicaidNumberUnique(Long clientId, Long communityId, String medicaidNumber);

    Boolean isMemberNumberUnique(Long clientId, Long communityId, String memberNumber);

    Boolean wasManuallyCreated(Client client);

    List<AdmittanceHistory> findClientAdmittanceHistoryInCommunity(Long clientId, Long communityId);

    void updateClientAccordingToComprehensiveAssessment(Long clientId, Long assessmentId);

    <T> List<T> findSortedByName(ClientFilter filter, Class<T> projectionClass);

    List<ClientNameAndStatusAware> findNoteClientNames(Long noteId);

    List<Long> findNotViewableEventTypeIds(Long employeeId, Long clientId);

    void toggleStatus(Long clientId);

    boolean existInCommunity(Long communityId);

    boolean existOptedInInCommunity(Long communityId);

    boolean existOptedInInOrganization(Long organizationId);

    boolean existsCreatedByAnyInCommunityIds(Collection<Employee> employees, Collection<Long> communityIds);

    boolean existsCreatedByAnyInCommunityId(Collection<Employee> employees, Long communityId);

    boolean existsOptedInAndCreatedByAnyInCommunityId(Collection<Employee> employees, Long communityId);

    boolean existsOptedInAndCreatedByAnyInOrganizationId(Collection<Employee> employees, Long organizationId);

    List<Employee> findCreatedByAnyInCommunityId(Collection<Employee> employees, Long communityId);

    boolean existsCreatedByAnyInOrganization(Collection<Employee> employees, Long organizationId);

    Optional<Client> findByIdentityFields(Long communityId, String ssn, LocalDate dateOfBirth, String lastName, String firstName);

    boolean isExistsAffiliatedCommunities(Long clientId);

    List<ClientNameAndCommunityAware> findUnassociated(Long organizationId, PermissionFilter permissionFilter);

    boolean existsChatAccessible(PermissionFilter permissionFilter, ConversationParticipatingAccessibilityFilter filter);

    List<IdNamesWithAssociatedEmployeeIdsStatusAware> findChatAccessibleClients(PermissionFilter permissionFilter,
                                                                                AccessibleChatClientFilter filter);

    Optional<IdAware> findByLoginCompanyIdAndLegacyId(String loginCompanyId, String legacyId);

    Optional<IdAware> findByOrganizationAlternativeIdAndLegacyId(String organizationAlternativeId, String legacyId);

    List<ClientNameAndCommunityAware> findAllByIds(Collection<Long> ids);

    void setFavourite(Long clientId, boolean favourite, Long requestedByEmployeeId);

    Optional<Instant> resolveDeactivatedDate(IdActiveCreatedLastUpdatedAware source);

    Optional<IdOrganizationIdActiveAware> findHealthPartnersClient(String healthPartnersMemberIdentifier, Long communityId);

    void activateClient(Long clientId, ClientActivationDto dto);

    void activateClient(Long clientId);

    void deactivateClient(Long clientId, ClientDeactivationDto dto);

    void deactivateClient(Long clientId);

    void validateActive(Long clientId);

    @Transactional(readOnly = true)
    void validateActive(Client client);

    Long count(PermissionFilter permissionFilter, Long organizationId, Boolean canRequestSignature);

    boolean existsByIdsInOrganization(Collection<Long> clientIds, Long organizationId);

    void validateCareTeamMemberAsPrimaryContact(Long clientId, Long clientCareTeamMemberId);

    List<IdNamesActiveAware> findWithPrimaryContact(Long employeeId);

    <P> List<P> find(Specification<Client> clientSpecification, Class<P> projectionClass);

    <T> List<T> findAllByCommunityId(Long id, Class<T> projectionClass);

    boolean hasConfirmedHieConsentPolicy(Client client);

    List<Long> getNotOptedOutClientIds(Collection<Long> clientIds);
}
