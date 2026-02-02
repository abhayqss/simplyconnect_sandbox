package com.scnsoft.eldermark.services.cda;

import com.scnsoft.eldermark.dao.*;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.services.ccd.templates.sections.*;
import com.scnsoft.eldermark.services.cda.templates.header.*;
import com.scnsoft.eldermark.services.consana.ResidentUpdateQueueProducer;
import com.scnsoft.eldermark.services.consana.model.ResidentUpdateType;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author phomal
 * Created on 4/27/2018.
 */
@Service
public class ClinicalDocumentServiceImpl implements ClinicalDocumentService {

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
    private ResidentDao residentDao;
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
    private MedicationDao medicationsDao;
    @Autowired
    private PayerDao payerDao;
    @Autowired
    private PlanOfCareDao planOfCareDao;
    @Autowired
    private ProblemDao problemsDao;
    @Autowired
    private ProcedureDao proceduresDao;
    @Autowired
    private ResultDao resultsDao;
    @Autowired
    private SocialHistoryDao socialHistoryDao;
    @Autowired
    private VitalSignDao vitalSignDao;

    @Autowired
    private ResidentService residentService;

    @Autowired
    ResidentUpdateQueueProducer residentUpdateQueueProducer;

    @Override
    public ClinicalDocumentVO getClinicalDocument(Long residentId) {
        checkNotNull(residentId);
        return getClinicalDocument(residentId, Collections.singletonList(residentId));
    }

    @Override
    public ClinicalDocumentVO getClinicalDocument(Long mainResidentId, List<Long> residentIds) {
        final ClinicalDocumentVO document = new ClinicalDocumentVO();
        if (CollectionUtils.isEmpty(residentIds)) {
            return document;
        }

        // Restrictions of an aggregated clinical document
        // * ClinicalDocument (C-CDA) SHOULD contain zero or one (0..1) legalAuthenticator (CONF:5579 [1], CONF:1198-5579 [2]).
        // * ClinicalDocument (C-CDA) MAY contain zero or one (0..1) dataEnterer (CONF:5441 [1], CONF:1198-28678 [2]).
        // * ClinicalDocument (C-CDA) SHALL contain exactly one (1..1) custodian (CONF:5519 [1], CONF:1198-5519 [2]).
        // * Though ClinicalDocument may contain multiple (1..*) recordTarget elements (Patient Details section in UI),
        //   I don't think it's the right way of representing a merged record.
        //
        // References:
        // [1] The first reference is to General Header Constraints (templateId = 2.16.840.1.113883.10.20.22.1.1), 2013
        // [2] The second reference is to US Realm Header (V3) Constraints (templateId = 2.16.840.1.113883.10.20.22.1.1 : 2015-08-01), 2015

        // header sections [0..1]

        if (dataEntererFactory.isTemplateIncluded()) {
            final DataEnterer dataEnterer = dataEntererDao.getCcdDataEnterer(mainResidentId);
            document.setDataEnterer(dataEnterer);
        }
        if (custodianFactory.isTemplateIncluded()) {
            final Custodian custodian = custodianDao.getCcdCustodian(mainResidentId);
            document.setCustodian(custodian);
        }
        if (legalAuthenticatorFactory.isTemplateIncluded()) {
            final LegalAuthenticator legalAuthenticator = legalAuthenticatorDao.getCcdLegalAuthenticator(mainResidentId);
            document.setLegalAuthenticator(legalAuthenticator);
        }
        if (recordTargetFactory.isTemplateIncluded()) {
            final Resident resident = residentDao.get(mainResidentId);
            document.setRecordTarget(resident);
        }

        // header sections [0..*]

        if (authorFactory.isTemplateIncluded()) {
            document.setAuthors(new ArrayList<>(authorDao.listByResidentIds(residentIds)));
        }
        if (informantFactory.isTemplateIncluded()) {
            document.setInformants(new ArrayList<>(informantDao.listByResidentIds(residentIds)));
        }
        if (informationRecipientFactory.isTemplateIncluded()) {
            document.setInformationRecipients(new ArrayList<>(informationRecipientDao.listByResidentIds(residentIds)));
        }
        if (authenticatorFactory.isTemplateIncluded()) {
            document.setAuthenticators(new ArrayList<>(authenticatorDao.listByResidentIds(residentIds)));
        }
        if (participantFactory.isTemplateIncluded()) {
            document.setParticipants(new ArrayList<>(participantDao.listByResidentIds(residentIds)));
        }
        if (documentationOfFactory.isTemplateIncluded()) {
            document.setDocumentationOfs(new ArrayList<>(documentationOfDao.listByResidentIds(residentIds)));
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
            document.setAdvanceDirectives(new ArrayList<>(advanceDirectiveDao.listByResidentIds(residentIds)));
        }
        if (allergiesFactory.isTemplateIncluded()) {
            document.setAllergies(new ArrayList<>(allergiesDao.listByResidentIds(residentIds)));
        }
        if (encountersFactory.isTemplateIncluded()) {
            document.setEncounters(new ArrayList<>(encountersDao.listByResidentIds(residentIds)));
        }
        if (familyHistoryFactory.isTemplateIncluded()) {
            document.setFamilyHistories(new ArrayList<>(familyHistoryDao.listByResidentIds(residentIds)));
        }
        if (functionalStatusFactory.isTemplateIncluded()) {
            document.setFunctionalStatuses(new ArrayList<>(functionalStatusDao.listByResidentIds(residentIds)));
        }
        if (immunizationsFactory.isTemplateIncluded()) {
            document.setImmunizations(new ArrayList<>(immunizationsDao.listByResidentIds(residentIds)));
        }
        if (medicalEquipmentFactory.isTemplateIncluded()) {
            document.setMedicalEquipments(new ArrayList<>(medicalEquipmentDao.listByResidentIds(residentIds)));
        }
        if (medicationsFactory.isTemplateIncluded()) {
            document.setMedications(new ArrayList<>(medicationsDao.listByResidentIds(residentIds)));
        }
        if (payerFactory.isTemplateIncluded()) {
            document.setPayers(new ArrayList<>(payerDao.listByResidentIds(residentIds)));
        }
        if (planOfCareFactory.isTemplateIncluded()) {
            document.setPlanOfCares(new ArrayList<>(planOfCareDao.listByResidentIds(residentIds)));
        }
        if (problemsFactory.isTemplateIncluded()) {
            document.setProblems(new ArrayList<>(problemsDao.listByResidentIds(residentIds)));
        }
        if (proceduresFactory.isTemplateIncluded()) {
            document.setProcedures(new ArrayList<>(proceduresDao.listByResidentIds(residentIds)));
        }
        if (resultsFactory.isTemplateIncluded()) {
            document.setResults(new ArrayList<>(resultsDao.listByResidentIds(residentIds)));
        }
        if (socialHistoryFactory.isTemplateIncluded()) {
            document.setSocialHistories(new ArrayList<>(socialHistoryDao.listByResidentIds(residentIds)));
        }
        if (vitalSignFactory.isTemplateIncluded()) {
            document.setVitalSigns(new ArrayList<>(vitalSignDao.listByResidentIds(residentIds)));
        }

        return document;
    }

