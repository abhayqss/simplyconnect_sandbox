package com.scnsoft.eldermark.util.cda;

import com.scnsoft.eldermark.cda.service.schema.CdaDocumentType;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.community.CommunityAddress;
import com.scnsoft.eldermark.entity.community.CommunityTelecom;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.mdht.uml.cda.ClinicalDocument;
import org.eclipse.mdht.uml.cda.ParticipantRole;
import org.eclipse.mdht.uml.cda.Performer2;
import org.eclipse.mdht.uml.cda.Section;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.rim.InfrastructureRoot;
import org.eclipse.mdht.uml.hl7.vocab.EntityNamePartQualifier;
import org.eclipse.mdht.uml.hl7.vocab.EntityNameUse;
import org.eclipse.mdht.uml.hl7.vocab.PostalAddressUse;
import org.eclipse.mdht.uml.hl7.vocab.TelecommunicationAddressUse;
import org.openhealthtools.mdht.uml.cda.ccd.PlanOfCareSection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

//todo rename to CdaParseUtils
public class CcdParseUtils {
    private static final Logger logger = LoggerFactory.getLogger(CcdParseUtils.class);

    public static final String US_REALM_HEADER = "2.16.840.1.113883.10.20.22.1.1";

    public static Set<CdaDocumentType> resolveCdaTypes(ClinicalDocument doc) {
        var id = doc.getId();
        final String docId;
        if (id != null) {
            docId = "root=\"" + id.getRoot() + "\" extension=\"" + id.getExtension() + "\"";
        } else {
            docId = "<null>";
        }
        logger.info("Resolving document type of {}.", docId);

        var types = doc.getTemplateIds().stream()
                .filter(templateId -> !US_REALM_HEADER.equals(templateId.getRoot()))
                .map(templateId -> CdaDocumentType.from(templateId.getRoot(), templateId.getExtension()))
                .collect(Collectors.toSet());

        final String typesAsString = types.stream().map(Object::toString).collect(Collectors.joining(" + "));
        logger.info("Identified document {} as {}.", docId, typesAsString);

        return types;
    }

    public static boolean hasContent(ANY element) {
        if (element == null)
            return false;
        if ((element.isSetNullFlavor()) || (!element.hasContent()))
            return false;
        return true;
    }

    public static boolean hasContent(InfrastructureRoot element) {
        if (element == null)
            return false;
        if ((element.isNullFlavorDefined()) || (!element.hasContent()))
            return false;
        if (element instanceof PlanOfCareSection) {
            Section section = (Section) element;
            return !(CollectionUtils.isEmpty(section.getActs()) && CollectionUtils.isEmpty(section.getEncounters())
                    && CollectionUtils.isEmpty(section.getObservations())
                    && CollectionUtils.isEmpty(section.getProcedures())
                    && CollectionUtils.isEmpty(section.getSupplies())
                    && CollectionUtils.isEmpty(section.getSubstanceAdministrations()));
        }
        return true;
    }

    /**
     * @throws ClassCastException Handle this exception if you think that the
     *                            provided collection may contain values of an
     *                            unexpected type.
     */
    public static <E extends ANY, T extends E> T getFirstNotEmptyValue(List<E> values,
                                                                       @SuppressWarnings("unused") Class<T> returnClass) {
        if (!CollectionUtils.isEmpty(values)) {
            for (E value : values) {
                if (hasContent(value))
                    return (T) value;
            }
        }
        return null;
    }

    public static Pair<String, String> getRootAndExt(II id) {
        if (id == null || !hasContent(id)) {
            return null;
        } else {
            return new Pair<>(id.getRoot(), id.getExtension());
        }
    }

    public static Pair<String, String> getFirstRootAndExt(EList<II> ids) {
        return getRootAndExt(getFirstNotEmptyValue(ids, II.class));
    }

    public static long getFirstIdExtension(EList<II> ids) {
        long result = 0L;
        II firstId = getFirstNotEmptyValue(ids, II.class);
        if (firstId != null && firstId.getExtension() != null) {
            try {
                result = Long.parseLong(firstId.getExtension());
            } catch (NumberFormatException exc) {
                logger.warn(ExceptionUtils.getStackTrace(exc));
            }
        }
        return result;
    }

    public static String getFirstIdExtensionStr(EList<II> ids) {
        String result = CcdConstants.NWHIN_LEGACY_ID_STR;
        II firstId = getFirstNotEmptyValue(ids, II.class);
        if (firstId != null && firstId.getExtension() != null) {
            result = firstId.getExtension();
        }
        return result;
    }

