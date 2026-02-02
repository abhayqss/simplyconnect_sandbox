package com.scnsoft.eldermark.service.careteam.invitation;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.dao.basic.JpaUtils;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.exception.CareTeamInvitationClientHieConsentValidationException;
import com.scnsoft.eldermark.merger.service.ClientMatcherService;
import com.scnsoft.eldermark.merger.service.MatchResultEntry;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.MpiMergedClientsService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
public class ClientCareTeamInvitationClientResolverImpl implements ClientCareTeamInvitationClientResolver {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private MpiMergedClientsService mpiMergedClientsService;

    @Autowired
    private ClientMatcherService clientMatcherService;

    @Override
    public Client resolveClient(Long clientId) {
        return findExistingClient(clientService.getById(clientId))
                .orElseGet(() -> createFamilyAppMergedClient(clientId));
    }

    @Override
    public Optional<Client> findExistingClient(Client client) {
        if (isFromFamilyOrganization(client)) {
            if (clientService.isOptOutPolicy(client)) {
                throw new CareTeamInvitationClientHieConsentValidationException("You record at the Family Organization should be Opted in to invite Care team member", false);
            }
            return Optional.of(client);
        }

        if (clientService.isOptOutPolicy(client)) {
            throw new CareTeamInvitationClientHieConsentValidationException("You record at the " + client.getOrganization().getName() +
                    " should be Opted in to invite Care team member", false);
        }

        var mergedInFamilyApp = findMergedInFamilyApp(client.getId());
        if (mergedInFamilyApp.isPresent()) {
            return mergedInFamilyApp;
        }

        var allWithSameUniqueFieldsInFamilyApp = findWithSameUniqueFieldsInFamilyApp(client);
        if (!allWithSameUniqueFieldsInFamilyApp.isEmpty()) {
            var optedInWithSameUniqueFieldsInFamilyApp = allWithSameUniqueFieldsInFamilyApp.stream()
                    .filter(c -> !clientService.isOptOutPolicy(c))
                    .collect(Collectors.toList());

            if (optedInWithSameUniqueFieldsInFamilyApp.isEmpty()) {
                throw new CareTeamInvitationClientHieConsentValidationException("You record at the Family Organization " +
                        "should be Opted in to invite Care team member", true);
            }

            var firstWithSameUniqueFieldsInFamilyApp = optedInWithSameUniqueFieldsInFamilyApp.get(0);
            mpiMergedClientsService.mergeClients(client, firstWithSameUniqueFieldsInFamilyApp);
            return Optional.of(firstWithSameUniqueFieldsInFamilyApp);
        }

        mergedInFamilyApp = runMergingAndGetMerged(client);
        return mergedInFamilyApp;
    }

    private List<Client> findWithSameUniqueFieldsInFamilyApp(Client client) {
        if (StringUtils.isAllEmpty(
                client.getSocialSecurity(),
                client.getMedicaidNumber(),
                client.getMedicareNumber()
        )) {
            return List.of();
        }


        var familyAppClients = clientDao.findAll(((root, criteriaQuery, criteriaBuilder) -> {
            var predicates = new ArrayList<Predicate>();
            if (StringUtils.isNotEmpty(client.getSocialSecurity())) {
                predicates.add(criteriaBuilder.equal(root.get(Client_.socialSecurity), client.getSocialSecurity()));
            }

            if (StringUtils.isNotEmpty(client.getMedicareNumber())) {
                predicates.add(criteriaBuilder.equal(root.get(Client_.medicareNumber), client.getMedicareNumber()));
            }

            if (StringUtils.isNotEmpty(client.getMedicaidNumber())) {
                predicates.add(criteriaBuilder.equal(root.get(Client_.medicaidNumber), client.getMedicaidNumber()));
            }

            return criteriaBuilder.and(
                    criteriaBuilder.equal(JpaUtils.getOrCreateJoin(root, Client_.organization).get(Organization_.alternativeId),
                            CareCoordinationConstants.FAMILY_APP_ALTERNATIVE_ID),
                    criteriaBuilder.or(predicates.toArray(new Predicate[0]))
            );
        }), Client.class);

        return familyAppClients;
    }