    @Override
    public void deleteByResidentId(Long residentId) {
        checkNotNull(residentId);

        // header sections
        if (dataEntererFactory.isTemplateIncluded()) {
            dataEntererDao.deleteByResidentId(residentId);
        }
        if (custodianFactory.isTemplateIncluded()) {
            custodianDao.deleteByResidentId(residentId);
        }
        if (legalAuthenticatorFactory.isTemplateIncluded()) {
            legalAuthenticatorDao.deleteByResidentId(residentId);
        }
        if (authorFactory.isTemplateIncluded()) {
            authorDao.deleteByResidentId(residentId);
        }
        if (informantFactory.isTemplateIncluded()) {
            informantDao.deleteByResidentId(residentId);
        }
        if (informationRecipientFactory.isTemplateIncluded()) {
            informationRecipientDao.deleteByResidentId(residentId);
        }
        if (authenticatorFactory.isTemplateIncluded()) {
            authenticatorDao.deleteByResidentId(residentId);
        }
        if (participantFactory.isTemplateIncluded()) {
            participantDao.deleteByResidentId(residentId);
        }
        if (documentationOfFactory.isTemplateIncluded()) {
            documentationOfDao.deleteByResidentId(residentId);
        }

        // stubs - not implemented for an unknown reason
        if (inFulfillmentOfFactory.isTemplateIncluded()) ;  // do nothing
        if (authorizationFactory.isTemplateIncluded()) ;    // do nothing
        if (componentFactory.isTemplateIncluded()) ;        // do nothing

        // body sections
        if (advanceDirectiveFactory.isTemplateIncluded()) {
            advanceDirectiveDao.deleteByResidentId(residentId);
        }
        if (allergiesFactory.isTemplateIncluded()) {
            allergiesDao.deleteByResidentId(residentId);
        }
        if (encountersFactory.isTemplateIncluded()) {
            encountersDao.deleteByResidentId(residentId);
        }
        if (familyHistoryFactory.isTemplateIncluded()) {
            familyHistoryDao.deleteByResidentId(residentId);
        }
        if (functionalStatusFactory.isTemplateIncluded()) {
            functionalStatusDao.deleteByResidentId(residentId);
        }
        if (immunizationsFactory.isTemplateIncluded()) {
            immunizationsDao.deleteByResidentId(residentId);
        }
        if (medicalEquipmentFactory.isTemplateIncluded()) {
            medicalEquipmentDao.deleteByResidentId(residentId);
        }
        if (medicationsFactory.isTemplateIncluded()) {
            medicationsDao.deleteByResidentId(residentId);
        }
        if (payerFactory.isTemplateIncluded()) {
            payerDao.deleteByResidentId(residentId);
        }
        if (planOfCareFactory.isTemplateIncluded()) {
            planOfCareDao.deleteByResidentId(residentId);
        }
        if (problemsFactory.isTemplateIncluded()) {
            problemsDao.deleteByResidentId(residentId);
        }
        if (proceduresFactory.isTemplateIncluded()) {
            proceduresDao.deleteByResidentId(residentId);
        }
        if (resultsFactory.isTemplateIncluded()) {
            resultsDao.deleteByResidentId(residentId);
        }
        if (socialHistoryFactory.isTemplateIncluded()) {
            socialHistoryDao.deleteByResidentId(residentId);
        }
        if (vitalSignFactory.isTemplateIncluded()) {
            vitalSignDao.deleteByResidentId(residentId);
        }
    }

