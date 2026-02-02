package com.scnsoft.eldermark.test.services.consol.templates.sections;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import com.scnsoft.eldermark.services.consol.templates.sections.PayerFactory;
import com.scnsoft.eldermark.test.util.TestUtil;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.cda.Procedure;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.IVL_TS;
import org.eclipse.mdht.uml.hl7.vocab.NullFlavor;
import org.junit.Test;
import org.openhealthtools.mdht.uml.cda.consol.CoverageActivity;
import org.openhealthtools.mdht.uml.cda.consol.PayersSection;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class PayerSectionTest {

    private static final long RESIDENT_ID = 49L;

    private Random random = new Random();

    @Test
    public void testBuildingTemplate() {

        List<Payer> payerList = new ArrayList<>();

        for (int n = 0; n < 3; n++) {
            Payer payerMock = new Payer();
            payerList.add(payerMock);
            //payerMock.setId(random.nextLong());
            // if (n != 1)
            payerMock.setCoverageActivityId(TestUtil.getRandomString(10));

            if (n != 2) {
                List<PolicyActivity> policyActivities = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    PolicyActivity policyActivity = new PolicyActivity();
                    policyActivity.setId(random.nextLong());
                    if (i % 2 == 0)
                        policyActivity.setGuarantorOrganization(TestUtil.createOrganizationMock());
                    else if (i != 3)
                        policyActivity.setGuarantorPerson(TestUtil.createPersonMock());
                    policyActivity.setGuarantorTime(new Date());
                    policyActivity.setHealthInsuranceTypeCode(TestUtil.createCcdCodeMock());
                    if (i % 2 == 0)
                        policyActivity.setParticipantDateOfBirth(new Date());

                    if (i != 2)
                        policyActivity.setPayerOrganization(TestUtil.createOrganizationMock());

                    if (i != 3)
                        policyActivity.setPayerFinanciallyResponsiblePartyCode(TestUtil.createCcdCodeMock());

                    policyActivity.setSequenceNumber(BigInteger.valueOf(random.nextInt()));

                    if (i != 1)
                        policyActivity.setParticipant(TestUtil.createParticipantMock());

                    if (i != 3)
                        policyActivity.setSubscriber(TestUtil.createParticipantMock());

                    if (i % 2 == 0) {
                        List<AuthorizationActivity> authorizationActivities = new ArrayList<>();

                        for (int k = 0; k < 2; k++) {
                            AuthorizationActivity authorizationActivity = new AuthorizationActivity();
                            if (k != 1)
                                authorizationActivity.setClinicalStatements(new ArrayList<>(Arrays.asList(TestUtil.createCcdCodeMock(), TestUtil.createCcdCodeMock())));
                            authorizationActivity.setId(random.nextLong());

                            authorizationActivities.add(authorizationActivity);
                        }
                        policyActivity.setAuthorizationActivities(authorizationActivities);

                    } else if (i == 3) {

                        List<CoveragePlanDescription> coveragePlanDescriptions = new ArrayList<>();

                        for (int k = 0; k < 2; k++) {
                            CoveragePlanDescription coveragePlanDescription = new CoveragePlanDescription();
                            coveragePlanDescription.setId(random.nextLong());
                            if (k != 1)
                                coveragePlanDescription.setText(TestUtil.getRandomString(10));

                            coveragePlanDescriptions.add(coveragePlanDescription);
                        }

                        policyActivity.setCoveragePlanDescriptions(coveragePlanDescriptions);
                    }

                    policyActivities.add(policyActivity);
                }
                payerMock.setPolicyActivities(policyActivities);
            }

        }
        /*
        daoMock = EasyMock.createMock(PayerDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(new HashSet<>(payerList));
        EasyMock.replay(daoMock);*/

        PayerFactory payerFactory = new PayerFactory();
        // dependency on DAO is not required for parsing anymore
        //payerFactory.setPayerDao(daoMock);

        final PayersSection payerSection = payerFactory.buildTemplateInstance(payerList);

        assertNotNull(payerSection);
        //EasyMock.verify(daoMock);


        assertEquals("2.16.840.1.113883.10.20.22.2.18", payerSection.getTemplateIds().get(0).getRoot());

        assertEquals("48768-6", payerSection.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", payerSection.getCode().getCodeSystem());
        assertEquals("LOINC", payerSection.getCode().getCodeSystemName());
        assertEquals("Payers".toLowerCase(), payerSection.getCode().getDisplayName().toLowerCase());

        //assertEquals("Insurance Providers", payerSection.getTitle().getText());

        List<CoverageActivity> coverageActivityList = payerSection.getCoverageActivities();
        assertEquals(payerList.size(), coverageActivityList.size());

        for (Payer payerMock : payerList) {
            CoverageActivity coverageActivity = (CoverageActivity) TestUtil.getActById(payerMock.getCoverageActivityId(), coverageActivityList);
            assertNotNull(coverageActivity);

            assertEquals("ACT", coverageActivity.getClassCode().toString());
            assertEquals("EVN", coverageActivity.getMoodCode().toString());
            assertEquals("2.16.840.1.113883.10.20.22.4.60", coverageActivity.getTemplateIds().get(0).getRoot());
            assertEquals("completed", coverageActivity.getStatusCode().getCode());

            CD code = coverageActivity.getCode();
            assertEquals("48768-6", code.getCode());
            assertEquals("2.16.840.1.113883.6.1", code.getCodeSystem());
            assertEquals("LOINC", code.getCodeSystemName());
            assertEquals("Payment Sources", code.getDisplayName());

            List<org.openhealthtools.mdht.uml.cda.consol.PolicyActivity> policyActivityList = coverageActivity.getPolicyActivities();

            if (!CollectionUtils.isEmpty(payerMock.getPolicyActivities())) {

                assertEquals(payerMock.getPolicyActivities().size(), policyActivityList.size());

                for (PolicyActivity policyActivityMock : payerMock.getPolicyActivities()) {
                    org.openhealthtools.mdht.uml.cda.consol.PolicyActivity policyActivityCcd =
                            (org.openhealthtools.mdht.uml.cda.consol.PolicyActivity) TestUtil.getActById(policyActivityMock.getId(), policyActivityList);
                    assertNotNull(policyActivityCcd);

                    assertEquals("COMP", ((EntryRelationship) policyActivityCcd.eContainer()).getTypeCode().toString());
                    if (policyActivityMock.getSequenceNumber() != null) {
                        assertEquals(policyActivityMock.getSequenceNumber(), ((EntryRelationship) policyActivityCcd.eContainer()).getSequenceNumber().getValue());
                    }
                    assertEquals("ACT", policyActivityCcd.getClassCode().toString());
                    assertEquals("EVN", policyActivityCcd.getMoodCode().toString());
                    assertEquals("2.16.840.1.113883.10.20.22.4.61", policyActivityCcd.getTemplateIds().get(0).getRoot());

                    assertEquals("completed", policyActivityCcd.getStatusCode().getCode());

                    if (policyActivityMock.getHealthInsuranceTypeCode() != null) {
                        TestUtil.assertCodes(policyActivityMock.getHealthInsuranceTypeCode(),policyActivityCcd.getCode());
                    }

                    Organization payerOrganizationMock = policyActivityMock.getPayerOrganization();
                    Performer2 payerPerformer = getPerformer(policyActivityCcd.getPerformers(), "2.16.840.1.113883.10.20.22.4.87");
                    if (payerOrganizationMock != null) {
                        assertNotNull(payerPerformer);
                        assertEquals("PRF", payerPerformer.getTypeCode().toString());

                        AssignedEntity assignedEntity = payerPerformer.getAssignedEntity();
                       // assertEquals(payerOrganizationMock.getId().toString(), assignedEntity.getIds().get(0).getExtension());

                        if (policyActivityMock.getPayerFinanciallyResponsiblePartyCode() != null) {
                            TestUtil.assertCodes(policyActivityMock.getPayerFinanciallyResponsiblePartyCode(),assignedEntity.getCode());
                        }

                        assertPerformerOrganizations(payerOrganizationMock, assignedEntity);


                    } else {
                        assertEquals("PRF", payerPerformer.getTypeCode().toString());
                        assertEquals(NullFlavor.NI, payerPerformer.getNullFlavor());
                    }

                    Organization guarantorOrganization = policyActivityMock.getGuarantorOrganization();
                    Person guarantorPerson = policyActivityMock.getGuarantorPerson();

                    if (guarantorOrganization != null || guarantorPerson != null) {
                        Performer2 guarantorPerformer = getPerformer(policyActivityCcd.getPerformers(), "2.16.840.1.113883.10.20.22.4.88");
                        assertNotNull(guarantorPerformer);
                        assertEquals("PRF", guarantorPerformer.getTypeCode().toString());

                        if (policyActivityMock.getGuarantorTime() != null) {
                            assertEquals(CcdUtils.formatSimpleDate(policyActivityMock.getGuarantorTime()), guarantorPerformer.getTime().getValue());
                        }

                        AssignedEntity assignedEntity = guarantorPerformer.getAssignedEntity();

                        assertEquals("GUAR", assignedEntity.getCode().getCode());
                        assertEquals("2.16.840.1.113883.5.111", assignedEntity.getCode().getCodeSystem());

                        if (guarantorOrganization != null) {
                            assertPerformerOrganizations(guarantorOrganization, assignedEntity);
                        } else {
                            assertEquals(guarantorPerson.getId().toString(),assignedEntity.getIds().get(0).getExtension());
                            if (!CollectionUtils.isEmpty(guarantorPerson.getAddresses())) {
                                TestUtil.assertAddresses(guarantorPerson.getAddresses().get(0), assignedEntity.getAddrs().get(0));
                            }

                            if (!CollectionUtils.isEmpty(guarantorPerson.getTelecoms())) {
                                assertEquals(guarantorPerson.getTelecoms().get(0).getValue(), assignedEntity.getTelecoms().get(0).getValue());
                            }

                            if (!CollectionUtils.isEmpty(guarantorPerson.getNames())) {
                                TestUtil.assertNames(guarantorPerson.getNames().get(0), assignedEntity.getAssignedPerson().getNames().get(0));
                            }
                        }
                    }

                    Participant2 participiant = getParticipiant(policyActivityCcd.getParticipants(), "2.16.840.1.113883.10.20.22.4.89");

                    Participant participantMock = policyActivityMock.getParticipant();
                    if (policyActivityMock.getParticipant() != null) {
                        assertEquals("COV", participiant.getTypeCode().getName());
                        IVL_TS participiantTime = participiant.getTime();
                        if (participantMock.getTimeLow() != null) {
                            assertEquals(CcdUtils.formatSimpleDate(participantMock.getTimeLow()), participiantTime.getLow().getValue());
                        }
                        if (participantMock.getTimeHigh() != null) {
                            assertEquals(CcdUtils.formatSimpleDate(participantMock.getTimeHigh()), participiantTime.getHigh().getValue());
                        }

                        ParticipantRole participantRole = participiant.getParticipantRole();
                        assertEquals(policyActivityMock.getParticipantMemberId(), participantRole.getIds().get(0).getExtension()); //TODO if id is GUID use getRoot

                        if (participantMock.getRoleCode() != null) {
                            TestUtil.assertCodes(participantMock.getRoleCode(), participantRole.getCode());
//                            assertEquals("2.16.840.1.113883.5.111", participantRole.getCode().getCodeSystem());
                        }

                        Person participiantPerson = participantMock.getPerson();
                        if (participiantPerson != null) {
                            if (!CollectionUtils.isEmpty(participiantPerson.getAddresses())) {
                                TestUtil.assertAddresses(participiantPerson.getAddresses().get(0), participantRole.getAddrs().get(0));
                            }
                            if (!CollectionUtils.isEmpty(participiantPerson.getNames())) {
                                TestUtil.assertNames(participiantPerson.getNames().get(0), participantRole.getPlayingEntity().getNames().get(0));
                            }
                              //Don't remove it!!!!!
//                            if (policyActivityMock.getParticipantDateOfBirth() != null) {
//                                assertEquals(CcdUtils.formatSimpleDate(policyActivityMock.getParticipantDateOfBirth()), participantRole.getPlayingEntity().getSDTCBirthTime().getValue());
//                            }

                        }

                    } else {
                        assertEquals(NullFlavor.NI, participiant.getNullFlavor());
                        assertEquals("COV", participiant.getTypeCode().getName());
                    }

                    Participant subscriberMock = policyActivityMock.getSubscriber();
                    if (subscriberMock != null) {
                        Participant2 subscriber = getParticipiant(policyActivityCcd.getParticipants(), "2.16.840.1.113883.10.20.22.4.90");
                        assertEquals("HLD", subscriber.getTypeCode().getName());

                        if (subscriberMock.getTimeLow() != null) {
                            assertEquals(CcdUtils.formatSimpleDate(subscriberMock.getTimeLow()), subscriber.getTime().getValue());
                        }

                        ParticipantRole participantRole = subscriber.getParticipantRole();
                        assertEquals(subscriberMock.getId().toString(), participantRole.getIds().get(0).getExtension());

                        Person subscriberPerson = subscriberMock.getPerson();
                        if (!CollectionUtils.isEmpty(subscriberPerson.getAddresses())) {
                            TestUtil.assertAddresses(subscriberPerson.getAddresses().get(0), participantRole.getAddrs().get(0));
                        }
                    }

//                    if (!CollectionUtils.isEmpty(policyActivityMock.getAuthorizationActivities()) ||
//                            !CollectionUtils.isEmpty(policyActivityMock.getCoveragePlanDescriptions())) {
//                        List<Act> aas = getSpecificActs(policyActivityCcd.getActs(), x_DocumentActMood.EVN);
//                        assertTrue(aas.size()>0);
//
//                        for (Act aa: aas) {
//
//                        }
                    if (!CollectionUtils.isEmpty(policyActivityMock.getAuthorizationActivities())) {
                        for (AuthorizationActivity aaMock : policyActivityMock.getAuthorizationActivities()) {
                            Act act = TestUtil.getActById(aaMock.getId(), policyActivityCcd.getActs());
                            assertNotNull(act);
                            assertEquals("REFR", ((EntryRelationship) act.eContainer()).getTypeCode().toString());

                            assertEquals("ACT", act.getClassCode().getName());
                            assertEquals("EVN", act.getMoodCode().getName());
                            assertEquals("2.16.840.1.113883.10.20.1.19", act.getTemplateIds().get(0).getRoot());
                            assertEquals(aaMock.getId().toString(), act.getIds().get(0).getExtension());

                            if (!CollectionUtils.isEmpty(aaMock.getClinicalStatements())) {

//                                Collections.sort(aaMock.getClinicalStatements());
//
//                                Collections.sort(act.getProcedures(), new Comparator<Procedure>() {
//                                    @Override
//                                    public int compare(Procedure p1, Procedure p2) {
//                                        return p1.getCode().getCode().compareTo(p2.getCode().getCode());
//                                    }
//                                });
//

                                assertEquals(aaMock.getClinicalStatements().size(), act.getProcedures().size());
                                for (int i = 0; i < aaMock.getClinicalStatements().size(); i++) {
                                    Procedure procedure = act.getProcedures().get(i);
                                    assertEquals("SUBJ", ((EntryRelationship) procedure.eContainer()).getTypeCode().toString());
                                    assertEquals("PRMS", procedure.getMoodCode().toString());

                                    TestUtil.assertCodes(aaMock.getClinicalStatements().get(i), procedure.getCode());

                                }

                            }
                        }
                    }

                    if (!CollectionUtils.isEmpty(policyActivityMock.getCoveragePlanDescriptions())) {
                        for (CoveragePlanDescription coveragePlanDescription : policyActivityMock.getCoveragePlanDescriptions()) {

                            Act act = TestUtil.getActById(coveragePlanDescription.getId(), policyActivityCcd.getActs());
                            assertNotNull(act);
                            assertEquals("REFR", ((EntryRelationship) act.eContainer()).getTypeCode().toString());

                            assertEquals("ACT", act.getClassCode().getName());
                            assertEquals("DEF", act.getMoodCode().getName());

                            if (coveragePlanDescription.getText() != null)
                                assertEquals(coveragePlanDescription.getText(), act.getText().getText());
                        }
                    }
                }

            } else {
                Act policyActivityCcd = coverageActivity.getEntryRelationships().get(0).getAct();
                assertEquals("COMP", ((EntryRelationship) policyActivityCcd.eContainer()).getTypeCode().toString());
                assertEquals("ACT", policyActivityCcd.getClassCode().toString());
                assertEquals("EVN", policyActivityCcd.getMoodCode().toString());
            }
        }
    }
//
//    private List<Act> getSpecificActs(EList<Act> acts, x_DocumentActMood moodCode) {
//        List<Act> resultActs = new ArrayList<Act>();
//        for (Act act : acts) {
//            if (act.getMoodCode().equals(moodCode)) {
//                resultActs.add(act);
//            }
//        }
//        return resultActs;
//    }

    private void assertPerformerOrganizations(Organization organizationMock, AssignedEntity assignedEntity) {
        assertEquals(organizationMock.getId().toString(),assignedEntity.getIds().get(0).getExtension());
        if (!CollectionUtils.isEmpty(organizationMock.getAddresses())) {
            TestUtil.assertAddresses(organizationMock.getAddresses().get(0), assignedEntity.getAddrs().get(0));
        }

        if (organizationMock.getTelecom() != null) {
            assertEquals(organizationMock.getTelecom().getValue(), assignedEntity.getTelecoms().get(0).getValue());
        }

        if (organizationMock.getName() != null) {
            assertEquals(organizationMock.getName(), assignedEntity.getRepresentedOrganizations().get(0).getNames().get(0).getText());
        }

    }

    private Performer2 getPerformer(EList<Performer2> performers, String templateId) {
        for (Performer2 performer2 : performers) {
            if (performer2.getTemplateIds().get(0).getRoot().equals(templateId)) {
                return performer2;
            }
        }
        return null;
    }

    private Participant2 getParticipiant(EList<Participant2> participants, String templateId) {
        for (Participant2 participant2 : participants) {
            if (participant2.getTemplateIds().get(0).getRoot().equals(templateId)) {
                return participant2;
            }
        }
        return null;
    }
}