    private boolean isFromFamilyOrganization(Client client) {
        return CareCoordinationConstants.FAMILY_APP_ALTERNATIVE_ID.equals(client.getOrganization().getAlternativeId());
    }

    private Optional<Client> findMergedInFamilyApp(Long clientId) {
        return clientService.findFirstMergedClientByOrganizationAlternativeId(
                clientId,
                CareCoordinationConstants.FAMILY_APP_ALTERNATIVE_ID
        );
    }

    private Optional<Client> runMergingAndGetMerged(Client client) {
        //todo use projections
        var result = clientMatcherService.findMatchedPatients(
                client,
                () -> clientService.findAllByOrganizationAlternativeId(
                        CareCoordinationConstants.FAMILY_APP_ALTERNATIVE_ID
                ),
                false
        );

        var familyAppClientsWithConfidence = result.getMatchedRecords().stream()
                .map(matchResultEntry -> {
                    var familyAppClient = getOtherClient(matchResultEntry, client);
                    return new Pair<>(familyAppClient, matchResultEntry.getConfidence());
                })
                .collect(Collectors.toList());

        var mergedFamilyAppClient = familyAppClientsWithConfidence.stream()
                .filter(pair -> !clientService.isOptOutPolicy(pair.getFirst()))
                .findFirst()
                .map(pair -> {
                    mpiMergedClientsService.mergeClients(client, pair.getFirst(), pair.getSecond());
                    return pair.getFirst();
                });
        if (mergedFamilyAppClient.isPresent()) {
            return mergedFamilyAppClient;
        }

        if (!familyAppClientsWithConfidence.isEmpty()) {
            //means that there are potential merged clients, but all are opted out.
            //We don't want to create more duplicates and instead force opting in one of such records.
            throw new CareTeamInvitationClientHieConsentValidationException("You record at the Family Organization " +
                    "should be Opted in to invite Care team member", true);
        }

        return Optional.empty();
    }

    private Client createFamilyAppMergedClient(Long clientId) {
        var sourceClient = clientService.findById(clientId);

        var targetClient = new Client();
        com.scnsoft.eldermark.service.basic.CareCoordinationConstants.setLegacyId(targetClient);
        targetClient.setLegacyTable(com.scnsoft.eldermark.service.basic.CareCoordinationConstants.CCN_MANUAL_LEGACY_TABLE);

        targetClient.setOrganization(organizationDao.findByAlternativeId(CareCoordinationConstants.FAMILY_APP_ALTERNATIVE_ID));
        targetClient.setOrganizationId(targetClient.getOrganization().getId());
        targetClient.setCommunity(communityDao.findFirstByOrganizationId(targetClient.getOrganizationId()));
        targetClient.setCommunityId(targetClient.getCommunity().getId());

        targetClient.setActive(true);
        targetClient.setFirstName(sourceClient.getFirstName());
        targetClient.setLastName(sourceClient.getLastName());
        targetClient.setSocialSecurity(sourceClient.getSocialSecurity());
        targetClient.setGender(sourceClient.getGender());
        targetClient.setMedicareNumber(sourceClient.getMedicareNumber());
        targetClient.setMedicaidNumber(sourceClient.getMedicaidNumber());
        targetClient.setGroupNumber(sourceClient.getGroupNumber());
        targetClient.setBirthDate(sourceClient.getBirthDate());
        targetClient.setDeathDate(sourceClient.getDeathDate());
        targetClient.setDeathIndicator(sourceClient.getDeathIndicator());
        targetClient.setInNetworkInsurance(sourceClient.getInNetworkInsurance());
        targetClient.setInsurancePlan(sourceClient.getInsurancePlan());
        targetClient.setMaritalStatus(sourceClient.getMaritalStatus());
        targetClient.setMemberNumber(sourceClient.getMemberNumber());
        targetClient.setMiddleName(sourceClient.getMiddleName());
        targetClient.setRace(sourceClient.getRace());
        targetClient.setReligion(sourceClient.getReligion());
        targetClient.setUnitNumber(sourceClient.getUnitNumber());
        targetClient.setVeteran(sourceClient.getVeteran());

        targetClient.setPerson(CareCoordinationUtils.createNewPerson(targetClient.getOrganization()));

        targetClient.setHieConsentPolicyType(HieConsentPolicyType.OPT_IN);

        copyPersonData(sourceClient.getPerson(), targetClient.getPerson());

        targetClient = clientService.save(targetClient);

        mpiMergedClientsService.mergeClients(sourceClient, targetClient);

        return targetClient;
    }

