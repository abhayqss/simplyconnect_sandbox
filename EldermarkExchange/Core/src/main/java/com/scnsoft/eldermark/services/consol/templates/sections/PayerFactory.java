package com.scnsoft.eldermark.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.cda.templates.RequiredTemplateFactory;
import com.scnsoft.eldermark.services.cda.templates.SectionFactory;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.Procedure;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.CE;
import org.eclipse.mdht.uml.hl7.datatypes.DatatypesFactory;
import org.eclipse.mdht.uml.hl7.datatypes.INT;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.openhealthtools.mdht.uml.cda.consol.ConsolFactory;
import org.openhealthtools.mdht.uml.cda.consol.CoverageActivity;
import org.openhealthtools.mdht.uml.cda.consol.PayersSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

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
@Component("consol.PayerFactory")
public class PayerFactory extends RequiredTemplateFactory implements SectionFactory<PayersSection, Payer> {

    private static final Logger logger = LoggerFactory.getLogger(PayerFactory.class);
    private static final String LEGACY_TABLE = "NWHIN_PAYER";
    private static final String TEMPLATE_ID_STR = "2.16.840.1.113883.10.20.22.2.18";
    private static final String PAYER_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.87";
    private static final String GUARANTOR_TEMPLATE_ID = "2.16.840.1.113883.10.20.22.4.88";

    @Override
    public PayersSection buildTemplateInstance(Collection<Payer> payers) {
        final PayersSection payersSection = ConsolFactory.eINSTANCE.createPayersSection();
        payersSection.getTemplateIds().add(DatatypesFactory.eINSTANCE.createII(TEMPLATE_ID_STR));

        final CE sectionCode = CcdUtils.createCE("48768-6", "Payers", CodeSystem.LOINC);
        payersSection.setCode(sectionCode);

        payersSection.setTitle(DatatypesFactory.eINSTANCE.createST("Payers"));

        payersSection.createStrucDocText(buildSectionText(payers));

        if (CollectionUtils.isEmpty(payers)) {
            // TODO buildNullPayer() ?
            return payersSection;
        }

        for (Payer payer : payers) {
            CoverageActivity coverageActivity = ConsolFactory.eINSTANCE.createCoverageActivity();
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
                    org.openhealthtools.mdht.uml.cda.consol.PolicyActivity policyActivityCcd = ConsolFactory.eINSTANCE.createPolicyActivity();
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

}