    private static Pair<String, String> getPairFromEnxp(ENXP enxp) {
        Pair<String, String> result = new Pair<>();

        result.setFirst(StringUtils.trim(enxp.getText()));
        if (CollectionUtils.isNotEmpty(enxp.getQualifiers())) {
            EntityNamePartQualifier enpq = enxp.getQualifiers().get(0);
            result.setSecond(StringUtils.trim(enpq.getName()));
        }
        return result;
    }

    public static Pair<String, String> getValueAndQualifierFromFirstEnxp(List<ENXP> enxpList) {
        if (CollectionUtils.isNotEmpty(enxpList)) {
            return getPairFromEnxp(enxpList.get(0));
        }
        return null;
    }

    public static Pair<String, String> getValueAndQualifierFromSecondEnxp(List<ENXP> enxpList) {
        if (CollectionUtils.isNotEmpty(enxpList) && enxpList.size() > 1) {
            return getPairFromEnxp(enxpList.get(1));
        }
        return null;
    }

    public static String getTextFromFirstCcdAddressRecord(List<ADXP> ccdAddressRecords) {
        String result = null;
        if (CollectionUtils.isNotEmpty(ccdAddressRecords)) {
            ADXP ccdAddressRecord = ccdAddressRecords.get(0);
            result = StringUtils.trim(ccdAddressRecord.getText());
        }
        return result;
    }

    public static PersonTelecom createTelecom(TEL ccdTelecom, Organization organization, Person person,
                                              String legacyTable) {
        PersonTelecom personTelecom = new PersonTelecom();
        personTelecom.setLegacyId(CcdConstants.NWHIN_LEGACY_ID_STR);
        personTelecom.setOrganization(organization);
        personTelecom.setLegacyTable(legacyTable);

        personTelecom.setPerson(person);
        personTelecom.setValue(ccdTelecom.getValue());
        if (CollectionUtils.isNotEmpty(ccdTelecom.getUses())) {
            TelecommunicationAddressUse telecommunicationAddressUse = ccdTelecom.getUses().get(0);
            personTelecom.setUseCode(telecommunicationAddressUse.getLiteral());
            // TODO Find another way to setSyncQualifier(?); or drop unique constraint
            personTelecom.setSyncQualifier(guessSyncQualifier(personTelecom.getUseCode(), personTelecom.getValue()));
        }

        return personTelecom;
    }

    /**
     * @return Sync Qualifier. By default, 0, which corresponds to
     * {@code PersonTelecomCode.EMAIL}
     */
    private static int guessSyncQualifier(String useCode, String telValue) {
        int result;
        if ("WP".equals(useCode)) {
            result = PersonTelecomCode.WP.getCode();
        } else if ("HP".equals(useCode)) {
            result = PersonTelecomCode.HP.getCode();
        } else if ("MC".equals(useCode)) {
            result = PersonTelecomCode.MC.getCode();
        } else {
            result = PersonTelecomCode.EMAIL.getCode();
        }
        return result;
    }

    public static CommunityTelecom createTelecom(TEL ccdTelecom, Organization organization, Community community,
                                                 String legacyTable) {
        CommunityTelecom orgTelecom = new CommunityTelecom();
        orgTelecom.setLegacyId(CcdConstants.NWHIN_LEGACY_ID_STR);
        orgTelecom.setOrganization(organization);
        orgTelecom.setLegacyTable(legacyTable);

        orgTelecom.setValue(ccdTelecom.getValue());
        orgTelecom.setOrganization(organization);
        if (CollectionUtils.isNotEmpty(ccdTelecom.getUses())) {
            TelecommunicationAddressUse telecommunicationAddressUse = ccdTelecom.getUses().get(0);
            orgTelecom.setUseCode(telecommunicationAddressUse.getLiteral());
        }

        return orgTelecom;
    }

    public static PersonAddress createAddress(AD ccdAddress, Organization organization, Person person,
                                              String legacyTable) {
        PersonAddress personAddress = new PersonAddress();
        personAddress.setLegacyId(CcdConstants.NWHIN_LEGACY_ID_STR);
        personAddress.setLegacyTable(legacyTable);
        personAddress.setOrganization(organization);

        personAddress.setPerson(person);
        if (CollectionUtils.isNotEmpty(ccdAddress.getUses())) {
            PostalAddressUse ccdPostalAddressUse = ccdAddress.getUses().get(0);
            personAddress.setPostalAddressUse(ccdPostalAddressUse.getName());
        }
        personAddress.setStreetAddress(getTextFromFirstCcdAddressRecord(ccdAddress.getStreetAddressLines()));
        personAddress.setCity(getTextFromFirstCcdAddressRecord(ccdAddress.getCities()));
        personAddress.setState(getTextFromFirstCcdAddressRecord(ccdAddress.getStates()));
        personAddress.setCountry(getTextFromFirstCcdAddressRecord(ccdAddress.getCountries()));
        personAddress.setPostalCode(getTextFromFirstCcdAddressRecord(ccdAddress.getPostalCodes()));
        return personAddress;
    }

