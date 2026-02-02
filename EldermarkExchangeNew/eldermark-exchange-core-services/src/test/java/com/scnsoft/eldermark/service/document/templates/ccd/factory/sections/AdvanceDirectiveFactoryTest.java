package com.scnsoft.eldermark.service.document.templates.ccd.factory.sections;

import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.document.AdvanceDirectiveDocument;
import com.scnsoft.eldermark.entity.document.ccd.AdvanceDirective;
import com.scnsoft.eldermark.entity.document.ccd.Participant;
import com.scnsoft.eldermark.util.TestUtil;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.AD;
import org.eclipse.mdht.uml.hl7.datatypes.PN;
import org.junit.jupiter.api.Test;
import org.openhealthtools.mdht.uml.cda.ccd.AdvanceDirectiveObservation;
import org.openhealthtools.mdht.uml.cda.ccd.AdvanceDirectivesSection;

import java.util.*;

import static org.junit.Assert.*;

public class AdvanceDirectiveFactoryTest {

    private static final long RESIDENT_ID = 49L;

    @Test
    public void testBuildingTemplate() throws Exception {
        Random random = new Random();

        Set<AdvanceDirective> advanceDirectives = new HashSet<>();

        for (int n = 0; n < 4; n++) {

            AdvanceDirective adMock = new AdvanceDirective();
            adMock.setId(random.nextLong());
            adMock.setTextType(TestUtil.getRandomString(7));
            if (n == 1)
                adMock.setTimeHigh(new Date());
            else
                adMock.setTimeHigh(null);

            if (n == 2)
                adMock.setTimeLow(new Date());
            else
                adMock.setTimeLow(null);

            adMock.setType(TestUtil.createCcdCodeMock());
            adMock.setCustodian(TestUtil.createParticipantMock());

            var verifiers = new ArrayList<Participant>();

            for (int i = 0; i < 4; i++) {
                verifiers.add(TestUtil.createParticipantMock());
            }
            adMock.setVerifiers(verifiers);

            List<AdvanceDirectiveDocument> documents = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                AdvanceDirectiveDocument documentMock = new AdvanceDirectiveDocument();
                documentMock.setId(random.nextLong());
                documentMock.setMediaType(TestUtil.getRandomString(5));
                documentMock.setUrl(TestUtil.getRandomString(50));
                documents.add(documentMock);
            }

            adMock.setReferenceDocuments(documents);

            advanceDirectives.add(adMock);
        }

        /*
        daoMock = EasyMock.createMock(AdvanceDirectiveDao.class);
        EasyMock.expect(daoMock.listByResidentId(RESIDENT_ID, false));
        EasyMock.expectLastCall().andReturn(advanceDirectives);
        EasyMock.replay(daoMock);*/

        AdvanceDirectiveFactory advanceDirectiveFactory = new AdvanceDirectiveFactory();
        // dependency on DAO is not required for parsing anymore
        //advanceDirectiveFactory.setAdvanceDirectiveDao(daoMock);

        AdvanceDirectivesSection advanceDirectivesSection = advanceDirectiveFactory.buildTemplateInstance(advanceDirectives);

        assertNotNull(advanceDirectivesSection);
        //EasyMock.verify(daoMock);

        assertEquals("2.16.840.1.113883.10.20.1.1", advanceDirectivesSection.getTemplateIds().get(0).getRoot());
        assertEquals("42348-3", advanceDirectivesSection.getCode().getCode());
        assertEquals("2.16.840.1.113883.6.1", advanceDirectivesSection.getCode().getCodeSystem());
        assertEquals("Advance Directives", advanceDirectivesSection.getTitle().getText());

        List<AdvanceDirectiveObservation> advanceDirectiveObservations = advanceDirectivesSection.getAdvanceDirectiveObservations();
        assertEquals(advanceDirectives.size(), advanceDirectiveObservations.size());

