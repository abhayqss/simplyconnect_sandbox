package com.scnsoft.eldermark.services.ccd.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.Performer2Factory;
import com.scnsoft.eldermark.services.cda.templates.sections.entries.PersonFactory;
import com.scnsoft.eldermark.services.cda.util.CcdTransform;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.CcdCodeFactory;
import com.scnsoft.eldermark.services.cda.util.CcdParseUtils;
import com.scnsoft.eldermark.services.cda.templates.ParsableSectionFactory;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.Procedure;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.ccd.CCDFactory;
import org.openhealthtools.mdht.uml.cda.ccd.CoverageActivity;
import org.openhealthtools.mdht.uml.cda.ccd.PayersSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.scnsoft.eldermark.entity.CodeSystem.*;

/**
 * <h1>Payers</h1>
 * “This section describes payers and the coverage they provide for defined activities. For each payer,
 * “all the pertinent data needed to contact, bill to, and collect from that payer should be included.
 * Authorization information that can be used to define pertinent referral, authorization tracking number,
 * procedure, therapy, intervention, device, or similar authorizations for the patient or provider or both
 * should be included.” [CCD 3.1]
 *
 * @see CoveragePlanDescription
 * @see Payer
 * @see Person
 * @see Participant
 * @see Organization
 * @see AuthorizationActivity
 * @see PolicyActivity
 * @see Resident
 * @see CcdCode
 */
@Component
public class PayerFactory extends RequiredTemplateFactory implements ParsableSectionFactory<PayersSection, Payer> {

    @Autowired
    private CcdCodeFactory ccdCodeFactory;

    @Autowired
    private PersonFactory personFactory;

    @Autowired
    private Performer2Factory performer2Factory;

    private static final Logger logger = LoggerFactory.getLogger(PayerFactory.class);
    private static final String LEGACY_TABLE = "NWHIN_PAYER";
    private static final String PAYER_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.87";
    private static final String GUARANTOR_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.88";

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
            //List<CoverageActivity> coverageActivityList = payersSection.getCoverageActivities();

            //  coverageActivityList.add(coverageActivity);
            payersSection.addAct(coverageActivity);

            coverageActivity.setClassCode(x_ActClassDocumentEntryAct.ACT);
            coverageActivity.setMoodCode(x_DocumentActMood.EVN);

            coverageActivity.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.60"));

            coverageActivity.getIds().add(CcdUtils.getId(payer.getCoverageActivityId()));

            coverageActivity.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

            CE code = CcdUtils.createCE("48768-6", "Payment Sources", CodeSystem.LOINC);
            coverageActivity.setCode(code);

            List<PolicyActivity> policyActivityList = payer.getPolicyActivities();