    public static CommunityAddress createAddress(AD ccdAddress, Organization organization, Community community,
                                                 String legacyTable) {
        CommunityAddress communityAddress = new CommunityAddress();
        communityAddress.setLegacyId(CcdConstants.NWHIN_LEGACY_ID_STR);
        communityAddress.setOrganization(organization);
        communityAddress.setLegacyTable(legacyTable);

        communityAddress.setOrganization(organization);
        if (CollectionUtils.isNotEmpty(ccdAddress.getUses())) {
            PostalAddressUse ccdPostalAddressUse = ccdAddress.getUses().get(0);
            communityAddress.setPostalAddressUse(ccdPostalAddressUse.getName());
        }
        communityAddress.setStreetAddress(getTextFromFirstCcdAddressRecord(ccdAddress.getStreetAddressLines()));
        communityAddress.setCity(getTextFromFirstCcdAddressRecord(ccdAddress.getCities()));
        communityAddress.setState(getTextFromFirstCcdAddressRecord(ccdAddress.getStates()));
        communityAddress.setCountry(getTextFromFirstCcdAddressRecord(ccdAddress.getCountries()));
        communityAddress.setPostalCode(getTextFromFirstCcdAddressRecord(ccdAddress.getPostalCodes()));
        return communityAddress;
    }

    public static BirthplaceAddress createAddress(AD ccdAddress, Client client) {
        if (!CcdParseUtils.hasContent(ccdAddress)) {
            return null;
        }

        final BirthplaceAddress birthplaceAddress = new BirthplaceAddress();
        birthplaceAddress.setClient(client);

        if (CollectionUtils.isNotEmpty(ccdAddress.getUses())) {
            final PostalAddressUse ccdPostalAddressUse = ccdAddress.getUses().get(0);
            birthplaceAddress.setPostalAddressUse(ccdPostalAddressUse.getName());
        }
        // street in birthplace is uncommon; it's not listed in the specification.
        birthplaceAddress.setStreetAddress(getTextFromFirstCcdAddressRecord(ccdAddress.getStreetAddressLines()));
        birthplaceAddress.setCity(getTextFromFirstCcdAddressRecord(ccdAddress.getCities()));
        birthplaceAddress.setState(getTextFromFirstCcdAddressRecord(ccdAddress.getStates()));
        birthplaceAddress.setCountry(getTextFromFirstCcdAddressRecord(ccdAddress.getCountries()));
        birthplaceAddress.setPostalCode(getTextFromFirstCcdAddressRecord(ccdAddress.getPostalCodes()));

        return birthplaceAddress;
    }

    // TODO move normalization to a trigger
    private static String normalizeName(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase().replaceAll("[' \\-]", "");
    }

    public static Name createName(PN ccdName, Organization organization, Person person, String legacyTable) {
        Name name = new Name();
        name.setLegacyId(CcdConstants.NWHIN_LEGACY_ID_STR);
        name.setLegacyTable(legacyTable);
        name.setOrganization(organization);

        name.setPerson(person);
        if (CollectionUtils.isNotEmpty(ccdName.getUses())) {
            EntityNameUse ccdEntityNameUse = ccdName.getUses().get(0);
            name.setNameUse(ccdEntityNameUse.getName());
        }

        Pair<String, String> family = getValueAndQualifierFromFirstEnxp(ccdName.getFamilies());
        if (family != null) {
            name.setFamily(family.getFirst());
            name.setFamilyNormalized(normalizeName(family.getFirst()));
            name.setFamilyQualifier(family.getSecond());
        }

        Pair<String, String> given = getValueAndQualifierFromFirstEnxp(ccdName.getGivens());
        if (given != null) {
            if ("CL".equals(given.getSecond())) {
                name.setPreferredName(given.getFirst());
            } else if ("AC".equals(given.getSecond())) {
                name.setDegree(given.getFirst());
            } else {
                name.setGiven(given.getFirst());
                name.setGivenNormalized(normalizeName(given.getFirst()));
                name.setGivenQualifier(given.getSecond());
            }
        }

        Pair<String, String> middle = getValueAndQualifierFromSecondEnxp(ccdName.getGivens());
        if (middle != null) {
            if ("CL".equals(middle.getSecond())) {
                name.setPreferredName(middle.getFirst());
            } else if ("AC".equals(middle.getSecond())) {
                name.setDegree(middle.getFirst());
            } else {
                name.setMiddle(middle.getFirst());
                name.setMiddleNormalized(normalizeName(middle.getFirst()));
                name.setMiddleQualifier(middle.getSecond());
            }
        }

        Pair<String, String> prefix = getValueAndQualifierFromFirstEnxp(ccdName.getPrefixes());
        if (prefix != null) {
            name.setPrefix(prefix.getFirst());
            name.setPrefixQualifier(prefix.getSecond());
        }

        Pair<String, String> suffix = getValueAndQualifierFromFirstEnxp(ccdName.getSuffixes());
        if (suffix != null) {
            name.setSuffix(suffix.getFirst());
            name.setSuffixQualifier(suffix.getSecond());
        }

        String text = ccdName.getText(true);
        if (StringUtils.isNotBlank(text)) {
            name.setFullName(StringUtils.abbreviate(text, 255));
        }

        return name;
    }