    private void copyPersonData(Person sourcePerson, Person targetPerson) {
        copyNames(sourcePerson.getNames(), targetPerson);
        copyTelecoms(sourcePerson.getTelecoms(), targetPerson);
        copyAddresses(sourcePerson.getAddresses(), targetPerson);
    }

    private void copyNames(List<Name> names, Person targetPerson) {
        if (CollectionUtils.isNotEmpty(names)) {
            targetPerson.setNames(new ArrayList<>(names.size()));
            for (var sourceName : names) {
                var name = new Name();
                name.setPerson(targetPerson);
                name.setOrganization(targetPerson.getOrganization());
                name.setOrganizationId(targetPerson.getOrganizationId());
                CareCoordinationConstants.setLegacyId(name);
                name.setLegacyTable(CareCoordinationConstants.RBA_NAME_LEGACY_TABLE);

                name.setNameUse(sourceName.getNameUse());
                name.setFamily(sourceName.getFamily());
                name.setGiven(sourceName.getGiven());
                name.setPreferredName(sourceName.getPreferredName());
                name.setMiddle(sourceName.getMiddle());
                name.setDegree(sourceName.getDegree());
                name.setFamilyQualifier(sourceName.getFamilyQualifier());
                name.setGivenQualifier(sourceName.getGivenQualifier());
                name.setMiddleQualifier(sourceName.getMiddleQualifier());
                name.setPrefix(sourceName.getPrefix());
                name.setPrefixQualifier(sourceName.getPrefixQualifier());
                name.setNameRepresentationCode(sourceName.getNameRepresentationCode());
                name.setSuffix(sourceName.getSuffix());
                name.setSuffixQualifier(sourceName.getSuffixQualifier());

                targetPerson.getNames().add(name);
            }
        }
    }

    private void copyTelecoms(List<PersonTelecom> telecoms, Person targetPerson) {
        if (CollectionUtils.isNotEmpty(telecoms)) {
            targetPerson.setTelecoms(new ArrayList<>(telecoms.size()));
            for (var sourceTelecom : telecoms) {
                var telecom = new PersonTelecom();
                telecom.setPerson(targetPerson);
                telecom.setOrganization(targetPerson.getOrganization());
                telecom.setOrganizationId(targetPerson.getOrganizationId());
                telecom.setLegacyTable(CareCoordinationConstants.RBA_PERSON_TELECOM_LEGACY_TABLE);
                com.scnsoft.eldermark.service.CareCoordinationConstants.setLegacyId(telecom);


                telecom.setSyncQualifier(sourceTelecom.getSyncQualifier());
                telecom.setUseCode(sourceTelecom.getUseCode());
                telecom.setValue(sourceTelecom.getValue());
                telecom.setNormalized(sourceTelecom.getNormalized());

                targetPerson.getTelecoms().add(telecom);
            }
        }
    }

    private void copyAddresses(List<PersonAddress> addresses, Person targetPerson) {
        if (CollectionUtils.isNotEmpty(addresses)) {
            targetPerson.setAddresses(new ArrayList<>(addresses.size()));
            for (var sourceTelecom : addresses) {
                var address = new PersonAddress();
                address.setPerson(targetPerson);
                address.setOrganization(targetPerson.getOrganization());
                address.setOrganizationId(targetPerson.getOrganizationId());
                address.setLegacyTable(CareCoordinationConstants.RBA_ADDRESS_LEGACY_TABLE);
                com.scnsoft.eldermark.service.CareCoordinationConstants.setLegacyId(address);


                address.setStreetAddress(sourceTelecom.getStreetAddress());
                address.setCity(sourceTelecom.getCity());
                address.setCountry(sourceTelecom.getCountry());
                address.setPostalAddressUse(sourceTelecom.getPostalAddressUse());
                address.setPostalCode(sourceTelecom.getPostalCode());
                address.setState(sourceTelecom.getState());

                targetPerson.getAddresses().add(address);
            }
        }
    }