            if (!CollectionUtils.isEmpty(policyActivityList)) {

                for (PolicyActivity policyActivity : policyActivityList) {
                    org.openhealthtools.mdht.uml.cda.ccd.PolicyActivity policyActivityCcd = CCDFactory.eINSTANCE.createPolicyActivity();
                    //coverageActivity.getPolicyActivities().add(policyActivityCcd);
                    coverageActivity.addAct(policyActivityCcd);
                    ((EntryRelationship) policyActivityCcd.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.COMP);
                    if (policyActivity.getSequenceNumber() != null) {
                        INT sqNum = DatatypesFactory.eINSTANCE.createINT();
                        sqNum.setValue(policyActivity.getSequenceNumber());
                        ((EntryRelationship) policyActivityCcd.eContainer()).setSequenceNumber(sqNum);
                    }
                    policyActivityCcd.setClassCode(x_ActClassDocumentEntryAct.ACT);
                    policyActivityCcd.setMoodCode(x_DocumentActMood.EVN);
                    policyActivityCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.61"));
                    policyActivityCcd.getIds().add(CcdUtils.getId(policyActivity.getId()));
                    policyActivityCcd.setStatusCode(DatatypesFactory.eINSTANCE.createCS("completed"));

                    policyActivityCcd.setCode(CcdUtils.createCE(policyActivity.getHealthInsuranceTypeCode(), X12N_1336.getOid()));

                    Performer2 payerPerformer = CDAFactory.eINSTANCE.createPerformer2();
                    payerPerformer.setTypeCode(ParticipationPhysicalPerformer.PRF);
                    payerPerformer.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(PAYER_TEMPLATE_ID));
                    policyActivityCcd.getPerformers().add(payerPerformer);
                    Organization payerOrganization = policyActivity.getPayerOrganization();
                    if (payerOrganization != null) {
                        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                        payerPerformer.setAssignedEntity(assignedEntity);

                        if (policyActivity.getPayerFinanciallyResponsiblePartyCode() != null) {
                            assignedEntity.setCode(CcdUtils.createCE(policyActivity.getPayerFinanciallyResponsiblePartyCode(), ROLE_CLASS.getOid()));
                        }
                        createOrganization(assignedEntity, payerOrganization);
                    } else {
                        payerPerformer.setNullFlavor(NullFlavor.NI);
                        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                        payerPerformer.setAssignedEntity(assignedEntity);
                        assignedEntity.setNullFlavor(NullFlavor.NI);
                        assignedEntity.getIds().add(CcdUtils.getNullId());
                    }

                    if (policyActivity.getGuarantorOrganization() != null || policyActivity.getGuarantorPerson() != null) {
                        Performer2 guarantorPerformer = CDAFactory.eINSTANCE.createPerformer2();
                        policyActivityCcd.getPerformers().add(guarantorPerformer);
                        guarantorPerformer.setTypeCode(ParticipationPhysicalPerformer.PRF);
                        guarantorPerformer.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(GUARANTOR_TEMPLATE_ID));

                        if (policyActivity.getGuarantorTime() != null) {
                            guarantorPerformer.setTime(CcdUtils.convertEffectiveTime(policyActivity.getGuarantorTime()));
                        }
                        AssignedEntity assignedEntity = CDAFactory.eINSTANCE.createAssignedEntity();
                        guarantorPerformer.setAssignedEntity(assignedEntity);

                        assignedEntity.setCode(DatatypesFactory.eINSTANCE.createCE("GUAR", ROLE_CODE.getOid()));

                        Person guarantorPerson = policyActivity.getGuarantorPerson();
                        if (guarantorPerson != null) {
                            assignedEntity.getIds().add(CcdUtils.getId(guarantorPerson.getId()));        //TODO that will be not unique if orgId == personId
                            if (!CollectionUtils.isEmpty(guarantorPerson.getAddresses())) {
                                CcdUtils.addConvertedAddress(assignedEntity.getAddrs(), guarantorPerson.getAddresses().get(0));
                            }
                            if (!CollectionUtils.isEmpty(guarantorPerson.getTelecoms())) {
                                CcdUtils.addConvertedTelecom(assignedEntity.getTelecoms(), guarantorPerson.getTelecoms().get(0));
                            }
                            assignedEntity.setAssignedPerson(SectionEntryFactory.buildPerson(guarantorPerson));
                        } else {
                            createOrganization(assignedEntity, policyActivity.getGuarantorOrganization());
                        }
                    }

