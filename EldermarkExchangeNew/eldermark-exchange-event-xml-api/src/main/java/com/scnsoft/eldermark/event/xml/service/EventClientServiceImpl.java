package com.scnsoft.eldermark.event.xml.service;

import com.scnsoft.eldermark.beans.ClientFilter;
import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.specification.ClientSpecificationGenerator;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.event.xml.dao.EventClientDao;
import com.scnsoft.eldermark.event.xml.entity.emuns.Gender;
import com.scnsoft.eldermark.event.xml.schema.Address;
import com.scnsoft.eldermark.event.xml.schema.DevicePatient;
import com.scnsoft.eldermark.event.xml.schema.Patient;
import com.scnsoft.eldermark.service.CareCoordinationConstants;
import com.scnsoft.eldermark.service.ClientHieConsentDefaultPolicyService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EventClientServiceImpl implements EventClientService {

    private final ClientService clientService;

    private final EventClientDao eventClientDao;

    private final EventCcdCodeService eventCcdCodeService;

    private final Converter<Address, PersonAddress> personAddressEntityConverter;

    private final ClientSpecificationGenerator clientSpecificationGenerator;

    private final ClientDao clientDao;

    private final ClientHieConsentDefaultPolicyService clientHieConsentDefaultPolicyService;

    public EventClientServiceImpl(ClientService clientService, EventClientDao eventClientDao, EventCcdCodeService eventCcdCodeService, Converter<Address, PersonAddress> personAddressEntityConverter, ClientSpecificationGenerator clientSpecificationGenerator, ClientDao clientDao, ClientHieConsentDefaultPolicyService clientHieConsentDefaultPolicyService) {
        this.clientService = clientService;
        this.eventClientDao = eventClientDao;
        this.eventCcdCodeService = eventCcdCodeService;
        this.personAddressEntityConverter = personAddressEntityConverter;
        this.clientSpecificationGenerator = clientSpecificationGenerator;
        this.clientDao = clientDao;
        this.clientHieConsentDefaultPolicyService = clientHieConsentDefaultPolicyService;
    }

    @Override
    public List<Client> getOrCreateClient(Community community, Patient patient) {
        Optional.ofNullable(patient.getSSN())
                .map(ssn -> ssn.replaceAll("-", ""))
                .ifPresent(patient::setSSN);

        Client organizationClient = null;
        Client communityClient = null;
        if (StringUtils.isNotEmpty(patient.getPatientID())) {
            organizationClient = eventClientDao.findByLegacyIdAndOrganizationId(patient.getPatientID(), community.getOrganizationId()).orElse(null);
            communityClient = eventClientDao.findByLegacyIdAndCommunityId(patient.getPatientID(), community.getId()).orElse(null);
        }

        if (organizationClient != null) {
            if (communityClient != null) {
                //In case patient was found in organization and community specified in request - update patient data
                updateClientData(patient, organizationClient);
                var client = clientService.saveWithUpdateLegacyIdsIfEmpty(organizationClient);
                return clientService.findAllMergedClients(client);
            } else {
                //In case patient was found in organization but in other community - do not update id or data
                return clientService.findAllMergedClients(organizationClient);
            }
        } else {
            var byFilter = clientSpecificationGenerator.byFilter(createClientFilter(patient));
            var byOrganizationId = clientSpecificationGenerator.byOrganizationId(community.getOrganizationId());
            //In case patient with specified id is not exist - update patient id
            return clientDao.findOne(byFilter.and(byOrganizationId))
                    .map(client -> {
                        if (StringUtils.isNotEmpty(patient.getPatientID())) {
                            client.setLegacyId(patient.getPatientID());
                            return clientService.saveWithUpdateLegacyIdsIfEmpty(client);
                        }
                        return client;
                    })
                    .map(clientService::findAllMergedClients)
                    .orElseGet(() -> List.of(createClient(patient, community)));
        }
    }

    @Override
    public List<Client> getOrCreateClient(Community community, DevicePatient patient) {
        //TODO remove as it is not used
        return null;
    }

    private void updateClientData(Patient patient, Client client) {
        var ssn = CareCoordinationUtils.normalizePhone(patient.getSSN());
        client.setSocialSecurity(ssn);
        client.setSsnLastFourDigits(StringUtils.right(ssn, 4));
        client.setFirstName(patient.getName().getFirstName());
        client.setLastName(patient.getName().getLastName());
        client.setMiddleName(patient.getName().getMiddleName());
        if (CollectionUtils.isNotEmpty(client.getPerson().getNames())) {
            Name name = client.getPerson().getNames().get(0);
            name.setGiven(patient.getName().getFirstName());
            name.setFamily(patient.getName().getLastName());
        }
        client.setBirthDate(DateTimeUtils.toLocalDate(patient.getDateOfBirth().toGregorianCalendar().getTime()));
        client.setGender(eventCcdCodeService.getGenderCcdCode(Gender.getGenderByCode(patient.getGender())));
        client.setMaritalStatus(Optional.ofNullable(patient.getMaritalStatus())
                .map(eventCcdCodeService::getMaritalStatus)
                .orElse(null));
        createOrUpdatePersonAddress(client.getOrganization(), client.getPerson(), patient.getAddress());
        createOrUpdatePersonName(client.getPerson(), client.getFirstName(), client.getLastName());
    }

    private void createOrUpdatePersonAddress(Organization organization, Person person, Address address) {
        var personAddress = personAddressEntityConverter.convert(address);
        if (CollectionUtils.isNotEmpty(person.getAddresses())) {
            person.getAddresses().clear();
        } else {
            person.setAddresses(new ArrayList<>());
        }
        if (personAddress != null) {
            personAddress.setOrganization(organization);
            personAddress.setOrganizationId(organization.getId());
            personAddress.setPerson(person);
            person.getAddresses().add(personAddress);
        }
    }

    private void createOrUpdatePersonName(Person person, String firstName, String lastName) {
        if (CollectionUtils.isEmpty(person.getNames())) {
            person.setNames(new ArrayList<>());
        }
        var name = person.getNames().stream()
                .filter(item -> "L".equals(item.getNameUse()))
                .findFirst()
                .orElseGet(() -> {
                    var n = new Name();
                    n.setLegacyTable(CareCoordinationConstants.RBA_NAME_LEGACY_TABLE);
                    CareCoordinationConstants.setLegacyId(n);
                    n.setNameUse("L");
                    n.setOrganizationId(person.getOrganizationId());
                    n.setOrganization(person.getOrganization());
                    n.setPerson(person);
                    person.getNames().add(n);
                    return n;
                });

        name.setGiven(firstName);
        name.setGivenNormalized(CareCoordinationUtils.normalizeName(firstName));
        name.setFamily(lastName);
        name.setFamilyNormalized(CareCoordinationUtils.normalizeName(lastName));
    }

    private ClientFilter createClientFilter(Patient patient) {
        var filter = new ClientFilter();
        filter.setFirstName(patient.getName().getFirstName());
        filter.setLastName(patient.getName().getLastName());
        filter.setSsn(CareCoordinationUtils.normalizePhone(patient.getSSN()));
        filter.setGenderId(Optional.ofNullable(eventCcdCodeService.getGenderCcdCode(Gender.getGenderByCode(patient.getGender()))).map(CcdCode::getId).orElse(null));
        filter.setBirthDate(new SimpleDateFormat("MM/dd/yyyy").format(patient.getDateOfBirth().toGregorianCalendar().getTime()));
        return filter;
    }

    private Client createClient(Patient patient, Community community) {
        var client = new Client();
        client.setActive(true);
        client.setSharing(community.getIsSharingData());
        client.setOrganization(community.getOrganization());
        client.setOrganizationId(community.getOrganizationId());
        if (StringUtils.isNotBlank(patient.getPatientID())) {
            client.setLegacyId(patient.getPatientID());
        } else {
            CareCoordinationConstants.setLegacyId(client);
        }
        client.setCommunity(community);
        client.setCommunityId(community.getId());
        client.setLegacyTable(CareCoordinationConstants.RBA_PERSON_LEGACY_TABLE);
        client.setPerson(createPerson(community));
        clientHieConsentDefaultPolicyService.fillDefaultPolicy(client);
        updateClientData(patient, client);

        return clientService.saveWithUpdateLegacyIdsIfEmpty(client);
    }

    private Person createPerson(Community community) {
        var person = new Person();
        CareCoordinationConstants.setLegacyId(person);
        person.setLegacyTable(CareCoordinationConstants.RBA_PERSON_LEGACY_TABLE);
        person.setOrganization(community.getOrganization());
        person.setOrganizationId(community.getOrganizationId());
        person.setTelecoms(new ArrayList<>());
        return person;
    }
}
