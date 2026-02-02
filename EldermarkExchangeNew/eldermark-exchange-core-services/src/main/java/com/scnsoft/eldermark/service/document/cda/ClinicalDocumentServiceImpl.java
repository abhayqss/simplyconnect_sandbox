package com.scnsoft.eldermark.service.document.cda;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.document.ccd.Custodian;
import com.scnsoft.eldermark.entity.document.ccd.DataEnterer;
import com.scnsoft.eldermark.entity.document.ccd.LegalAuthenticator;
import com.scnsoft.eldermark.jms.dto.ResidentUpdateType;
import com.scnsoft.eldermark.jms.producer.ClientUpdateQueueProducer;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.ccd.factory.sections.*;
import com.scnsoft.eldermark.service.document.templates.cda.factory.header.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ClinicalDocumentServiceImpl implements ClinicalDocumentService {

    @Autowired
    private ClientService clientService;

    // header factories
    @Autowired
    private RecordTargetFactory recordTargetFactory;
    @Autowired
    private AuthorFactory authorFactory;
    @Autowired
    private DataEntererFactory dataEntererFactory;
    @Autowired
    private InformantFactory informantFactory;
    @Autowired
    private CustodianFactory custodianFactory;
    @Autowired
    private InformationRecipientFactory informationRecipientFactory;
    @Autowired
    private LegalAuthenticatorFactory legalAuthenticatorFactory;
    @Autowired
    private AuthenticatorFactory authenticatorFactory;
    @Autowired
    private ParticipantFactory participantFactory;
    @Autowired
    private InFulfillmentOfFactory inFulfillmentOfFactory;
    @Autowired
    private DocumentationOfFactory documentationOfFactory;
    @Autowired
    private AuthorizationFactory authorizationFactory;
    @Autowired
    private ComponentFactory componentFactory;

    // body factories
    @Autowired
    private AdvanceDirectiveFactory advanceDirectiveFactory;
    @Autowired
    private AllergiesFactory allergiesFactory;
    @Autowired
    private EncountersFactory encountersFactory;
    @Autowired
    private FamilyHistoryFactory familyHistoryFactory;
    @Autowired
    private FunctionalStatusFactory functionalStatusFactory;
    @Autowired
    private ImmunizationsFactory immunizationsFactory;
    @Autowired
    private MedicalEquipmentFactory medicalEquipmentFactory;
    @Autowired
    private MedicationsFactory medicationsFactory;
    @Autowired
    private PayerFactory payerFactory;
    @Autowired
    private PlanOfCareFactory planOfCareFactory;
    @Autowired
    private ProblemsFactory problemsFactory;
    @Autowired
    private ProceduresFactory proceduresFactory;
    @Autowired
    private ResultsFactory resultsFactory;
    @Autowired
    private SocialHistoryFactory socialHistoryFactory;
    @Autowired
    private VitalSignFactory vitalSignFactory;

    // header DAOs
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private AuthorDao authorDao;
    @Autowired
    private DataEntererDao dataEntererDao;
    @Autowired
    private InformantDao informantDao;
    @Autowired
    private CustodianDao custodianDao;
    @Autowired
    private InformationRecipientDao informationRecipientDao;
    @Autowired
    private LegalAuthenticatorDao legalAuthenticatorDao;
    @Autowired
    private AuthenticatorDao authenticatorDao;
    @Autowired
    private ParticipantDao participantDao;
    @Autowired
    private DocumentationOfDao documentationOfDao;

    // body DAOs
    @Autowired
    private AdvanceDirectiveDao advanceDirectiveDao;
    @Autowired
    private AllergyDao allergiesDao;
    @Autowired
    private EncounterDao encountersDao;
    @Autowired
    private FamilyHistoryDao familyHistoryDao;
    @Autowired
    private FunctionalStatusDao functionalStatusDao;
    @Autowired
    private ImmunizationDao immunizationsDao;
    @Autowired
    private MedicalEquipmentDao medicalEquipmentDao;
    @Autowired
    private MedicationDao medicationDao;
    @Autowired
    private PayerDao payerDao;
    @Autowired
    private PlanOfCareDao planOfCareDao;
    @Autowired
    private ProblemDao problemDao;
    @Autowired
    private ProcedureDao proceduresDao;
    @Autowired
    private ResultDao resultsDao;
    @Autowired
    private SocialHistoryDao socialHistoryDao;
    @Autowired
    private VitalSignDao vitalSignDao;

    @Autowired
    private ClientUpdateQueueProducer clientUpdateQueueProducer;

    @Override
    @Transactional(readOnly = true)
    public ClinicalDocumentVO getClinicalDocument(Long mainClientId, List<Long> clientIds) {
        final ClinicalDocumentVO document = new ClinicalDocumentVO();
        if (CollectionUtils.isEmpty(clientIds)) {
            return document;
        }

        // Restrictions of an aggregated clinical document
        // * ClinicalDocument (C-CDA) SHOULD contain zero or one (0..1)
        // legalAuthenticator (CONF:5579 [1], CONF:1198-5579 [2]).
        // * ClinicalDocument (C-CDA) MAY contain zero or one (0..1) dataEnterer
        // (CONF:5441 [1], CONF:1198-28678 [2]).
        // * ClinicalDocument (C-CDA) SHALL contain exactly one (1..1) custodian
        // (CONF:5519 [1], CONF:1198-5519 [2]).
        // * Though ClinicalDocument may contain multiple (1..*) recordTarget elements
        // (Patient Details section in UI),
        // I don't think it's the right way of representing a merged record.
        //
        // References:
        // [1] The first reference is to General Header Constraints (templateId =
        // 2.16.840.1.113883.10.20.22.1.1), 2013
        // [2] The second reference is to US Realm Header (V3) Constraints (templateId =
        // 2.16.840.1.113883.10.20.22.1.1 : 2015-08-01), 2015

        // header sections [0..1]

        //todo apply security checks when generating CCD document
        if (dataEntererFactory.isTemplateIncluded()) {
            final DataEnterer dataEnterer = dataEntererDao.getCcdDataEnterer(mainClientId);
            document.setDataEnterer(dataEnterer);
        }

        if (custodianFactory.isTemplateIncluded()) {
            final Custodian custodian = custodianDao.getCcdCustodian(mainClientId);
            document.setCustodian(custodian);
        }
        if (legalAuthenticatorFactory.isTemplateIncluded()) {
            final LegalAuthenticator legalAuthenticator = legalAuthenticatorDao.getCcdLegalAuthenticator(mainClientId);
            document.setLegalAuthenticator(legalAuthenticator);
        }
        if (recordTargetFactory.isTemplateIncluded()) {
            final Client client = clientDao.getOne(mainClientId);

            document.setRecordTarget(client);
        }

        // header sections [0..*]

        if (authorFactory.isTemplateIncluded()) {
            document.setAuthors(authorDao.findByClient_IdIn(clientIds));
        }
        if (informantFactory.isTemplateIncluded()) {
            document.setInformants(informantDao.findByClient_IdIn(clientIds));
        }
        if (informationRecipientFactory.isTemplateIncluded()) {
            document.setInformationRecipients(informationRecipientDao.findByClient_IdIn(clientIds));
        }
        if (authenticatorFactory.isTemplateIncluded()) {
            document.setAuthenticators(authenticatorDao.findByClient_IdIn(clientIds));
        }
        if (participantFactory.isTemplateIncluded()) {
            // FIXME : to verify if its correct or can be listCcdParticipants or listResponsibleParties
            document.setParticipants(participantDao.listByClientIds(clientIds));
        }
        if (documentationOfFactory.isTemplateIncluded()) {
            document.setDocumentationOfs(documentationOfDao.findByClient_IdIn(clientIds));
        }

        // stubs - not implemented for an unknown reason
        if (inFulfillmentOfFactory.isTemplateIncluded()) {
            document.setInFulfillmentOfs(Collections.<BasicEntity>emptyList());
        }
        if (authorizationFactory.isTemplateIncluded()) {
            document.setAuthorizations(Collections.<BasicEntity>emptyList());
        }
        if (componentFactory.isTemplateIncluded()) {
            document.setComponent(null);
        }

        // body sections

        if (advanceDirectiveFactory.isTemplateIncluded()) {
            document.setAdvanceDirectives(advanceDirectiveDao.findByClient_IdIn(clientIds));
        }
        if (allergiesFactory.isTemplateIncluded()) {
            document.setAllergies(allergiesDao.listByClientIds(clientIds));
        }
        if (encountersFactory.isTemplateIncluded()) {
            document.setEncounters(encountersDao.findByClient_IdIn(clientIds));
        }
        if (familyHistoryFactory.isTemplateIncluded()) {
            document.setFamilyHistories(familyHistoryDao.findByClient_IdIn(clientIds));
        }
        if (functionalStatusFactory.isTemplateIncluded()) {
            document.setFunctionalStatuses(functionalStatusDao.listByClientIds(clientIds));
        }
        if (immunizationsFactory.isTemplateIncluded()) {
            document.setImmunizations(immunizationsDao.findByClient_IdIn(clientIds));
        }
        if (medicalEquipmentFactory.isTemplateIncluded()) {
            document.setMedicalEquipments(medicalEquipmentDao.findByClient_IdIn(clientIds));
        }
        if (medicationsFactory.isTemplateIncluded()) {
            document.setMedications(medicationDao.findByClient_IdIn(clientIds));
        }
        if (payerFactory.isTemplateIncluded()) {
            document.setPayers(payerDao.findByClient_IdIn(clientIds));
        }
        if (planOfCareFactory.isTemplateIncluded()) {
            document.setPlanOfCares(planOfCareDao.listByClientIds(clientIds));
        }
        if (problemsFactory.isTemplateIncluded()) {
            document.setProblems(problemDao.listByClientIds(clientIds));
        }
        if (proceduresFactory.isTemplateIncluded()) {
            document.setProcedures(proceduresDao.findByClient_IdIn(clientIds));
        }
        if (resultsFactory.isTemplateIncluded()) {
            document.setResults(resultsDao.findByClient_IdIn(clientIds));
        }
        if (socialHistoryFactory.isTemplateIncluded()) {
            document.setSocialHistories(socialHistoryDao.findByClient_IdIn(clientIds));
        }
        if (vitalSignFactory.isTemplateIncluded()) {
            document.setVitalSigns(vitalSignDao.findByClient_IdIn(clientIds));
        }

        return document;
    }

    @Override
    public void deleteByResidentId(Long clientId) {
        Objects.requireNonNull(clientId);

        // header sections
        if (dataEntererFactory.isTemplateIncluded()) {
            dataEntererDao.deleteAllByClientId(clientId);
        }
        if (custodianFactory.isTemplateIncluded()) {
            custodianDao.deleteAllByClientId(clientId);
        }
        if (legalAuthenticatorFactory.isTemplateIncluded()) {
            legalAuthenticatorDao.deleteAllByClientId(clientId);
        }
        if (authorFactory.isTemplateIncluded()) {
            authorDao.deleteAllByClientId(clientId);
        }
        if (informantFactory.isTemplateIncluded()) {
            informantDao.deleteAllByClientId(clientId);
        }
        if (informationRecipientFactory.isTemplateIncluded()) {
            informationRecipientDao.deleteAllByClientId(clientId);
        }
        if (authenticatorFactory.isTemplateIncluded()) {
            authenticatorDao.deleteAllByClientId(clientId);
        }
        if (participantFactory.isTemplateIncluded()) {
            participantDao.deleteAllByClientId(clientId);
        }
        if (documentationOfFactory.isTemplateIncluded()) {
            documentationOfDao.deleteAllByClientId(clientId);
        }

        // stubs - not implemented for an unknown reason
        if (inFulfillmentOfFactory.isTemplateIncluded()) ;  // do nothing
        if (authorizationFactory.isTemplateIncluded()) ;    // do nothing
        if (componentFactory.isTemplateIncluded()) ;        // do nothing

        // body sections
        if (advanceDirectiveFactory.isTemplateIncluded()) {
            advanceDirectiveDao.deleteAllByClientId(clientId);
        }
        if (allergiesFactory.isTemplateIncluded()) {
            allergiesDao.deleteAllByClientId(clientId);
        }
        if (encountersFactory.isTemplateIncluded()) {
            encountersDao.deleteAllByClientId(clientId);
        }
        if (familyHistoryFactory.isTemplateIncluded()) {
            familyHistoryDao.deleteAllByClientId(clientId);
        }
        if (functionalStatusFactory.isTemplateIncluded()) {
            functionalStatusDao.deleteAllByClientId(clientId);
        }
        if (immunizationsFactory.isTemplateIncluded()) {
            immunizationsDao.deleteAllByClientId(clientId);
        }
        if (medicalEquipmentFactory.isTemplateIncluded()) {
            medicalEquipmentDao.deleteAllByClientId(clientId);
        }
        if (medicationsFactory.isTemplateIncluded()) {
            medicationDao.deleteAllByClientId(clientId);
        }
        if (payerFactory.isTemplateIncluded()) {
            payerDao.deleteAllByClientId(clientId);
        }
        if (planOfCareFactory.isTemplateIncluded()) {
            planOfCareDao.deleteAllByClientId(clientId);
        }
        if (problemsFactory.isTemplateIncluded()) {
            problemDao.deleteAllByClientId(clientId);
        }
        if (proceduresFactory.isTemplateIncluded()) {
            proceduresDao.deleteAllByClientId(clientId);
        }
        if (resultsFactory.isTemplateIncluded()) {
            resultsDao.deleteAllByClientId(clientId);
        }
        if (socialHistoryFactory.isTemplateIncluded()) {
            socialHistoryDao.deleteAllByClientId(clientId);
        }
        if (vitalSignFactory.isTemplateIncluded()) {
            vitalSignDao.deleteAllByClientId(clientId);
        }
    }

    @Override
    @Transactional
    public void saveClinicalDocument(Client resident, ClinicalDocumentVO clinicalDocumentVO) {
        Objects.requireNonNull(resident);
        Objects.requireNonNull(clinicalDocumentVO);

        // TODO iterate resident-related entities and change ownership from RecordTarget to Resident ?

        var updateTypes = EnumSet.noneOf(ResidentUpdateType.class);
        // header sections [0..1]
        if (dataEntererFactory.isTemplateIncluded() && clinicalDocumentVO.getDataEnterer() != null) {
            dataEntererDao.save(clinicalDocumentVO.getDataEnterer());
        }
        if (custodianFactory.isTemplateIncluded() && clinicalDocumentVO.getCustodian() != null) {
            custodianDao.save(clinicalDocumentVO.getCustodian());
        }
        if (legalAuthenticatorFactory.isTemplateIncluded() && clinicalDocumentVO.getLegalAuthenticator() != null) {
            legalAuthenticatorDao.save(clinicalDocumentVO.getLegalAuthenticator());
        }

        // header sections [0..*]
        if (authorFactory.isTemplateIncluded()) {
            authorDao.saveAll(clinicalDocumentVO.getAuthors());
        }
        if (informantFactory.isTemplateIncluded()) {
            informantDao.saveAll(clinicalDocumentVO.getInformants());
        }
        if (informationRecipientFactory.isTemplateIncluded()) {
            informationRecipientDao.saveAll(clinicalDocumentVO.getInformationRecipients());
        }
        if (authenticatorFactory.isTemplateIncluded()) {
            authenticatorDao.saveAll(clinicalDocumentVO.getAuthenticators());
        }
        if (participantFactory.isTemplateIncluded()) {
            participantDao.saveAll(clinicalDocumentVO.getParticipants());
        }
        if (documentationOfFactory.isTemplateIncluded()) {
            documentationOfDao.saveAll(clinicalDocumentVO.getDocumentationOfs());
        }

        // stubs - not implemented for an unknown reason
        if (inFulfillmentOfFactory.isTemplateIncluded()) ;  // do nothing
        if (authorizationFactory.isTemplateIncluded()) ;    // do nothing
        if (componentFactory.isTemplateIncluded()) ;        // do nothing

        // body sections
        if (advanceDirectiveFactory.isTemplateIncluded()) {
            advanceDirectiveDao.saveAll(clinicalDocumentVO.getAdvanceDirectives());
        }
        if (allergiesFactory.isTemplateIncluded()) {
            if (CollectionUtils.isNotEmpty(allergiesDao.saveAll(clinicalDocumentVO.getAllergies()))) {
                updateTypes.add(ResidentUpdateType.ALLERGY);
            }
        }
        if (encountersFactory.isTemplateIncluded()) {
            if (CollectionUtils.isNotEmpty(encountersDao.saveAll(clinicalDocumentVO.getEncounters()))) {
                updateTypes.add(ResidentUpdateType.ENCOUNTER);
            }
        }
        if (familyHistoryFactory.isTemplateIncluded()) {
            familyHistoryDao.saveAll(clinicalDocumentVO.getFamilyHistories());
        }
        if (functionalStatusFactory.isTemplateIncluded()) {
            functionalStatusDao.saveAll(clinicalDocumentVO.getFunctionalStatuses());
        }
        if (immunizationsFactory.isTemplateIncluded()) {
            if (CollectionUtils.isNotEmpty(immunizationsDao.saveAll(clinicalDocumentVO.getImmunizations()))) {
                updateTypes.add(ResidentUpdateType.MEDICATION);
            }
        }
        if (medicalEquipmentFactory.isTemplateIncluded()) {
            medicalEquipmentDao.saveAll(clinicalDocumentVO.getMedicalEquipments());
        }
        if (medicationsFactory.isTemplateIncluded()) {
            if (CollectionUtils.isNotEmpty(medicationDao.saveAll(clinicalDocumentVO.getMedications()))) {
                updateTypes.add(ResidentUpdateType.MEDICATION);
            }
        }
        if (payerFactory.isTemplateIncluded()) {
            payerDao.saveAll(clinicalDocumentVO.getPayers());
        }
        if (planOfCareFactory.isTemplateIncluded()) {
            planOfCareDao.saveAll(clinicalDocumentVO.getPlanOfCares());
        }
        if (problemsFactory.isTemplateIncluded()) {
            if (CollectionUtils.isNotEmpty(problemDao.saveAll(clinicalDocumentVO.getProblems()))) {
                updateTypes.add(ResidentUpdateType.ALLERGY);
            }
        }
        if (proceduresFactory.isTemplateIncluded()) {
            proceduresDao.saveAll(clinicalDocumentVO.getProcedures());
        }
        if (resultsFactory.isTemplateIncluded()) {
            resultsDao.saveAll(clinicalDocumentVO.getResults());
        }
        if (socialHistoryFactory.isTemplateIncluded()) {
            socialHistoryDao.saveAll(clinicalDocumentVO.getSocialHistories());
        }
        if (vitalSignFactory.isTemplateIncluded()) {
            vitalSignDao.saveAll(clinicalDocumentVO.getVitalSigns());
        }

        clientService.save(resident);

        if (CollectionUtils.isNotEmpty(updateTypes)) {
            clientUpdateQueueProducer.putToResidentUpdateQueue(resident.getId(), updateTypes);
        }
    }
}