                    Participant participantModel = policyActivity.getParticipant();
                    Participant2 participantCcd = CDAFactory.eINSTANCE.createParticipant2();
                    participantCcd.setTypeCode(ParticipationType.COV);
                    participantCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.89"));
                    if (participantModel != null) {

                        if (participantModel.getTimeLow() != null || participantModel.getTimeHigh() != null) {
                            participantCcd.setTime(CcdUtils.convertEffectiveTime(participantModel.getTimeLow(), participantModel.getTimeHigh()));
                        }

                        ParticipantRole participantRole = CDAFactory.eINSTANCE.createParticipantRole();
                        participantCcd.setParticipantRole(participantRole);

                        participantRole.getIds().add(CcdUtils.getId(policyActivity.getParticipantMemberId()));

                        if (participantModel.getRoleCode() != null) {
                            participantRole.setCode(CcdUtils.createCE(participantModel.getRoleCode(), ROLE_CODE.getOid()));
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
                                playingEntity.setSDTCBirthTime(CcdUtils.convertEffectiveTime(policyActivity.getParticipantDateOfBirth()));
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
                        subscriberCcd.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.22.4.90"));
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
                        for (AuthorizationActivity authorizationActivity : policyActivity.getAuthorizationActivities()) {

                            Act act = CDAFactory.eINSTANCE.createAct();
                            act.setClassCode(x_ActClassDocumentEntryAct.ACT);
                            act.setMoodCode(x_DocumentActMood.EVN);
                            policyActivityCcd.addAct(act);

                            ((EntryRelationship) act.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.REFR);
                            act.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII("2.16.840.1.113883.10.20.1.19"));
                            act.getIds().add(CcdUtils.getId(authorizationActivity.getId()));

                            CD nullCD = DatatypesFactory.eINSTANCE.createCD();
                            nullCD.setNullFlavor(NullFlavor.NA);
                            act.setCode(nullCD);

                            if (!CollectionUtils.isEmpty(authorizationActivity.getClinicalStatements())) {
                                for (CcdCode statement : authorizationActivity.getClinicalStatements()) {
                                    Procedure procedure = CDAFactory.eINSTANCE.createProcedure();
                                    act.addProcedure(procedure);
                                    ((EntryRelationship) procedure.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
                                    procedure.setMoodCode(x_DocumentProcedureMood.PRMS);
                                    procedure.setClassCode(ActClass.PROC);
                                    procedure.setCode(CcdUtils.createCE(statement, SNOMED_CT.getOid()));
                                }
                            } else {
                                Procedure procedure = CDAFactory.eINSTANCE.createProcedure();
                                act.addProcedure(procedure);
                                ((EntryRelationship) procedure.eContainer()).setTypeCode(x_ActRelationshipEntryRelationship.SUBJ);
                                procedure.setNullFlavor(NullFlavor.NI);
                            }

                            if (!CollectionUtils.isEmpty(authorizationActivity.getPerformers())) {
                                for (Person performer : authorizationActivity.getPerformers()) {
                                    act.getPerformers().add(SectionEntryFactory.buildPerformer2(performer));
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

                            // HMO is a policy for a health plan that provides coverage for health care only through contracted or employed physicians and hospitals located in particular geographic or service areas. HMOs emphasize prevention and early detection of illness. Eligibility to enroll in an HMO is determined by where a covered party lives or works.
                            final CD actCode = CcdUtils.createCD("HMO", "health maintenance organization policy", HL7_ACT_CODE);
                            act.setCode(actCode);

                            //ED text = DatatypesFactory.eINSTANCE.createED();
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

    private static String buildSectionText(Collection<Payer> payers) {
        StringBuilder sectionText = new StringBuilder();

        if (CollectionUtils.isEmpty(payers)) {
            return "No known payers.";
        }

        sectionText.append("<table>");
        sectionText.append("<thead>");
        sectionText.append("<tr>");
        sectionText.append("<th>Company Name</th>");
        sectionText.append("<th>Policy Number</th>");
        sectionText.append("<th>Coverage Dates</th>");
        sectionText.append("</tr>");
        sectionText.append("</thead>");
        sectionText.append("<tbody>");

        for (Payer payer : payers) {
            if (payer.getPolicyActivities() != null) {
                for (PolicyActivity policyActivity : payer.getPolicyActivities()) {
                    sectionText.append("<tr>");
                    if (policyActivity.getPayerOrganization() != null) {
                        CcdUtils.addCellToSectionText(policyActivity.getPayerOrganization().getName(), sectionText);
                    } else {
                        CcdUtils.addEmptyCell(sectionText);
                    }
                    CcdUtils.addCellToSectionText(policyActivity.getParticipantMemberId(), sectionText);

                    sectionText.append("<td>");
                    Participant participant = policyActivity.getParticipant();
                    if (participant != null && (participant.getTimeHigh() != null || participant.getTimeLow() != null)) {
                        CcdUtils.addDateRangeToSectionText(participant.getTimeLow(), participant.getTimeHigh(), sectionText);
                    } else {
                        CcdUtils.addEmptyCellToSectionText(sectionText);
                    }
                    sectionText.append("</td>");
                    sectionText.append("</tr>");
                }
            }
        }
        sectionText.append("</tbody>");
        sectionText.append("</table>");

        return sectionText.toString();
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


    private static void createOrganization(AssignedEntity assignedEntity, Organization organization) {
        assignedEntity.getIds().add(CcdUtils.getId(organization.getId()));
        if (!CollectionUtils.isEmpty(organization.getAddresses())) {
            CcdUtils.addConvertedAddress(assignedEntity.getAddrs(), organization.getAddresses().get(0));
        }
        CcdUtils.addConvertedTelecom(assignedEntity.getTelecoms(), organization.getTelecom());
        assignedEntity.getRepresentedOrganizations().add(SectionEntryFactory.buildOrganization(organization, false));
    }

    @Override
    public List<Payer> parseSection(Resident resident, PayersSection payersSection) {
        if (!CcdParseUtils.hasContent(payersSection) || CollectionUtils.isEmpty(payersSection.getCoverageActivities())) {
            return Collections.emptyList();
        }
        checkNotNull(resident);

        final List<Payer> payers = new ArrayList<>();
        for (CoverageActivity ccdPayerCoverageActivity : payersSection.getCoverageActivities()) {
            Payer payer = new Payer();
            payer.setDatabase(resident.getDatabase());
            payer.setResident(resident);
            payer.setLegacyId(CcdParseUtils.getFirstIdExtension(ccdPayerCoverageActivity.getIds()));

            II coverageActivityId = CcdParseUtils.getFirstNotEmptyValue(ccdPayerCoverageActivity.getIds(), II.class);
            Pair<String, String> rootAndExtension = CcdParseUtils.getRootAndExt(coverageActivityId);
            if (rootAndExtension != null)
                payer.setCoverageActivityId(rootAndExtension.getSecond());

            if (!CollectionUtils.isEmpty(ccdPayerCoverageActivity.getPolicyActivities())) {
                List<PolicyActivity> policyActivities = new ArrayList<>();
                for (org.openhealthtools.mdht.uml.cda.ccd.PolicyActivity ccdPolicyActivity : ccdPayerCoverageActivity.getPolicyActivities()) {
                    if (CcdParseUtils.hasContent(ccdPolicyActivity)) {
                        PolicyActivity policyActivity = new PolicyActivity();
                        policyActivity.setDatabase(resident.getDatabase());

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
                        List<Performer2> payerPerformers = CcdParseUtils.findByTemplateId(
                                ccdPolicyActivity.getPerformers(), PAYER_TEMPLATE_ID);
                        if (CollectionUtils.isEmpty(payerPerformers)) {
                            payerPerformers = CcdParseUtils.findByCode(ccdPolicyActivity.getPerformers(), "PAYOR");
                        }
                        if (!CollectionUtils.isEmpty(payerPerformers)) {
                            AssignedEntity assignedEntity = payerPerformers.get(0).getAssignedEntity();
                            if (CcdParseUtils.hasContent(assignedEntity)) {
                                policyActivity.setPayerFinanciallyResponsiblePartyCode(ccdCodeFactory.convert(
                                        assignedEntity.getCode()));

                                if (CollectionUtils.isEmpty(assignedEntity.getRepresentedOrganizations())) {
                                    Person payerPerson = personFactory.parse(assignedEntity, resident.getDatabase(), LEGACY_TABLE);
                                    // TODO where to save PayerPerson?
                                } else {
                                    // TODO added default organization
                                    policyActivity.setPayerOrganization(resident.getFacility());
                                }
                            }
                        }

                        // In order to distinguish between Payer and Guarantor we use templateId here
                        List<Performer2> guarantorPerformers = CcdParseUtils.findByTemplateId(
                                ccdPolicyActivity.getPerformers(), GUARANTOR_TEMPLATE_ID);
                        if (CollectionUtils.isEmpty(guarantorPerformers)) {
                            guarantorPerformers = CcdParseUtils.findByCode(ccdPolicyActivity.getPerformers(), "GUAR");
                        }
                        if (!CollectionUtils.isEmpty(guarantorPerformers)) {
                            Performer2 guarantorPerformer = guarantorPerformers.get(0);

                            policyActivity.setGuarantorTime(CcdParseUtils.convertTsToDate(guarantorPerformer.getTime()));

                            AssignedEntity assignedEntity = guarantorPerformer.getAssignedEntity();
                            if (assignedEntity != null) {
                                if (CollectionUtils.isEmpty(assignedEntity.getRepresentedOrganizations())) {
                                    Person guarantorPerson = personFactory.parse(assignedEntity, resident.getDatabase(),
                                            LEGACY_TABLE);
                                    policyActivity.setGuarantorPerson(guarantorPerson);
                                } else {
                                    // TODO added default organization
                                    policyActivity.setGuarantorOrganization(resident.getFacility());
                                }
                            }
                        }

                        for (Participant2 participant2 : ccdPolicyActivity.getParticipants()) {
                            ParticipantRole participantRole = participant2.getParticipantRole();

                            Participant participant = new Participant();
                            participant.setDatabase(resident.getDatabase());
                            participant.setLegacyTable(LEGACY_TABLE);
                            if (participantRole != null) {
                                participant.setLegacyId(CcdParseUtils.getFirstIdExtension(participantRole.getIds()));
                            }

                            if (participant2.getTypeCode() == ParticipationType.COV) {
                                //ParticipantRole coveredParty = ccdPolicyActivity.getCoveredParty();

                                Pair<Date, Date> time = CcdTransform.IVLTStoHighLowDate(participant2.getTime());
                                if (time != null) {
                                    participant.setTimeHigh(time.getFirst());
                                    participant.setTimeLow(time.getSecond());
                                }

                                if (participantRole != null) {
                                    participant.setRoleCode(ccdCodeFactory.convert(participantRole.getCode()));
                                    II participantRoleId = CcdParseUtils.getFirstNotEmptyValue(participantRole.getIds(), II.class);
                                    if (participantRoleId != null) {
                                        policyActivity.setParticipantMemberId(participantRoleId.getExtension());
                                    }

                                    PlayingEntity playingEntity = participantRole.getPlayingEntity();
                                    if (playingEntity != null) {
                                        Person coverageTargetPerson = CcdParseUtils.createPerson(participantRole,
                                                resident.getDatabase(), LEGACY_TABLE);
                                        participant.setPerson(coverageTargetPerson);
                                        policyActivity.setParticipantDateOfBirth(CcdParseUtils.convertTsToDate(playingEntity.getSDTCBirthTime()));
                                    }
                                }

                                // Coverage target
                                policyActivity.setParticipant(participant);
                            } else if (participant2.getTypeCode() == ParticipationType.HLD) {
                                //ParticipantRole subscriber = ccdPolicyActivity.getSubscriber();
                                // TODO retrieve holder (subscriber participant) by ID?

                                Pair<Date, Date> highLowDate = CcdTransform.IVLTStoHighLowDate(participant2.getTime());
                                if (highLowDate != null) {
                                    participant.setTimeLow(highLowDate.getSecond());
                                }

                                if (participantRole != null && !CollectionUtils.isEmpty(participantRole.getAddrs())) {
                                    Person holderPerson = CcdParseUtils.createPerson(participantRole,
                                            resident.getDatabase(), LEGACY_TABLE);
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
                                    coveragePlanDescription.setDatabase(resident.getDatabase());
                                    coveragePlanDescription.setLegacyId(CcdParseUtils.getFirstIdExtension(act.getIds()));
                                    coveragePlanDescription.setPolicyActivity(policyActivity);

                                    coveragePlanDescription.setText(CcdTransform.EDtoString(act.getText(),
                                            act.getCode() == null ? null : act.getCode().getDisplayName()));

                                    coveragePlanDescriptions.add(coveragePlanDescription);
                                } else if (act.getMoodCode() == x_DocumentActMood.EVN) {
                                    // Authorization Activity
                                    AuthorizationActivity authorizationActivity = new AuthorizationActivity();
                                    authorizationActivity.setDatabase(resident.getDatabase());
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
                                        Person performer = performer2Factory.parsePerson(performer2, resident.getDatabase(), LEGACY_TABLE);
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

}