    @Override
    public List<Client> findForHiePolicyChangeAmong(Collection<Long> clientIds, Collection<Client> clients, Collection<Client> clientsToCheck) {
        var mergedIds = findMergedAmong(clientIds, clientsToCheck);

        var withSameUniqueFieldsIds = findWithSameUniqueFieldsAmong(clients, clientsToCheck);

        var remainingToCheckPotentialMerge = clientsToCheck.stream()
                .filter(clientToCheck -> !mergedIds.contains(clientToCheck.getId()) && !withSameUniqueFieldsIds.contains(clientToCheck.getId()))
                .collect(Collectors.toList());

        var potentialMergedClientIds = findPotentialMergedClientIdsAndMerge(clientIds, clients, remainingToCheckPotentialMerge);

        return clientsToCheck.stream()
                .filter(clientToCheck -> mergedIds.contains(clientToCheck.getId()) || withSameUniqueFieldsIds.contains(clientToCheck.getId()) ||
                        potentialMergedClientIds.contains(clientToCheck.getId()))
                .collect(Collectors.toList());
    }

    private Set<Long> findMergedAmong(Collection<Long> clientIds, Collection<Client> clientsToCheck) {
        return clientService.findMergedClientIdsAmong(
                        clientIds,
                        CareCoordinationUtils.toIds(clientsToCheck, Collectors.toList())
                ).values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Set<Long> findWithSameUniqueFieldsAmong(Collection<Client> clients, Collection<Client> clientsToCheck) {
        return clientsToCheck.stream()
                .filter(clientToCheck -> {
                    if (StringUtils.isNotEmpty(clientToCheck.getSocialSecurity())) {
                        if (clients.stream().anyMatch(client -> clientToCheck.getSocialSecurity().equals(client.getSocialSecurity()))) {
                            return true;
                        }
                    }

                    if (StringUtils.isNotEmpty(clientToCheck.getMedicareNumber())) {
                        if (clients.stream().anyMatch(client -> clientToCheck.getMedicareNumber().equals(client.getMedicareNumber()))) {
                            return true;
                        }
                    }

                    if (StringUtils.isNotEmpty(clientToCheck.getMedicaidNumber())) {
                        if (clients.stream().anyMatch(client -> clientToCheck.getMedicaidNumber().equals(client.getMedicaidNumber()))) {
                            return true;
                        }
                    }
                    return false;
                })
                .map(BasicEntity::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> findPotentialMergedClientIdsAndMerge(Collection<Long> clientIds, Collection<Client> clients, List<Client> remainingToCheckPotentialMerge) {
        var potentialMerges = clientMatcherService.findMatchedPatients(clients, () -> remainingToCheckPotentialMerge, false);
        var potentialMergedClientIds = CollectionUtils.emptyIfNull(potentialMerges.getMatchedRecords()).stream()
                .map(matchResultEntry -> {
                    if (!clientService.isOptOutPolicy(matchResultEntry.getR1()) && !clientService.isOptOutPolicy(matchResultEntry.getR2())) {
                        mpiMergedClientsService.mergeClients(matchResultEntry.getR1(), matchResultEntry.getR2(), matchResultEntry.getConfidence());
                    }
                    return getOtherClient(matchResultEntry, clientIds);
                })
                .map(BasicEntity::getId)
                .collect(Collectors.toSet());
        return potentialMergedClientIds;
    }

    private Client getOtherClient(MatchResultEntry<Client> matchResultEntry, Client client) {
        return Stream.of(
                        matchResultEntry.getR1(),
                        matchResultEntry.getR2()
                )
                .filter(c -> !c.getId().equals(client.getId()))
                .findFirst()
                .orElseThrow();
    }

    private Client getOtherClient(MatchResultEntry<Client> matchResultEntry, Collection<Long> clientIds) {
        return Stream.of(
                        matchResultEntry.getR1(),
                        matchResultEntry.getR2()
                )
                .filter(c -> !clientIds.contains(c.getId()))
                .findFirst()
                .orElseThrow();
    }
}
