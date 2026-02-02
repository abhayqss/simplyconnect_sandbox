package com.scnsoft.eldermark.test.util;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.Author;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.services.cda.util.CcdUtils;
import org.junit.Assert;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.AD;
import org.eclipse.mdht.uml.hl7.datatypes.CD;
import org.eclipse.mdht.uml.hl7.datatypes.II;
import org.eclipse.mdht.uml.hl7.datatypes.PN;
import org.eclipse.mdht.uml.hl7.vocab.TelecommunicationAddressUse;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static junit.framework.Assert.assertEquals;

public class TestUtil {

    private static Random random = new Random();

    public static CcdCode createCcdCodeMock(String code, String codeSystem, String codeSystemName, String displayName) {
        CcdCode ccdCode = new CcdCode();
        ccdCode.setCode(code);
        ccdCode.setCodeSystem(codeSystem);
        ccdCode.setCodeSystemName(codeSystemName);
        ccdCode.setDisplayName(displayName);
        return ccdCode;
    }

    public static CcdCode createCcdCodeMock() {
        return createCcdCodeMock(getRandomString(8), getRandomString(16), getRandomString(16), getRandomString(16));
    }

    public static Participant createParticipantMock() {
        Participant participant = new Participant();

        participant.setTimeLow(new Date());
        participant.setTimeHigh(new Date());
        participant.setId(random.nextLong());

        participant.setOrganization(createOrganizationMock());
        participant.setPerson(createPersonMock());

        return participant;
    }

    public static EncounterPerformer createEncounterPerformerMock() {
        EncounterPerformer encounterPerformer = new EncounterPerformer();
        encounterPerformer.setProviderCode(createCcdCodeMock());
        encounterPerformer.setPerformer(createPersonMock());
        return encounterPerformer;
    }

    public static Person createPersonMock() {
        Person person = new Person();
        person.setId(random.nextLong());


        List<Name> names = new ArrayList<Name>();

        for (int i = 0; i < 3; i++) {

            Name name = new Name();
            name.setPrefix(getRandomString(2));
            name.setGiven(getRandomString(8));
            name.setFamily(getRandomString(8));
            names.add(name);
        }
        person.setNames(names);

        PersonAddress addr = new PersonAddress();

        initAddr(addr);

        List<PersonAddress> addrs = new ArrayList<PersonAddress>();
        addrs.add(addr);

        person.setAddresses(addrs);

        PersonTelecom tel = new PersonTelecom();
        initTelecom(tel);

        List<PersonTelecom>personTelecomList = new ArrayList<PersonTelecom>();
        personTelecomList.add(tel);

        person.setTelecoms(personTelecomList);

        return person;

    }

    public static Organization createOrganizationMock() {


        OrganizationTelecom tel = new OrganizationTelecom();
        initTelecom(tel);

        OrganizationAddress addr = new OrganizationAddress();
        initAddr(addr);
        //addr.setPostalAddressUse(addr.getPostalCode());

        List<OrganizationAddress> addrs = new ArrayList<OrganizationAddress>();
        addrs.add(addr);

        Organization organizationMock = new Organization();
        organizationMock.setId(random.nextLong());
        organizationMock.setName(getRandomString(10));
        organizationMock.setTelecom(tel);
        organizationMock.setAddresses(addrs);

        return organizationMock;
    }

    private static void initTelecom(Telecom tel) {
        tel.setId(random.nextLong());
        tel.setValue("" + random.nextInt(10000));
        tel.setUseCode(TelecommunicationAddressUse.get(random.nextInt(TelecommunicationAddressUse.values().length)).getLiteral());
    }

    public static void initAddr(Address addr) {
        addr.setPostalCode("" + random.nextInt(10000));
        addr.setStreetAddress(getRandomString(10));
        addr.setCity(getRandomString(6));
        addr.setState(getRandomString(2));
        addr.setCountry(getRandomString(5));
    }


    public static String getRandomString(int length) {
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static List<String> generateStringArray(int maxLenght) {
        List<String> referenceRanges = new ArrayList<String>();
        for (int i=0;i<random.nextInt(maxLenght)+1;i++) {
            referenceRanges.add(TestUtil.getRandomString(8));
        }
        return referenceRanges;
    }

    public static void assertNames(Name name, PN pn) {
        if (name.getPrefix() != null) {
            Assert.assertTrue(pn.getPrefixes().size() > 0);
            assertEquals(name.getPrefix(), pn.getPrefixes().get(0).getText());
        }
        if (name.getGiven() != null) {
            Assert.assertTrue(pn.getGivens().size() > 0);
            assertEquals(name.getGiven(), pn.getGivens().get(0).getText());
        }
        if (name.getFamily() != null) {
            Assert.assertTrue(pn.getFamilies().size() > 0);
            assertEquals(name.getFamily(), pn.getFamilies().get(0).getText());
        }
        if (name.getSuffix() != null) {
            Assert.assertTrue(pn.getSuffixes().size() > 0);
            assertEquals(name.getSuffix(), pn.getSuffixes().get(0).getText());
        }
    }

    public static void assertAddresses(Address addressMock, AD addr) {
        if (addressMock.getStreetAddress() != null) {
            Assert.assertTrue(addr.getStreetAddressLines().size() > 0);
            assertEquals(addressMock.getStreetAddress(), addr.getStreetAddressLines().get(0).getText());
        }
        if (addressMock.getCity() != null) {
            Assert.assertTrue(addr.getCities().size() > 0);
            assertEquals(addressMock.getCity(), addr.getCities().get(0).getText());
        }

        if (addressMock.getCountry() != null) {
            Assert.assertTrue(addr.getCountries().size() > 0);
            assertEquals(addressMock.getCountry(), addr.getCountries().get(0).getText());
        }
        if (addressMock.getPostalCode() != null) {
            Assert.assertTrue(addr.getPostalCodes().size() > 0);
            assertEquals(addressMock.getPostalCode(), addr.getPostalCodes().get(0).getText());
        }
        if (addressMock.getState() != null) {
            Assert.assertTrue(addr.getStates().size() > 0);
            assertEquals(addressMock.getState(), addr.getStates().get(0).getText());
        }
    }

    public static Observation getObservationById(Long id, List<? extends Observation> advanceDirectiveObservations) {
        for (Observation observation : advanceDirectiveObservations) {
            II adId = observation.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return observation;
            }
        }
        return null;
    }

