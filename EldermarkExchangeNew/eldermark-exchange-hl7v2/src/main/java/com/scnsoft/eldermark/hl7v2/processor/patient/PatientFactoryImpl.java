package com.scnsoft.eldermark.hl7v2.processor.patient;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.OrganizationDao;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.MPI;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.hl7v2.exception.RegistryPatientException;
import com.scnsoft.eldermark.hl7v2.model.PatientIdentifiersHolder;
import com.scnsoft.eldermark.hl7v2.model.PersonIdentifier;
import com.scnsoft.eldermark.hl7v2.processor.patient.demographics.HL7v2PatientDemographics;
import com.scnsoft.eldermark.hl7v2.processor.patient.mpi.HL7v2MpiService;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.jms.producer.ClientUpdateQueueProducer;
import com.scnsoft.eldermark.service.ClientHieConsentDefaultPolicyService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PatientFactoryImpl implements PatientFactory {
    private static final Logger logger = LoggerFactory.getLogger(PatientFactoryImpl.class);

    private static final String DEFAULT_ADT_ORGANIZATION = "ADT Repo";
    private static final String DEFAULT_ADT_COMMUNITY = "ADT Organization";


    @Autowired
    private PatientDemographicsUpdater patientDemographicsUpdater;

    @Autowired
    private OrganizationDao organizationDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ClientUpdateQueueProducer clientUpdateQueueProducer;

    @Autowired
    private HL7v2MpiService hl7v2MpiService;

    @Autowired
    private ClientHieConsentDefaultPolicyService clientHieConsentDefaultPolicyService;

    @Override
    @Transactional
    public Client createPatient(PatientIdentifiersHolder patientIdentifiersHolder, HL7v2PatientDemographics patientDemographics, MessageSource messageSource) {
        var integrationPartner = messageSource.getHl7v2IntegrationPartner();
        var patientIdentifier = integrationPartner.getIdentifierToCreatePatient(patientIdentifiersHolder);
        logger.info("Will create patient for identifier {}", patientIdentifier);

        String organizationOid = StringUtils.defaultString(patientIdentifier.getAssigningAuthority().getUniversalId());
        String communityOid = patientIdentifier.getAssigningFacility().getUniversalId();

        final Organization organization = resolveOrganization(organizationOid);
        final Community community = resolveCommunity(organization, communityOid);
        if (community == null) {
            logger.warn("Failed to find community for the patient in organization {}", organization.getId());
            throw new RegistryPatientException("Failed to find community for the patient");
        }

        var client = new Client();

        client.setOrganization(organization);
        client.setOrganizationId(organization.getId());
        client.setCommunity(community);
        client.setCommunityId(community.getId());
        client.setActive(true);
        clientHieConsentDefaultPolicyService.fillDefaultPolicy(client);

        LegacyIdResolver.setLegacyId(client);

        patientDemographicsUpdater.updateDemographics(client, patientDemographics);

        client = clientDao.save(client);

        updateLegacyIds(client);
        clientDao.save(client);

        clientUpdateQueueProducer.putToResidentUpdateQueue(client.getId(), ResidentUpdateType.RESIDENT);

        processMPI(client, patientDemographics, integrationPartner.getIdentifiersToCreatePatientMPI(patientIdentifiersHolder));

        return client;
    }

    private void processMPI(Client client, HL7v2PatientDemographics patientDemographics, List<PersonIdentifier> identifiersForMpi) {
        //TODO: Patient matching may be there
        //Create new Patient Identifier records in MPI
        for (var pid : identifiersForMpi) {
            MPI identifier = PersonIdentifier.createMPIFromPersonIdentifier(pid);

            MPI personIdentifier = hl7v2MpiService.findMPI(identifier);
            if (personIdentifier == null) {
                identifier.setDeleted(Boolean.TRUE.equals(patientDemographics.getDeathIndicator()) ? "Y" : "N");
                identifier.setMerged("N");
                identifier.setClient(client);

                hl7v2MpiService.save(identifier);
            }
        }
    }

    private Organization resolveOrganization(String organizationOid) {
        final Organization database = organizationDao.findFirstByOid(organizationOid);
        if (database != null) {
            return database;
        }
        logger.error("No database found with OID: {} Record will be passed to default organization 'ADT repo'", organizationOid);
        return organizationDao.findFirstByName(DEFAULT_ADT_ORGANIZATION);
    }

    private Community resolveCommunity(Organization organization, String facilityStr) {
        if (DEFAULT_ADT_ORGANIZATION.equalsIgnoreCase(organization.getName())) {
            logger.info("Fetching Adt Repo default community");
            return communityDao.findByOrganizationIdAndName(organization.getId(), DEFAULT_ADT_COMMUNITY);
        }

        if (StringUtils.isNotEmpty(facilityStr)) {
            logger.info("Incoming message has assigning facility - trying to fetch");
            var community = communityDao.findByOrganizationIdAndOid(organization.getId(), facilityStr);
            if (community != null) {
                return community;
            }
        }

        logger.info("Wasn't able to resolve Community so far - fetching default community");
        return communityDao.findFirstByOrganizationIdAndXdsDefaultTrue(organization.getId());
    }

    private void updateLegacyIds(Client resident) {
        LegacyIdResolver.setLegacyId(resident);

        var person = resident.getPerson();
        LegacyIdResolver.setLegacyId(person);

        for (var personAddress : person.getAddresses()) {
            LegacyIdResolver.setLegacyId(personAddress);
        }

        for (var telecom : person.getTelecoms()) {
            LegacyIdResolver.setLegacyId(telecom);
        }

        for (var name : person.getNames()) {
            LegacyIdResolver.setLegacyId(name);
        }


        for (var language : CollectionUtils.emptyIfNull(resident.getLanguages())) {
            LegacyIdResolver.setLegacyId(language);
        }

        var mother = resident.getMother();
        if (mother != null) {
            LegacyIdResolver.setLegacyId(mother);
            for (var name : mother.getNames()) {
                LegacyIdResolver.setLegacyId(name);
            }
        }
    }
}