    public static Person createPerson(EList<PN> ccdNames, EList<AD> ccdAddresses, EList<TEL> ccdTelecoms,
                                      Organization organization, String legacyTable, String legacyId) {
        Person person = new Person();
        person.setOrganization(organization);
        person.setLegacyId(legacyId);
        person.setLegacyTable(legacyTable);

        List<Name> names = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ccdNames)) {
            for (PN ccdName : ccdNames) {
                if (hasContent(ccdName)) {
                    Name name = createName(ccdName, organization, person, legacyTable);
                    names.add(name);
                }
            }
        }
        person.setNames(names);
        List<PersonAddress> addresses = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ccdAddresses)) {
            for (AD ccdAddress : ccdAddresses) {
                if (hasContent(ccdAddress)) {
                    PersonAddress address = createAddress(ccdAddress, organization, person, legacyTable);
                    addresses.add(address);
                }
            }
        }
        person.setAddresses(addresses);
        Map<Integer, PersonTelecom> telecoms = new LinkedHashMap<>();
        if (CollectionUtils.isNotEmpty(ccdTelecoms)) {
            // Using map to ensure unique constraint in PersonTelecom table
            for (TEL ccdTelecom : ccdTelecoms) {
                if (hasContent(ccdTelecom)) {
                    PersonTelecom telecom = createTelecom(ccdTelecom, organization, person, legacyTable);
                    telecoms.put(telecom.getSyncQualifier(), telecom);
                }
            }
        }
        person.setTelecoms(new ArrayList<>(telecoms.values()));

        return person;
    }

    public static Person createPerson(ParticipantRole participantRole, Organization organization, String legacyTable) {
        Validate.notNull(participantRole);
        if (participantRole.getPlayingEntity() == null) {
            return null;
        }

        String legacyId = CcdParseUtils.getFirstIdExtensionStr(participantRole.getIds());
        return createPerson(participantRole.getPlayingEntity().getNames(), participantRole.getAddrs(),
                participantRole.getTelecoms(), organization, legacyTable, legacyId);
    }

    public static Person createPerson(org.eclipse.mdht.uml.cda.Guardian guardian, Organization organization,
                                      String legacyTable) {
        Validate.notNull(guardian);
        if (guardian.getGuardianPerson() == null) {
            return null;
        }

        String legacyId = CcdParseUtils.getFirstIdExtensionStr(guardian.getIds());
        return createPerson(guardian.getGuardianPerson().getNames(), guardian.getAddrs(), guardian.getTelecoms(),
                organization, legacyTable, legacyId);
    }

    public static Community createOrganization(ON ccdName, EList<AD> ccdAddresses, TEL ccdTelecom,
                                               Organization organization, String legacyTable, String legacyId) {
        Community community = new Community();
        community.setOrganization(organization);
        community.setOrganizationId(organization.getId());
        community.setLegacyId(legacyId);
        community.setLegacyTable(legacyTable);

        if (hasContent(ccdName)) {
            community.setName(ccdName.getText());
        }

        if (CollectionUtils.isNotEmpty(ccdAddresses)) {
            List<CommunityAddress> addresses = new ArrayList<>();
            for (AD ccdAddress : ccdAddresses) {
                if (hasContent(ccdAddress)) {
                    CommunityAddress address = createAddress(ccdAddress, organization, community, legacyTable);
                    addresses.add(address);
                }
            }
            community.setAddresses(addresses);
        }

        if (hasContent(ccdTelecom)) {
            community.setTelecom(createTelecom(ccdTelecom, organization, community, legacyTable));
        }

        return community;
    }

    public static Community createCommunity(EList<ON> ccdNames, EList<AD> ccdAddresses, EList<TEL> ccdTelecoms,
                                            Organization organization, String legacyTable, String legacyId) {
        ON ccdNameChosen = null;
        if (CollectionUtils.isNotEmpty(ccdNames)) {
            for (ON ccdName : ccdNames) {
                if (hasContent(ccdName)) {
                    ccdNameChosen = ccdName;
                    break;
                }
            }
        }
        TEL ccdTelecomChosen = null;
        TEL firstTelecom = null;
        if (CollectionUtils.isNotEmpty(ccdTelecoms)) {
            for (TEL ccdTelecom : ccdTelecoms) {
                if (ccdTelecom.getUses().contains(TelecommunicationAddressUse.WP)) {
                    ccdTelecomChosen = ccdTelecom;
                    break;
                }
                if (firstTelecom == null)
                    firstTelecom = ccdTelecom;
            }
            if (ccdTelecomChosen == null)
                ccdTelecomChosen = firstTelecom;
        }
        return createOrganization(ccdNameChosen, ccdAddresses, ccdTelecomChosen, organization, legacyTable, legacyId);
    }

    public static <T extends InfrastructureRoot> List<T> findByTemplateId(EList<T> roots, String templateId) {
        if (CollectionUtils.isEmpty(roots) || StringUtils.isEmpty(templateId)) {
            return Collections.emptyList();
        }

        List<T> resultList = new ArrayList<>();
        for (T root : roots) {
            for (II ii : root.getTemplateIds()) {
                if (templateId.equals(ii.getRoot())) {
                    resultList.add(root);
                    break;
                }
            }
        }

        return resultList;
    }

    public static List<Performer2> findByCode(EList<Performer2> performers, String code) {
        if (CollectionUtils.isEmpty(performers) || StringUtils.isEmpty(code)) {
            return Collections.emptyList();
        }

        final List<Performer2> result = new ArrayList<>();
        for (Performer2 performer : performers) {
            final CE ce = performer.getAssignedEntity().getCode();
            if (hasContent(ce) && code.equals(ce.getCode())) {
                result.add(performer);
            }
        }

        return result;
    }

    private static String[] SUPPORTED_DATE_PATTERNS = {"yyyyMMddHHmmss.SSSZ", "yyyyMMddhhmmssZ", "yyyyMMddhhmmss",
            "yyyyMMddhhmmZ", // "201805140000-0300"
            "yyyyMMddhhmm", // "201805140000"
            "yyyyMMddhhZ", // "2018051400-0300"
            "yyyyMMddhh", "yyyyMMdd", // "20180514"
            "yyyyMM", "yyyy"};

    public static Date parseDate(String dateString) {
        if (dateString == null) {
            return null;
        }

        try {
            return DateUtils.parseDate(dateString, SUPPORTED_DATE_PATTERNS);
        } catch (ParseException e) {
            logger.warn(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static LocalDate parseLocalDate(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder();
            Arrays.stream(SUPPORTED_DATE_PATTERNS).forEach(s -> dateTimeFormatterBuilder.appendPattern("[" + s + "]"));
            return dateTimeFormatterBuilder.toFormatter().parse(dateString, LocalDate::from);
        } catch (DateTimeParseException e) {
            logger.warn(ExceptionUtils.getStackTrace(e));
            return null;
        }
    }

    public static Date convertTsToDate(TS date) {
        Date result = null;
        if (hasContent(date)) {
            result = parseDate(date.getValue());
        }
        return result;
    }

    public static LocalDate convertTsToLocalDate(TS date) {
        LocalDate result = null;
        if (hasContent(date)) {
            result = parseLocalDate(date.getValue());
        }
        return result;
    }

    public static Date parseCenterTime(IVL_TS time) {
        if (!hasContent(time) || time.getCenter() == null) {
            return null;
        }

        String center = time.getCenter().getValue();
        return parseDate(center);
    }

    public static String parseFreeText(Section section) {
        final StringWriter sw = new StringWriter();
        final Map<String, Object> options = new HashMap<>();
        options.put(XMLResource.OPTION_ROOT_OBJECTS, Collections.singletonList(section.getText()));
        try {
            ((XMLResource) section.getText().eResource()).save(sw, options);
        } catch (IOException e) {
            logger.warn(ExceptionUtils.getStackTrace(e));
        }
        return removeTextTag(sw.toString());
    }

    private static String removeTextTag(String s) {
        return s.substring(s.indexOf(">", s.indexOf("<text")) + 1, s.lastIndexOf("</text>"));
    }
}
