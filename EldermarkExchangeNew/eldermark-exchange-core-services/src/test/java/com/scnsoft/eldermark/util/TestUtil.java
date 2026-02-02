package com.scnsoft.eldermark.util;

import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.PersonTelecom;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.basic.Telecom;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.community.CommunityTelecom;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.EncounterPerformer;
import com.scnsoft.eldermark.entity.document.ccd.Participant;
import com.scnsoft.eldermark.entity.document.ccd.ProductInstance;
import com.scnsoft.eldermark.util.cda.CcdUtils;
import org.eclipse.mdht.uml.cda.*;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.TelecommunicationAddressUse;
import org.junit.Assert;
import org.mockito.ArgumentMatcher;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.function.BiFunction;

import static junit.framework.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;

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

    public static CcdCode createCcdCodeMockCodeSystemNot(String codeSystem) {
        return createCcdCodeMock(getRandomString(8), getRandomString(codeSystem.length() + 1),
                getRandomString(16), getRandomString(16));
    }

    public static Participant createParticipantMock() {
        Participant participant = new Participant();

        participant.setTimeLow(new Date());
        participant.setTimeHigh(new Date());
        participant.setId(random.nextLong());

        participant.setCommunity(createCommunityMock());
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

        List<PersonTelecom> personTelecomList = new ArrayList<PersonTelecom>();
        personTelecomList.add(tel);

        person.setTelecoms(personTelecomList);

        return person;

    }

    public static Community createCommunityMock() {
        CommunityTelecom tel = new CommunityTelecom();
        initTelecom(tel);

        CommunityAddress addr = new CommunityAddress();
        initAddr(addr);
        //addr.setPostalAddressUse(addr.getPostalCode());

        List<CommunityAddress> addrs = new ArrayList<>();
        addrs.add(addr);

        Community communityMock = new Community();
        communityMock.setId(random.nextLong());
        communityMock.setName(getRandomString(10));
        communityMock.setTelecom(tel);
        communityMock.setAddresses(addrs);

        return communityMock;
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
        for (int i = 0; i < random.nextInt(maxLenght) + 1; i++) {
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

    public static void assertTelecom(Telecom telecomMock, TEL tel) {
        Assert.assertEquals(telecomMock.getValue(), tel.getValue());

        if (telecomMock.getUseCode() != null) {
            Assert.assertTrue(tel.getUses().size() > 0);
            Assert.assertEquals(telecomMock.getUseCode(), tel.getUses().get(0).getName());
        }
    }

    public static <T extends Observation> T getObservationById(Long id, List<T> advanceDirectiveObservations) {
        for (var observation : advanceDirectiveObservations) {
            II adId = observation.getIds().get(0);
            if (adId.getExtension().equals(id.toString())) {
                return observation;
            }
        }
        return null;
    }

    public static <T extends Act> T getActById(Object id, List<T> acts) {
        for (var act : acts) {
            if (act.getIds().size() > 0) {
                II adId = act.getIds().get(0);
                if (adId.getExtension().equals(id.toString())) {
                    return act;
                }
            }
        }
        return null;
    }

    public static <T extends Organizer> T getOrganizerById(Long id, List<T> vitalSignsOrganizerList) {
        for (var vitalSignsOrganizer : vitalSignsOrganizerList) {
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

        Community organizationMock = authorMock.getCommunity();
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
        } else if (personMock != null) {

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
        //authorMock.setCommunity(TestUtil.createCommunityMock());
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

    public static Date atStartOfDay() {
        return Date.from(
                LocalDate.ofInstant(Instant.now(), ZoneId.systemDefault())
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
        );
    }

    public static <T> Collection<T> sameElementsCollection(Collection<T> c2) {
        return argThat(new ArgumentMatcher<>() {
            @Override
            public boolean matches(Collection<T> c1) {
                return isEqualCollection(c1, c2);
            }

            @Override
            public String toString() {
                return c2.toString();
            }
        });
    }

    private static <T> boolean isEqualCollection(Collection<T> c1, Collection<T> c2) {
        if (c1 == c2) {
            return true;
        }

        if (CollectionUtils.isEmpty(c1) && CollectionUtils.isEmpty(c2)) {
            return true;
        }

        if (CollectionUtils.isEmpty(c1) && !CollectionUtils.isEmpty(c2)) {
            return false;
        }

        if (!CollectionUtils.isEmpty(c1) && CollectionUtils.isEmpty(c2)) {
            return false;
        }

        return org.apache.commons.collections4.CollectionUtils.isEqualCollection(c1, c2);
    }

    public static <K, V> Map<K, V> sameElementsMap(Map<K, V> m2, BiFunction<V, V, Boolean> valueComparator) {
        return argThat(m1 -> {
            if (org.apache.commons.collections4.CollectionUtils.size(m1) != org.apache.commons.collections4.CollectionUtils.size(m2)) {
                return false;
            }

            if (!org.apache.commons.collections4.CollectionUtils.isEqualCollection(m1.keySet(), m2.keySet())) {
                return false;
            }

            return m1.entrySet().stream()
                    .allMatch(e -> {
                        var v1 = e.getValue();
                        var v2 = m2.get(e.getKey());
                        return valueComparator.apply(v1, v2);
                    });
        });
    }

}