    @Override
    public void saveClinicalDocument(Resident resident, ClinicalDocumentVO clinicalDocumentVO) {
        checkNotNull(resident);
        checkNotNull(clinicalDocumentVO);

        // TODO iterate resident-related entities and change ownership from RecordTarget to Resident ?

        final Set<ResidentUpdateType> updateTypes = new HashSet<>();
        // header sections [0..1]
        if (dataEntererFactory.isTemplateIncluded() && clinicalDocumentVO.getDataEnterer() != null) {
            dataEntererDao.create(clinicalDocumentVO.getDataEnterer());
        }
        if (custodianFactory.isTemplateIncluded() && clinicalDocumentVO.getCustodian() != null) {
            custodianDao.create(clinicalDocumentVO.getCustodian());
        }
        if (legalAuthenticatorFactory.isTemplateIncluded() && clinicalDocumentVO.getLegalAuthenticator() != null) {
            legalAuthenticatorDao.create(clinicalDocumentVO.getLegalAuthenticator());
        }

        // header sections [0..*]
        if (authorFactory.isTemplateIncluded()) {
            authorDao.create(clinicalDocumentVO.getAuthors());
        }
        if (informantFactory.isTemplateIncluded()) {
            informantDao.create(clinicalDocumentVO.getInformants());
        }
        if (informationRecipientFactory.isTemplateIncluded()) {
            informationRecipientDao.create(clinicalDocumentVO.getInformationRecipients());
        }
        if (authenticatorFactory.isTemplateIncluded()) {
            authenticatorDao.create(clinicalDocumentVO.getAuthenticators());
        }
        if (participantFactory.isTemplateIncluded()) {
            participantDao.create(clinicalDocumentVO.getParticipants());
        }
        if (documentationOfFactory.isTemplateIncluded()) {
            documentationOfDao.create(clinicalDocumentVO.getDocumentationOfs());
        }

        // stubs - not implemented for an unknown reason
        if (inFulfillmentOfFactory.isTemplateIncluded()) ;  // do nothing
        if (authorizationFactory.isTemplateIncluded()) ;    // do nothing
        if (componentFactory.isTemplateIncluded()) ;        // do nothing

        // body sections
        if (advanceDirectiveFactory.isTemplateIncluded()) {
            advanceDirectiveDao.create(clinicalDocumentVO.getAdvanceDirectives());
        }
        if (allergiesFactory.isTemplateIncluded()) {
            allergiesDao.create(clinicalDocumentVO.getAllergies());
        }
        if (encountersFactory.isTemplateIncluded()) {
            if (CollectionUtils.isNotEmpty(encountersDao.create(clinicalDocumentVO.getEncounters()))) {
                updateTypes.add(ResidentUpdateType.ENCOUNTER);
            }
        }
        if (familyHistoryFactory.isTemplateIncluded()) {
            familyHistoryDao.create(clinicalDocumentVO.getFamilyHistories());
        }
        if (functionalStatusFactory.isTemplateIncluded()) {
            functionalStatusDao.create(clinicalDocumentVO.getFunctionalStatuses());
        }
        if (immunizationsFactory.isTemplateIncluded()) {
            immunizationsDao.create(clinicalDocumentVO.getImmunizations());
        }
        if (medicalEquipmentFactory.isTemplateIncluded()) {
            medicalEquipmentDao.create(clinicalDocumentVO.getMedicalEquipments());
        }
        if (medicationsFactory.isTemplateIncluded()) {
            medicationsDao.create(clinicalDocumentVO.getMedications());
        }
        if (payerFactory.isTemplateIncluded()) {
            payerDao.create(clinicalDocumentVO.getPayers());
        }
        if (planOfCareFactory.isTemplateIncluded()) {
            planOfCareDao.create(clinicalDocumentVO.getPlanOfCares());
        }
        if (problemsFactory.isTemplateIncluded()) {
            problemsDao.create(clinicalDocumentVO.getProblems());
        }
        if (proceduresFactory.isTemplateIncluded()) {
            proceduresDao.create(clinicalDocumentVO.getProcedures());
        }
        if (resultsFactory.isTemplateIncluded()) {
            resultsDao.create(clinicalDocumentVO.getResults());
        }
        if (socialHistoryFactory.isTemplateIncluded()) {
            socialHistoryDao.create(clinicalDocumentVO.getSocialHistories());
        }
        if (vitalSignFactory.isTemplateIncluded()) {
            vitalSignDao.create(clinicalDocumentVO.getVitalSigns());
        }

        residentService.updateResident(resident);
        residentUpdateQueueProducer.putToResidentUpdateQueue(resident.getId(), updateTypes);
    }

}
