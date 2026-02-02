package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Participant;
import com.scnsoft.eldermark.entity.document.ccd.Payer;
import com.scnsoft.eldermark.entity.document.ccd.PolicyActivity;
import com.scnsoft.eldermark.service.document.cda.CcdCodeFactory;
import com.scnsoft.eldermark.service.document.templates.ccd.factory.entries.CcdSectionEntryFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.OptionalTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.factory.sections.RequiredTemplateFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.Performer2Factory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.entries.PersonFactory;
import com.scnsoft.eldermark.service.document.templates.cda.parser.sections.ParsableSectionFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.cda.CcdParseUtils;
import com.scnsoft.eldermark.util.cda.CcdTransform;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.CoverageActivity;
import org.openhealthtools.mdht.uml.cda.ccd.PayersSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.scnsoft.eldermark.entity.document.ccd.CodeSystem.*;

/**
 * <h1>Payers</h1> “This section describes payers and the coverage they provide
 * for defined activities. For each payer, “all the pertinent data needed to
 * contact, bill to, and collect from that payer should be included.
 * Authorization information that can be used to define pertinent referral,
 * authorization tracking number, procedure, therapy, intervention, device, or
 * similar authorizations for the patient or provider or both should be
 * included.” [CCD 3.1]
 *
 * @see CoveragePlanDescription
 * @see Payer
 * @see Person
 * @see Participant
 * @see Organization
 * @see AuthorizationActivity
 * @see PolicyActivity
 * @see Client
 * @see CcdCode
 */
@Component
public class PayerFactory extends OptionalTemplateFactory implements ParsableSectionFactory<PayersSection, Payer> {

    private static CcdSectionEntryFactory ccdSectionEntryFactory = CcdSectionEntryFactory.INSTANCE;

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private PersonFactory personFactory;

    @Autowired
    private Performer2Factory performer2Factory;

    @Value("${section.payers.enabled}")
    private boolean isTemplateIncluded;

    private static final String LEGACY_TABLE = "NWHIN_PAYER";
    private static final String PAYER_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.87";
    private static final String GUARANTOR_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.88";

    private static String buildSectionText(Collection<Payer> payers) {

        if (CollectionUtils.isEmpty(payers)) {
            return "No known payers.";
        }

        StringBuilder sectionText = new StringBuilder("<table>" +
                "<thead>" +
                "<tr>" +
                "<th>Company Name</th>" +
                "<th>Policy Number</th>" +
                "<th>Coverage Dates</th>" +
                "</tr>" +
                "</thead>");

        var body = new StringBuilder();

        for (Payer payer : payers) {
            if (CollectionUtils.isNotEmpty(payer.getPolicyActivities())) {
                for (PolicyActivity policyActivity : payer.getPolicyActivities()) {
                    body.append("<tr>");
                    if (policyActivity.getPayerCommunity() != null) {
                        CcdUtils.addCellToSectionText(policyActivity.getPayerCommunity().getName(), body);
                    } else {
                        CcdUtils.addEmptyCell(body);
                    }
                    CcdUtils.addCellToSectionText(policyActivity.getParticipantMemberId(), body);

                    body.append("<td>");
                    Participant participant = policyActivity.getParticipant();
                    if (participant != null
                            && (participant.getTimeHigh() != null || participant.getTimeLow() != null)) {
                        CcdUtils.addDateRangeToSectionText(participant.getTimeLow(), participant.getTimeHigh(),
                                body);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(body);
                    }
                    body.append("</td>");
                    body.append("</tr>");
                }
            }
        }

        if (body.length() == 0) {
            return "No known payers.";
        }

        CcdUtils.addContent(sectionText, body, CcdUtils.ContentTag.TBODY);
        sectionText.append("</table>");

        return sectionText.toString();
    }

