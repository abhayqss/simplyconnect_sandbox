package com.scnsoft.eldermark.service.document.cda.parse.consol;

import com.scnsoft.eldermark.entity.Authenticator;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.FunctionalStatus;
import com.scnsoft.eldermark.entity.basic.BasicEntity;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.ccd.*;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.service.document.cda.schema.ClinicalDocumentVO;
import com.scnsoft.eldermark.service.document.templates.cda.factory.header.*;
import com.scnsoft.eldermark.service.document.templates.consol.parser.sections.*;
import org.openhealthtools.mdht.uml.cda.consol.ContinuityOfCareDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsolCcdParsingServiceImpl implements ConsolCcdParsingService {

    @Autowired
    private RecordTargetFactory recordTargetFactory;
    @Autowired
    private ParticipantFactory participantFactory;
    @Autowired
    private LegalAuthenticatorFactory legalAuthenticatorFactory;
    @Autowired
    private InFulfillmentOfFactory inFulfillmentOfFactory;
    @Autowired
    private InformationRecipientFactory informationRecipientFactory;
    @Autowired
    private InformantFactory informantFactory;
    @Autowired
    private DocumentationOfFactory documentationOfFactory;
    @Autowired
    private DataEntererFactory dataEntererFactory;
    @Autowired
    private CustodianFactory custodianFactory;
    @Autowired
    private ComponentFactory componentFactory;
    @Autowired
    private AuthorizationFactory authorizationFactory;
    @Autowired
    private AuthorFactory authorFactory;
    @Autowired
    private AuthenticatorFactory authenticatorFactory;

    @Autowired
    private AdvanceDirectiveParser advanceDirectiveParser;
    @Autowired
    private AllergiesParser allergiesParser;
    @Autowired
    private EncountersParser encountersParser;
    @Autowired
    private FamilyHistoryParser familyHistoryParser;
    @Autowired
    private FunctionalStatusParser functionalStatusParser;
    @Autowired
    private ImmunizationsParser immunizationsParser;
    @Autowired
    private MedicalEquipmentParser medicalEquipmentParser;
    @Autowired
    private MedicationsParser medicationsParser;
    @Autowired
    private PayerParser payerParser;
    @Autowired
    private PlanOfCareParser planOfCareParser;
    @Autowired
    private ProblemsParser problemsParser;
    @Autowired
    private ProceduresParser proceduresParser;
    @Autowired
    private ResultsParser resultsParser;
    @Autowired
    private SocialHistoryParser socialHistoryParser;
    @Autowired
    private VitalSignParser vitalSignParser;

    @Override
    public ClinicalDocumentVO parse(ContinuityOfCareDocument document, Client resident) {
        // parse sections and store new data
        final List<AdvanceDirective> advanceDirectives = advanceDirectiveParser.parseSection(resident, document.getAdvanceDirectivesSection());
        final List<Allergy> allergies = allergiesParser.parseSection(resident, document.getAllergiesSection());
        final List<Encounter> encounters = encountersParser.parseSection(resident, document.getEncountersSection());
        final List<FamilyHistory> familyHistories = familyHistoryParser.parseSection(resident, document.getFamilyHistorySection());
        final List<FunctionalStatus> functionalStatuses = functionalStatusParser.parseSection(resident, document.getFunctionalStatusSection());
        final List<Immunization> immunizations = immunizationsParser.parseSection(resident, document.getImmunizationsSectionEntriesOptional());
        final List<MedicalEquipment> medicalEquipments = medicalEquipmentParser.parseSection(resident, document.getMedicalEquipmentSection());
        final List<Medication> medications = medicationsParser.parseSection(resident, document.getMedicationsSection());
        final List<Payer> payers = payerParser.parseSection(resident, document.getPayersSection());
        final List<PlanOfCare> planOfCares = planOfCareParser.parseSection(resident, document.getPlanOfCareSection());
        final List<Problem> problems = problemsParser.parseSection(resident, document.getProblemSection());
        final List<Procedure> procedures = proceduresParser.parseSection(resident, document.getProceduresSection());
        final List<Result> results = resultsParser.parseSection(resident, document.getResultsSection());
        final List<SocialHistory> socialHistories = socialHistoryParser.parseSection(resident, document.getSocialHistorySection());
        final List<VitalSign> vitalSigns = vitalSignParser.parseSection(resident, document.getVitalSignsSectionEntriesOptional());

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
        final Client recordTarget = recordTargetFactory.parseSection(resident.getCommunity(), document.getPatientRoles().get(0));

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
    public Client parsePatientOnly(ContinuityOfCareDocument document, Community community) {
        // TODO is it OK that only the first PatientRole entry is parsed?
        return recordTargetFactory.parseSection(community, document.getPatientRoles().get(0));
    }

}