    public static Act getActById(Object id, List<? extends Act> acts) {
        for (Act act : acts) {
            II adId = act.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return act;
            }
        }
        return null;
    }

    public static Organizer getOrganizerById(Long id, List<? extends Organizer> vitalSignsOrganizerList) {
        for (Organizer vitalSignsOrganizer : vitalSignsOrganizerList) {
            II adId = vitalSignsOrganizer.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return vitalSignsOrganizer;
            }
        }
        return null;
    }

    public static void assertAuthors(Author authorMock, org.eclipse.mdht.uml.cda.Author author) {
        //Add other fields to assert if necessary
        Assert.assertEquals(CcdUtils.formatSimpleDate(authorMock.getTime()), author.getTime().getValue());

        AssignedAuthor assignedAuthor = author.getAssignedAuthor();

        Organization organizationMock = authorMock.getOrganization();
        Person personMock = authorMock.getPerson();

        if (organizationMock != null) {

            //TODO assert ID
            if (!CollectionUtils.isEmpty(organizationMock.getAddresses())) {
                Address addressMock = organizationMock.getAddresses().get(0);
                AD addr = assignedAuthor.getAddrs().get(0);
                TestUtil.assertAddresses(addressMock, addr);
            }

            Telecom telecom = organizationMock.getTelecom();
            if (telecom != null) {
                assertEquals(telecom.getValue(), assignedAuthor.getTelecoms().get(0).getValue());
            }
        }
        else if (personMock!=null) {

            assertEquals(personMock.getId().toString(), assignedAuthor.getIds().get(0).getExtension());
            if (!CollectionUtils.isEmpty(personMock.getNames())) {
                PN pn = assignedAuthor.getAssignedPerson().getNames().get(0);

                Name name = personMock.getNames().get(0);
                TestUtil.assertNames(name, pn);
            }

            if (!CollectionUtils.isEmpty(personMock.getAddresses())) {
                Address addressMock = personMock.getAddresses().get(0);
                AD addr = assignedAuthor.getAddrs().get(0);
                TestUtil.assertAddresses(addressMock, addr);
            }
            List<PersonTelecom> telecoms = personMock.getTelecoms();
            if (!CollectionUtils.isEmpty(telecoms)) {
                assertEquals(telecoms.get(0).getValue(), assignedAuthor.getTelecoms().get(0).getValue());
            }

        }
    }

    public static Author createAuthorMock() {
        Author authorMock = new Author();
        authorMock.setId(random.nextLong());
        authorMock.setTime(new Date());
        //authorMock.setOrganization(TestUtil.createOrganizationMock());
        authorMock.setPerson(TestUtil.createPersonMock());

        return authorMock;
    }


    //TODO Assert NullFlavor's?
    public static void assertProductInstances(ProductInstance productInstanceMock, ParticipantRole participantRole) {
        assertEquals(productInstanceMock.getId().toString(), participantRole.getIds().get(0).getExtension());
        Assert.assertEquals("MANU", participantRole.getClassCode().getName());
        Assert.assertEquals("2.16.840.1.113883.10.20.22.4.37", participantRole.getTemplateIds().get(0).getRoot());
        assertEquals(productInstanceMock.getDeviceCode().getCode(), participantRole.getPlayingDevice().getCode().getCode());
        assertEquals(productInstanceMock.getDeviceCode().getCodeSystem(), participantRole.getPlayingDevice().getCode().getCodeSystem());
        assertEquals(productInstanceMock.getScopingEntityId(), participantRole.getScopingEntity().getIds().get(0).getExtension());
    }

    public static void assertCodes(CcdCode code, CD cd) {
        assertEquals(code.getCode(), cd.getCode());
        assertEquals(code.getCodeSystem(), cd.getCodeSystem());
        assertEquals(code.getDisplayName(), cd.getDisplayName());
        assertEquals(code.getCodeSystemName(), cd.getCodeSystemName());

    }
}
