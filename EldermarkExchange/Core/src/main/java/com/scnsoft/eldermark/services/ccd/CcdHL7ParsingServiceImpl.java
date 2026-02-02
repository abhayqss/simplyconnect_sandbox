package com.scnsoft.eldermark.services.ccd;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.ccd.templates.sections.*;
import com.scnsoft.eldermark.services.cda.ClinicalDocumentVO;
import com.scnsoft.eldermark.services.cda.templates.header.*;
import org.openhealthtools.mdht.uml.cda.ccd.ContinuityOfCareDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CcdHL7ParsingServiceImpl implements CcdHL7ParsingService {

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


    @Override
    public ClinicalDocumentVO parse(ContinuityOfCareDocument document, Resident resident) {
        // body sections
        final List<AdvanceDirective> advanceDirectives = advanceDirectiveFactory.parseSection(resident, document.getAdvanceDirectivesSection());
        final List<Allergy> allergies = allergiesFactory.parseSection(resident, document.getAlertsSection());
        final List<Encounter> encounters = encountersFactory.parseSection(resident, document.getEncountersSection());
        final List<FamilyHistory> familyHistories = familyHistoryFactory.parseSection(resident, document.getFamilyHistorySection());
        final List<FunctionalStatus> functionalStatuses = functionalStatusFactory.parseSection(resident, document.getFunctionalStatusSection());
        final List<Immunization> immunizations = immunizationsFactory.parseSection(resident, document.getImmunizationsSection());
        final List<MedicalEquipment> medicalEquipments = medicalEquipmentFactory.parseSection(resident, document.getMedicalEquipmentSection());
        final List<Medication> medications = medicationsFactory.parseSection(resident, document.getMedicationsSection());
        final List<Payer> payers = payerFactory.parseSection(resident, document.getPayersSection());
        final List<PlanOfCare> planOfCares = planOfCareFactory.parseSection(resident, document.getPlanOfCareSection());
        final List<Problem> problems = problemsFactory.parseSection(resident, document.getProblemSection());
        final List<Procedure> procedures = proceduresFactory.parseSection(resident, document.getProceduresSection());
        final List<Result> results = resultsFactory.parseSection(resident, document.getResultsSection());
        final List<SocialHistory> socialHistories = socialHistoryFactory.parseSection(resident, document.getSocialHistorySection());
        final List<VitalSign> vitalSigns = vitalSignFactory.parseSection(resident, document.getVitalSignsSection());

        // header sections
        final List<Authenticator> authenticators = authenticatorFactory.parseSection(resident, document.getAuthenticators());
        final List<Author> authors = authorFactory.parseSection(resident, document.getAuthors());
        final List<BasicEntity> authorizations = authorizationFactory.parseSection(resident, document.getAuthorizations());
        final BasicEntity component = componentFactory.parseSection(resident, document.getComponent());
        final Custodian custodian = custodianFactory.parseSection(resident, document.getCustodian());
        final DataEnterer dataEnterer = dataEntererFactory.parseSection(resident, document.getDataEnterer());
        final List<DocumentationOf> documentationOfs = documentationOfFactory.parseSection(resident, document.getDocumentationOfs());
        final List<Informant> informants = informantFactory.parseSection(resident, document.getInformants());
        final List<InformationRecipient> informationRecipients = informationRecipientFactory.parseSection(resident, document.getInformationRecipients());
        final List<BasicEntity> inFulfillmentOfs = inFulfillmentOfFactory.parseSection(resident, document.getInFulfillmentOfs());
        final LegalAuthenticator legalAuthenticator = legalAuthenticatorFactory.parseSection(resident, document.getLegalAuthenticator());
        final List<Participant> participants = participantFactory.parseSection(resident, document.getParticipants());
        // TODO is it OK that only the first PatientRole entry is parsed?
        // TODO check https://jira.scnsoft.com/browse/CCN-1470
        final Resident recordTarget = recordTargetFactory.parseSection(resident.getFacility(), document.getPatientRoles().get(0));

        final ClinicalDocumentVO clinicalDocumentVO = new ClinicalDocumentVO();
        clinicalDocumentVO.setAdvanceDirectives(advanceDirectives);
        clinicalDocumentVO.setAllergies(allergies);
        clinicalDocumentVO.setEncounters(encounters);
        clinicalDocumentVO.setFamilyHistories(familyHistories);
        clinicalDocumentVO.setFunctionalStatuses(functionalStatuses);
        clinicalDocumentVO.setImmunizations(immunizations);
        clinicalDocumentVO.setMedicalEquipments(medicalEquipments);
        clinicalDocumentVO.setMedications(medications);
        clinicalDocumentVO.setPayers(payers);
        clinicalDocumentVO.setPlanOfCares(planOfCares);
        clinicalDocumentVO.setProblems(problems);
        clinicalDocumentVO.setProcedures(procedures);
        clinicalDocumentVO.setResults(results);
        clinicalDocumentVO.setSocialHistories(socialHistories);
        clinicalDocumentVO.setVitalSigns(vitalSigns);

        clinicalDocumentVO.setAuthenticators(authenticators);
        clinicalDocumentVO.setAuthors(authors);
        clinicalDocumentVO.setAuthorizations(authorizations);
        clinicalDocumentVO.setComponent(component);
        clinicalDocumentVO.setCustodian(custodian);
        clinicalDocumentVO.setDataEnterer(dataEnterer);
        clinicalDocumentVO.setDocumentationOfs(documentationOfs);
        clinicalDocumentVO.setInformants(informants);
        clinicalDocumentVO.setInformationRecipients(informationRecipients);
        clinicalDocumentVO.setInFulfillmentOfs(inFulfillmentOfs);
        clinicalDocumentVO.setLegalAuthenticator(legalAuthenticator);
        clinicalDocumentVO.setParticipants(participants);
        clinicalDocumentVO.setRecordTarget(recordTarget);

        return clinicalDocumentVO;
    }

    @Override
    public Resident parsePatientOnly(ContinuityOfCareDocument document, Organization organization) {
        // TODO is it OK that only the first PatientRole entry is parsed?
        return recordTargetFactory.parseSection(organization, document.getPatientRoles().get(0));
    }

}