    @Override
    public PayersSection buildTemplateInstance(Collection<Payer> payers) {
        final PayersSection payersSection = CCDFactory.eINSTANCE.createPayersSection();
        payersSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.9"));

        final CE sectionCode = CcdUtils.createCE("48768-6", "Payers", CodeSystem.LOINC);
        payersSection.setCode(sectionCode);

        payersSection.setTitle(DatatypesFactory.eINSTANCE.createST("Payers"));

        payersSection.createStrucDocText(buildSectionText(payers));

        for (Payer payer : payers) {
            CoverageActivity coverageActivity = CCDFactory.eINSTANCE.createCoverageActivity();
            // List<CoverageActivity> coverageActivityList =
            // payersSection.getCoverageActivities();

            // coverageActivityList.add(coverageActivity);
            payersSection.addAct(coverageActivity);

            coverageActivity.setClassCode(x_ActClassDocumentEntryAct.ACT);
            coverageActivity.setMoodCode(x_DocumentActMood.EVN);

            coverageActivity.getTemplateIds()
                    .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.60"));

            coverageActivity.getIds().add(CcdUtils.getId(payer.getCoverageActivityId()));

            coverageActivity.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

            CE code = CcdUtils.createCE("48768-6", "Payment Sources", CodeSystem.LOINC);
            coverageActivity.setCode(code);

            List<PolicyActivity> policyActivityList = payer.getPolicyActivities();

            if (CollectionUtils.isNotEmpty(policyActivityList)) {

                for (PolicyActivity policyActivity : policyActivityList) {
                    org.openhealthtools.mdht.uml.cda.ccd.PolicyActivity policyActivityCcd = CCDFactory.eINSTANCE
                            .createPolicyActivity();
                    // coverageActivity.getPolicyActivities().add(policyActivityCcd);
                    coverageActivity.addAct(policyActivityCcd);
                    ((EntryRelationship) policyActivityCcd.eContainer())
                            .setTypeCode(x_ActRelationshipEntryRelationship.COMP);
                    if (policyActivity.getSequenceNumber() != null) {
                        INT sqNum = DatatypesFactory.eINSTANCE.createINT();
                        sqNum.setValue(policyActivity.getSequenceNumber());
                        ((EntryRelationship) policyActivityCcd.eContainer()).setSequenceNumber(sqNum);
                    }
                    policyActivityCcd.setClassCode(x_ActClassDocumentEntryAct.ACT);
                    policyActivityCcd.setMoodCode(x_DocumentActMood.EVN);
                    policyActivityCcd.getTemplateIds()
                            .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.61"));
                    policyActivityCcd.getIds().add(CcdUtils.getId(policyActivity.getId()));
                    policyActivityCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

                    policyActivityCcd.setCode(
                            CcdUtils.createCE(policyActivity.getHealthInsuranceTypeCode(), X12N_1336.getOid()));

                    Performer2 payerPerformer = CDAFactory.eINSTANCE.createPerformer2();
                    payerPerformer.setTypeCode(ParticipationPhysicalPerformer.PRF);
                    payerPerformer.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(PAYER_TEMPLATE_ID));
                    policyActivityCcd.getPerformers().add(payerPerformer);
                    Community payerOrganization = policyActivity.getPayerCommunity();
                    if (payerOrganization != null) {
                        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                        payerPerformer.setAssignedEntity(assignedEntity);

                        if (policyActivity.getPayerFinanciallyResponsiblePartyCode() != null) {
                            assignedEntity.setCode(CcdUtils.createCE(
                                    policyActivity.getPayerFinanciallyResponsiblePartyCode(), ROLE_CLASS.getOid()));
                        }
                        createOrganization(assignedEntity, payerOrganization);
                    } else {
                        payerPerformer.setNullFlavor(NullFlavor.NI);
                        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                        payerPerformer.setAssignedEntity(assignedEntity);
                        assignedEntity.setNullFlavor(NullFlavor.NI);
                        assignedEntity.getIds().add(CcdUtils.getNullId());
                    }

                    if (policyActivity.getGuarantorCommunity() != null
                            || policyActivity.getGuarantorPerson() != null) {
                        Performer2 guarantorPerformer = CDAFactory.eINSTANCE.createPerformer2();
                        policyActivityCcd.getPerformers().add(guarantorPerformer);
                        guarantorPerformer.setTypeCode(ParticipationPhysicalPerformer.PRF);
                        guarantorPerformer.getTemplateIds()
                                .add(DatatypesFactory.eINSTANCE.createII(GUARANTOR_TEMPLATE_ID));

                        if (policyActivity.getGuarantorTime() != null) {
                            guarantorPerformer
                                    .setTime(CcdUtils.convertEffectiveTime(policyActivity.getGuarantorTime()));
                        }
                        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                        guarantorPerformer.setAssignedEntity(assignedEntity);

                        assignedEntity.setCode(DatatypesFactory.eINSTANCE.createCE("GUAR", ROLE_CODE.getOid()));

                        Person guarantorPerson = policyActivity.getGuarantorPerson();
                        if (guarantorPerson != null) {
                            assignedEntity.getIds().add(CcdUtils.getId(guarantorPerson.getId())); // TODO that will be
                            // not unique if orgId
                            // == personId
                            if (!CollectionUtils.isEmpty(guarantorPerson.getAddresses())) {
                                CcdUtils.addConvertedAddress(assignedEntity.getAddrs(),
                                        guarantorPerson.getAddresses().get(0));
                            }
                            if (!CollectionUtils.isEmpty(guarantorPerson.getTelecoms())) {
                                CcdUtils.addConvertedTelecom(assignedEntity.getTelecoms(),
                                        guarantorPerson.getTelecoms().get(0));
                            }
                            assignedEntity.setAssignedPerson(ccdSectionEntryFactory.buildPerson(guarantorPerson));
                        } else {
                            createOrganization(assignedEntity, policyActivity.getGuarantorCommunity());
                        }
                    }

                    Participant participantModel = policyActivity.getParticipant();
                    Participant2 participantCcd = CDAFactory.eINSTANCE.createParticipant2();
                    participantCcd.setTypeCode(ParticipationType.COV);
                    participantCcd.getTemplateIds()
                            .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.89"));
                    if (participantModel != null) {

                        if (participantModel.getTimeLow() != null || participantModel.getTimeHigh() != null) {
                            participantCcd.setTime(CcdUtils.convertEffectiveTime(participantModel.getTimeLow(),
                                    participantModel.getTimeHigh()));
                        }

                        ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
                        participantCcd.setParticipantRole(participantRole);

                        participantRole.getIds().add(CcdUtils.getId(policyActivity.getParticipantMemberId()));

                        if (participantModel.getRoleCode() != null) {
                            participantRole
                                    .setCode(CcdUtils.createCE(participantModel.getRoleCode(), ROLE_CODE.getOid()));
                        }

                        Person person = participantModel.getPerson();
                        if (person != null) {
                            if (!CollectionUtils.isEmpty(person.getAddresses())) {
                                CcdUtils.addConvertedAddress(participantRole.getAddrs(), person.getAddresses().get(0));
                            }

                            PlayingEntity playingEntity = CDAFactory.eINSTANCE.createPlayingEntity();
                            participantRole.setPlayingEntity(playingEntity);

                            if (!CollectionUtils.isEmpty(person.getNames())) {
                                CcdUtils.addConvertedName(playingEntity.getNames(), person.getNames().get(0));
                            } else {
                                playingEntity.getNames().add(CcdUtils.getNullName());
                            }

                            if (policyActivity.getParticipantDateOfBirth() != null) {
                                playingEntity.setSDTCBirthTime(
                                        CcdUtils.convertEffectiveTime(policyActivity.getParticipantDateOfBirth()));
                            }
                        }
                    } else {
                        participantCcd.setNullFlavor(NullFlavor.NI);
                        ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
                        participantRole.setNullFlavor(NullFlavor.NI);
                        participantCcd.setParticipantRole(participantRole);
                    }
                    policyActivityCcd.getParticipants().add(participantCcd);

                    Participant subscriberModel = policyActivity.getSubscriber();
                    if (subscriberModel != null) {

                        Participant2 subscriberCcd = CDAFactory.eINSTANCE.createParticipant2();

                        policyActivityCcd.getParticipants().add(subscriberCcd);
                        subscriberCcd.getTemplateIds()
                                .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.90"));
                        subscriberCcd.setTypeCode(ParticipationType.HLD);

                        if (subscriberModel.getTimeLow() != null) {
                            subscriberCcd.setTime(CcdUtils.convertEffectiveTime(subscriberModel.getTimeLow()));
                        }

                        ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
                        subscriberCcd.setParticipantRole(participantRole);

                        participantRole.getIds().add(CcdUtils.getId(subscriberModel.getId()));

                        Person person = subscriberModel.getPerson();
                        if (person != null) {
                            if (!CollectionUtils.isEmpty(person.getAddresses())) {
                                CcdUtils.addConvertedAddress(participantRole.getAddrs(), person.getAddresses().get(0));
                            }
                        }
                    }

                    if (!CollectionUtils.isEmpty(policyActivity.getAuthorizationActivities())) {
                        for (AuthorizationActivity authorizationActivity : policyActivity
                                .getAuthorizationActivities()) {

                            Act act = CDAFactory.eINSTANCE.createAct();
                            act.setClassCode(x_ActClassDocumentEntryAct.ACT);
                            act.setMoodCode(x_DocumentActMood.EVN);
                            policyActivityCcd.addAct(act);

                            ((EntryRelationship) act.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);
                            act.getTemplateIds()
                                    .add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.19"));
                            act.getIds().add(CcdUtils.getId(authorizationActivity.getId()));

                            CD nullCD = DatatypesFactory.eINSTANCE.createCD();
                            nullCD.setNullFlavor(NullFlavor.NA);
                            act.setCode(nullCD);

                            if (!CollectionUtils.isEmpty(authorizationActivity.getClinicalStatements())) {
                                for (CcdCode statement : authorizationActivity.getClinicalStatements()) {
                                    Procedure procedure = CDAFactory.eINSTANCE.createProcedure();
                                    act.addProcedure(procedure);
                                    ((EntryRelationship) procedure.eContainer())
                                            .setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
                                    procedure.setMoodCode(x_DocumentProcedureMood.PRMS);
                                    procedure.setClassCode(ActClass.PROC);
                                    procedure.setCode(CcdUtils.createCE(statement, SNOMED_CT.getOid()));
                                }
                            } else {
                                Procedure procedure = CDAFactory.eINSTANCE.createProcedure();
                                act.addProcedure(procedure);
                                ((EntryRelationship) procedure.eContainer())
                                        .setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
                                procedure.setNullFlavor(NullFlavor.NI);
                            }

                            if (!CollectionUtils.isEmpty(authorizationActivity.getPerformers())) {
                                for (Person performer : authorizationActivity.getPerformers()) {
                                    act.getPerformers().add(ccdSectionEntryFactory.buildPerformer2(performer));
                                }
                            }
                        }
                    } else if (!CollectionUtils.isEmpty(policyActivity.getCoveragePlanDescriptions())) {
                        for (CoveragePlanDescription planDescription : policyActivity.getCoveragePlanDescriptions()) {
                            Act act = CDAFactory.eINSTANCE.createAct();
                            policyActivityCcd.addAct(act);
                            ((EntryRelationship) act.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);

                            act.setClassCode(x_ActClassDocumentEntryAct.ACT);
                            act.setMoodCode(x_DocumentActMood.DEF);

                            act.getIds().add(CcdUtils.getId(planDescription.getId()));

                            // HMO is a policy for a health plan that provides coverage for health care only
                            // through contracted or employed physicians and hospitals located in particular
                            // geographic or service areas. HMOs emphasize prevention and early detection of
                            // illness. Eligibility to enroll in an HMO is determined by where a covered
                            // party lives or works.
                            final CD actCode = CcdUtils.createCD("HMO", "health maintenance organization policy",
                                    HL7_ACT_CODE);
                            act.setCode(actCode);

                            // ED text = DatatypesFactory.eINSTANCE.createED();
                            if (planDescription.getText() != null) {
                                act.setText(DatatypesFactory.eINSTANCE.createED(planDescription.getText()));
                            }
                        }
                    } else {
                        EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                        entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.REFR);
                        entryRelationship.setAct(buildNullAuthorizationActivity());
                        policyActivityCcd.getEntryRelationships().add(entryRelationship);
                    }
                }
            } else {
                EntryRelationship entryRelationship = CDAFactory.eINSTANCE.createEntryRelationship();
                entryRelationship.setTypeCode(x_ActRelationshipEntryRelationship.COMP);
                entryRelationship.setAct(buildNullPolicyActivity());
                coverageActivity.getEntryRelationships().add(entryRelationship);
            }
        }
        return payersSection;
    }

    private Act buildNullPolicyActivity() {
        Act policyActivityCcd = CDAFactory.eINSTANCE.createAct();

        policyActivityCcd.setClassCode(x_ActClassDocumentEntryAct.ACT);
        policyActivityCcd.setMoodCode(x_DocumentActMood.EVN);
        policyActivityCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.61"));
        policyActivityCcd.getIds().add(CcdUtils.getNullId());
        policyActivityCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

        Performer2 payerPerformer = CDAFactory.eINSTANCE.createPerformer2();
        payerPerformer.setTypeCode(ParticipationPhysicalPerformer.PRF);
        payerPerformer.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.87"));
        payerPerformer.setNullFlavor(NullFlavor.NI);
        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
        assignedEntity.setNullFlavor(NullFlavor.NI);
        assignedEntity.getIds().add(CcdUtils.getNullId());
        payerPerformer.setAssignedEntity(assignedEntity);
        policyActivityCcd.getPerformers().add(payerPerformer);

        Participant2 participantCcd = CDAFactory.eINSTANCE.createParticipant2();
        participantCcd.setNullFlavor(NullFlavor.NI);
        participantCcd.setTypeCode(ParticipationType.COV);
        participantCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.89"));
        ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
        participantCcd.setParticipantRole(participantRole);
        participantRole.getIds().add(CcdUtils.getNullId());
        participantRole.setNullFlavor(NullFlavor.NI);
        participantCcd.setParticipantRole(participantRole);
        policyActivityCcd.getParticipants().add(participantCcd);

        policyActivityCcd.addAct(buildNullAuthorizationActivity());

        return policyActivityCcd;
    }

    private Act buildNullAuthorizationActivity() {
        Act act = CDAFactory.eINSTANCE.createAct();
        act.setClassCode(x_ActClassDocumentEntryAct.ACT);
        act.setMoodCode(x_DocumentActMood.EVN);

        act.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.19"));
        act.getIds().add(CcdUtils.getNullId());

        CD nullCD = DatatypesFactory.eINSTANCE.createCD();
        nullCD.setNullFlavor(NullFlavor.NA);
        act.setCode(nullCD);

        Procedure procedure = CDAFactory.eINSTANCE.createProcedure();
        act.addProcedure(procedure);
        ((EntryRelationship) procedure.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
        procedure.setMoodCode(x_DocumentProcedureMood.PRMS);
        procedure.setClassCode(ActClass.PROC);
        procedure.setCode(CcdUtils.createNillCode());
        procedure.setNullFlavor(NullFlavor.NI);

        return act;
    }

    private static void createOrganization(AssignedEntity assignedEntity, Community community) {
        assignedEntity.getIds().add(CcdUtils.getId(community.getId()));
        if (!CollectionUtils.isEmpty(community.getAddresses())) {
            CcdUtils.addConvertedAddress(assignedEntity.getAddrs(), community.getAddresses().get(0));
        }
        CcdUtils.addConvertedTelecom(assignedEntity.getTelecoms(), community.getTelecom());
        assignedEntity.getRepresentedOrganizations().add(ccdSectionEntryFactory.buildOrganization(community, false));
    }

    @Override
    public List<Payer> parseSection(Client client, PayersSection payersSection) {
        if (!CcdParseUtils.hasContent(payersSection)
                || CollectionUtils.isEmpty(payersSection.getCoverageActivities())) {
            return Collections.emptyList();
        }
        checkNotNull(client);

        final List<Payer> payers = new ArrayList<>();
        for (CoverageActivity ccdPayerCoverageActivity : payersSection.getCoverageActivities()) {
            Payer payer = new Payer();
            payer.setOrganization(client.getOrganization());
            payer.setClient(client);
            payer.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdPayerCoverageActivity.getIds()));

            II coverageActivityId = CcdParseUtils.getFirstNotEmptyValue(ccdPayerCoverageActivity.getIds(), II.class);
            Pair<String, String> rootAndExtension = CcdParseUtils.getRootAndExt(coverageActivityId);
            if (rootAndExtension != null)
                payer.setCoverageActivityId(rootAndExtension.getSecond());

            if (!CollectionUtils.isEmpty(ccdPayerCoverageActivity.getPolicyActivities())) {
                List<PolicyActivity> policyActivities = new ArrayList<>();
                for (org.openhealthtools.mdht.uml.cda.ccd.PolicyActivity ccdPolicyActivity : ccdPayerCoverageActivity
                        .getPolicyActivities()) {
                    if (CcdParseUtils.hasContent(ccdPolicyActivity)) {
                        PolicyActivity policyActivity = new PolicyActivity();
                        policyActivity.setOrganization(client.getOrganization());

                        policyActivity.setHealthInsuranceTypeCode(ccdCodeFactory.convert(ccdPolicyActivity.getCode()));

                        for (EntryRelationship entryRelationship : ccdPolicyActivity.getEntryRelationships()) {
                            if (entryRelationship.getTypeCode() == x_ActRelationshipEntryRelationship.COMP) {
                                INT sequenceNumber = entryRelationship.getSequenceNumber();
                                if (CcdParseUtils.hasContent(sequenceNumber)) {
                                    policyActivity.setSequenceNumber(sequenceNumber.getValue());
                                    break;
                                }
                            }
                        }

                        // In order to distinguish between Payer and Guarantor we use templateId here
                        List<Performer2> payerPerformers = CcdParseUtils
                                .findByTemplateId(ccdPolicyActivity.getPerformers(), PAYER_TEMPLATE_ID);
                        if (CollectionUtils.isEmpty(payerPerformers)) {
                            payerPerformers = CcdParseUtils.findByCode(ccdPolicyActivity.getPerformers(), "PAYOR");
                        }
                        if (!CollectionUtils.isEmpty(payerPerformers)) {
                            AssignedEntity assignedEntity = payerPerformers.get(0).getAssignedEntity();
                            if (CcdParseUtils.hasContent(assignedEntity)) {
                                policyActivity.setPayerFinanciallyResponsiblePartyCode(
                                        ccdCodeFactory.convert(assignedEntity.getCode()));

                                if (CollectionUtils.isEmpty(assignedEntity.getRepresentedOrganizations())) {
                                    personFactory.parse(assignedEntity, client.getOrganization(), LEGACY_TABLE);
                                    // TODO where to save PayerPerson?
                                } else {
                                    // TODO added default organization
                                    policyActivity.setPayerCommunity(client.getCommunity());
                                }
                            }
                        }

                        // In order to distinguish between Payer and Guarantor we use templateId here
                        List<Performer2> guarantorPerformers = CcdParseUtils
                                .findByTemplateId(ccdPolicyActivity.getPerformers(), GUARANTOR_TEMPLATE_ID);
                        if (CollectionUtils.isEmpty(guarantorPerformers)) {
                            guarantorPerformers = CcdParseUtils.findByCode(ccdPolicyActivity.getPerformers(), "GUAR");
                        }
                        if (!CollectionUtils.isEmpty(guarantorPerformers)) {
                            Performer2 guarantorPerformer = guarantorPerformers.get(0);

                            policyActivity
                                    .setGuarantorTime(CcdParseUtils.convertTsToDate(guarantorPerformer.getTime()));

                            AssignedEntity assignedEntity = guarantorPerformer.getAssignedEntity();
                            if (assignedEntity != null) {
                                if (CollectionUtils.isEmpty(assignedEntity.getRepresentedOrganizations())) {
                                    Person guarantorPerson = personFactory.parse(assignedEntity,
                                            client.getOrganization(), LEGACY_TABLE);
                                    policyActivity.setGuarantorPerson(guarantorPerson);
                                } else {
                                    // TODO added default organization
                                    policyActivity.setGuarantorCommunity(client.getCommunity());
                                }
                            }
                        }

                        for (Participant2 participant2 : ccdPolicyActivity.getParticipants()) {
                            ParticipantRole participantRole = participant2.getParticipantRole();

                            Participant participant = new Participant();
                            participant.setOrganization(client.getOrganization());
                            participant.setLegacyTable(LEGACY_TABLE);
                            if (participantRole != null) {
                                participant.setLegacyId(CcdParseUtils.getFirstIdExtension(participantRole.getIds()));
                            }

                            if (participant2.getTypeCode() == ParticipationType.COV) {
                                // ParticipantRole coveredParty = ccdPolicyActivity.getCoveredParty();

                                Pair<Date, Date> time = CcdTransform.IVLTStoHighLowDate(participant2.getTime());
                                if (time != null) {
                                    participant.setTimeHigh(time.getFirst());
                                    participant.setTimeLow(time.getSecond());
                                }

                                if (participantRole != null) {
                                    participant.setRoleCode(ccdCodeFactory.convert(participantRole.getCode()));
                                    II participantRoleId = CcdParseUtils.getFirstNotEmptyValue(participantRole.getIds(),
                                            II.class);
                                    if (participantRoleId != null) {
                                        policyActivity.setParticipantMemberId(participantRoleId.getExtension());
                                    }

                                    PlayingEntity playingEntity = participantRole.getPlayingEntity();
                                    if (playingEntity != null) {
                                        Person coverageTargetPerson = CcdParseUtils.createPerson(participantRole,
                                                client.getOrganization(), LEGACY_TABLE);
                                        participant.setPerson(coverageTargetPerson);
                                        policyActivity.setParticipantDateOfBirth(
                                                CcdParseUtils.convertTsToDate(playingEntity.getSDTCBirthTime()));
                                    }
                                }

                                // Coverage target
                                policyActivity.setParticipant(participant);
                            } else if (participant2.getTypeCode() == ParticipationType.HLD) {
                                // ParticipantRole subscriber = ccdPolicyActivity.getSubscriber();
                                // TODO retrieve holder (subscriber participant) by ID?

                                Pair<Date, Date> highLowDate = CcdTransform.IVLTStoHighLowDate(participant2.getTime());
                                if (highLowDate != null) {
                                    participant.setTimeLow(highLowDate.getSecond());
                                }

                                if (participantRole != null && !CollectionUtils.isEmpty(participantRole.getAddrs())) {
                                    Person holderPerson = CcdParseUtils.createPerson(participantRole,
                                            client.getOrganization(), LEGACY_TABLE);
                                    participant.setPerson(holderPerson);
                                }

                                // Holder
                                policyActivity.setSubscriber(participant);
                            }
                        }

                        List<AuthorizationActivity> authorizationActivities = new ArrayList<>();
                        List<CoveragePlanDescription> coveragePlanDescriptions = new ArrayList<>();
                        for (Act act : ccdPolicyActivity.getActs()) {
                            if (CcdParseUtils.hasContent(act)) {
                                if (act.getMoodCode() == x_DocumentActMood.DEF) {
                                    // Description of Coverage Plan
                                    CoveragePlanDescription coveragePlanDescription = new CoveragePlanDescription();
                                    coveragePlanDescription.setOrganization(client.getOrganization());
                                    coveragePlanDescription
                                            .setLegacyId(CcdParseUtils.getFirstIdExtension(act.getIds()));
                                    coveragePlanDescription.setPolicyActivity(policyActivity);

                                    coveragePlanDescription.setText(CcdTransform.EDtoString(act.getText(),
                                            act.getCode() == null ? null : act.getCode().getDisplayName()));

                                    coveragePlanDescriptions.add(coveragePlanDescription);
                                } else if (act.getMoodCode() == x_DocumentActMood.EVN) {
                                    // Authorization Activity
                                    AuthorizationActivity authorizationActivity = new AuthorizationActivity();
                                    authorizationActivity.setOrganization(client.getOrganization());
                                    authorizationActivity.setPolicyActivity(policyActivity);

                                    List<CcdCode> clinicalStatements = new ArrayList<>();
                                    for (Procedure procedure : act.getProcedures()) {
                                        if (CcdParseUtils.hasContent(procedure)) {
                                            clinicalStatements.add(ccdCodeFactory.convert(procedure.getCode()));
                                        }
                                    }
                                    clinicalStatements.remove(null);
                                    authorizationActivity.setClinicalStatements(clinicalStatements);

                                    List<Person> persons = new ArrayList<>();
                                    for (Performer2 performer2 : act.getPerformers()) {
                                        Person performer = performer2Factory.parsePerson(performer2,
                                                client.getOrganization(), LEGACY_TABLE);
                                        if (performer != null) {
                                            persons.add(performer);
                                        }
                                    }
                                    authorizationActivity.setPerformers(persons);

                                    authorizationActivities.add(authorizationActivity);
                                }
                            }
                        }
                        policyActivity.setAuthorizationActivities(authorizationActivities);
                        policyActivity.setCoveragePlanDescriptions(coveragePlanDescriptions);

                        policyActivity.setPayer(payer);
                        policyActivities.add(policyActivity);
                    }
                }
                if (!CollectionUtils.isEmpty(policyActivities)) {
                    payer.setPolicyActivities(policyActivities);
                }
            }
            payers.add(payer);
        }

        return payers;
    }

    @Override
    public boolean isTemplateIncluded() {
        return isTemplateIncluded;
    }
}