        for (AdvanceDirective adMock : advanceDirectives) {
            AdvanceDirectiveObservation observation = TestUtil.getObservationById(adMock.getId(), advanceDirectiveObservations);

            assertNotNull(observation);

            assertEquals("OBS", observation.getClassCode().toString());
            assertEquals("EVN", observation.getMoodCode().toString());
            assertEquals("2.16.840.1.113883.10.20.22.4.48", observation.getTemplateIds().get(0).getRoot());
//            assertEquals(adMock.getId(), observation.getIds().get(0).getRoot());

            TestUtil.assertCodes(adMock.getType(), observation.getCode());

            assertEquals("completed", observation.getStatusCode().getCode());

            if (adMock.getTimeLow() != null) {
                assertEquals(CcdUtils.formatSimpleDate(adMock.getTimeLow()), observation.getEffectiveTime().getLow().getValue());
            } else {
                assertEquals("UNK", observation.getEffectiveTime().getLow().getNullFlavor().getLiteral());
            }

            if (adMock.getTimeHigh() != null) {
                assertEquals(CcdUtils.formatSimpleDate(adMock.getTimeHigh()), observation.getEffectiveTime().getHigh().getValue());
            } else {
                assertEquals("UNK", observation.getEffectiveTime().getHigh().getNullFlavor().getLiteral());
            }

            //observation.getValues().get(0);

            List<Participant2> participants = observation.getParticipants();
            assertEquals(adMock.getVerifiers().size() + 1, participants.size());

            int custodianCount = 0;
            for (int j = 0; j < participants.size(); j++) {
                Participant2 participant = participants.get(j);

                if (participant.getTypeCode().getLiteral().equals("VRF")) {
                    Participant verifierMock = adMock.getVerifiers().get(j);
                    assertEquals("2.16.840.1.113883.10.20.1.58", participant.getTemplateIds().get(0).getRoot());

                    assertEquals(CcdUtils.formatSimpleDate(verifierMock.getTimeLow()), participant.getTime().getValue());

                    Person person = verifierMock.getPerson();
                    Name name = person.getNames().get(0);

                    ParticipantRole participantRole = participant.getParticipantRole();
                    PlayingEntity playingEntity = participantRole.getPlayingEntity();
                    PN pn = playingEntity.getNames().get(0);
                    TestUtil.assertNames(name, pn);

                } else if (participant.getTypeCode().getLiteral().equals("CST")) {
                    custodianCount++;
                    Participant cstMock = adMock.getCustodian();
                    ParticipantRole participantRole = participant.getParticipantRole();
                    assertEquals("AGNT", participantRole.getClassCode().getLiteral());

                    Person personMock = cstMock.getPerson();
                    var addressMock = personMock.getAddresses().get(0);
                    AD addr = participantRole.getAddrs().get(0);

                    TestUtil.assertAddresses(addressMock, addr);

                    var telecom = personMock.getTelecoms().get(0);
                    assertEquals(telecom.getValue(), participantRole.getTelecoms().get(0).getValue());

                    PlayingEntity playingEntity = participantRole.getPlayingEntity();

                    PN pn = playingEntity.getNames().get(0);
                    Person person = cstMock.getPerson();
                    Name name = person.getNames().get(0);
                    TestUtil.assertNames(name, pn);
                } else {
                    fail("Wrong participant type code");
                }


            }
            assertEquals(1, custodianCount);

            List<AdvanceDirectiveDocument> documents = adMock.getReferenceDocuments();
            List<Reference> references = observation.getReferences();
            assertEquals(documents.size(), references.size());

            for (int j = 0; j < references.size(); j++) {
                Reference reference = references.get(0);
                AdvanceDirectiveDocument documentMock = documents.get(0);
                assertEquals("REFR", reference.getTypeCode().getLiteral());
                ExternalDocument externalDocument = reference.getExternalDocument();
                assertEquals(documentMock.getId().toString(), externalDocument.getIds().get(0).getExtension());

                assertEquals(documentMock.getMediaType(), externalDocument.getText().getMediaType());
                assertEquals(documentMock.getUrl(), externalDocument.getText().getReference().getValue());
            }

        }
    }

}
