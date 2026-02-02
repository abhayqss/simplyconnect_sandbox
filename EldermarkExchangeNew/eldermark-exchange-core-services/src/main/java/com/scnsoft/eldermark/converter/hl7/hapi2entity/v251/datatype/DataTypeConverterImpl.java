package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.datatype;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractPrimitive;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v251.datatype.*;
import com.scnsoft.eldermark.dao.HL7CodeTableDao;
import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQuery;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DataTypeConverterImpl implements DataTypeConverter {

    private static final Logger logger = LoggerFactory.getLogger(DataTypeConverterImpl.class);

    private static final DateTimeFormatter yearPrecision = new DateTimeFormatterBuilder().appendPattern("yyyy[Z]")
            .parseDefaulting(ChronoField.DAY_OF_YEAR, 1)
            .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter monthPrecision = new DateTimeFormatterBuilder().appendPattern("yyyyMM[Z]")
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter dayPrecision = new DateTimeFormatterBuilder().appendPattern("yyyyMMdd[Z]")
            .parseDefaulting(ChronoField.NANO_OF_DAY, 0)
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter hourPrecision = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHH[Z]")
            .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter minutePrecision = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmm[Z]")
            .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter secondPrecision = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss[Z]")
            .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter nano1Precision = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss.S[Z]")
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter nano2Precision = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss.SS[Z]")
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter nano3Precision = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss.SSS[Z]")
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    private static final DateTimeFormatter nano4Precision = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss.SSSS[Z]")
            .toFormatter()
            .withZone(ZoneId.of("UTC"));

    @Autowired
    private HL7CodeTableDao hL7CodeTableDao;

    @Override
    public CECodedElement createCE(CE ce231) {
        if (isEmpty(ce231)) {
            return null;
        }
        var ce = new CECodedElement();
        ce.setIdentifier(getValue(ce231.getCe1_Identifier()));
        ce.setText(getValue(ce231.getCe2_Text()));
        ce.setNameOfCodingSystem(getValue(ce231.getCe3_NameOfCodingSystem()));
        ce.setAlternateIdentifier(getValue(ce231.getCe4_AlternateIdentifier()));
        ce.setAlternateText(getValue(ce231.getCe5_AlternateText()));
        ce.setNameOfAlternateCodingSystem(getValue(ce231.getCe6_NameOfAlternateCodingSystem()));
        return ce;
    }

    @Override
    public CECodedElement createCE(CWE cwe) {
        if (isEmpty(cwe)) {
            return null;
        }
        var ce = new CECodedElement();
        ce.setIdentifier(getValue(cwe.getCwe1_Identifier()));
        ce.setText(getValue(cwe.getCwe2_Text()));
        ce.setNameOfCodingSystem(getValue(cwe.getCwe3_NameOfCodingSystem()));
        ce.setAlternateIdentifier(getValue(cwe.getCwe4_AlternateIdentifier()));
        ce.setAlternateText(getValue(cwe.getCwe5_AlternateText()));
        ce.setNameOfAlternateCodingSystem(getValue(cwe.getCwe6_NameOfAlternateCodingSystem()));
        return ce;
    }

    @Override
    public List<CECodedElement> createCEList(CE[] ces231) {
        if (ces231 == null || ces231.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<CECodedElement>(ces231.length);
        for (CE ce : ces231) {
            addIfPresent(result, createCE(ce));
        }
        return result;
    }

    @Override
    public CECodedElement createCEWithCodedValues(CE ce231, Class<? extends HL7CodeTable> tableClass) {
        final CECodedElement ce = createCE(ce231);
        if (ce == null || ce.getIdentifier() == null) {
            return ce;
        }
        ce.setHl7CodeTable(hL7CodeTableDao.findByCodeAndType(ce.getIdentifier(), tableClass).orElse(null));
        return ce;
    }

    @Override
    public List<CECodedElement> createCEListWithCodedValues(CE[] ces231, Class<? extends HL7CodeTable> tableClass) {
        if (ces231 == null || ces231.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<CECodedElement>(ces231.length);
        for (CE ce : ces231) {
            addIfPresent(result, createCEWithCodedValues(ce, tableClass));
        }
        return result;
    }

    @Override
    public CECodedElement createCEWithCodedValuesFromIS(IS is231, Class<? extends HL7CodeTable> tableClass) {
        if (isEmpty(is231)) {
            return null;
        }
        final CECodedElement ce = new CECodedElement();
        ce.setIdentifier(is231.getValue());
        ce.setHl7CodeTable(hL7CodeTableDao.findByCodeAndType(ce.getIdentifier(), tableClass).orElse(null));
        return ce;
    }

    @Override
    public EIEntityIdentifier createEI(EI source) {
        if (isEmpty(source)) {
            return null;
        }
        var ei = new EIEntityIdentifier();
        ei.setEntityIdentifier(getValue(source.getEi1_EntityIdentifier()));
        ei.setNamespaceId(getValue(source.getEi2_NamespaceID()));
        ei.setUniversalId(getValue(source.getEi3_UniversalID()));
        ei.setUniversalIdType(getValue(source.getEi4_UniversalIDType()));
        return ei;
    }

    @Override
    public EIPEntityIdentifierPair createEIP(EIP source) {
        if (isEmpty(source)) {
            return null;
        }
        var eip = new EIPEntityIdentifierPair();

        eip.setPlacerAssignedIdentifier(createEI(source.getEip1_PlacerAssignedIdentifier()));
        eip.setFillerAssignedIdentifier(createEI(source.getEip2_FillerAssignedIdentifier()));
        return eip;
    }

    @Override
    public DRDateRange createDR(DR source) {
        if (isEmpty(source)) {
            return null;
        }
        var dr = new DRDateRange();
        dr.setRangeStartDatetime(convertTS(source.getDr1_RangeStartDateTime()));
        dr.setRangeEndDatetime(convertTS(source.getDr2_RangeEndDateTime()));
        return dr;
    }

    @Override
    public CXExtendedCompositeId createCX(CX source) {
        if (isEmpty(source)) {
            return null;
        }
        final CXExtendedCompositeId cx = new CXExtendedCompositeId();
        cx.setpId(getValue(source.getCx1_IDNumber()));
        cx.setAssigningAuthority(createHd(source.getAssigningAuthority()));
        cx.setAssigningFacility(createHd(source.getAssigningFacility()));
        cx.setIdentifierTypeCode(getValue(source.getIdentifierTypeCode()));
        return cx;
    }

    @Override
    public List<CXExtendedCompositeId> createCXList(CX[] cx231Array) {
        if (cx231Array == null || cx231Array.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<CXExtendedCompositeId>(cx231Array.length);
        for (CX cx : cx231Array) {
            addIfPresent(result, createCX(cx));
        }
        return result;
    }

    @Override
    public XCNExtendedCompositeIdNumberAndNameForPersons createXCN(final XCN source) {
        if (isEmpty(source)) {
            return null;
        }
        var xcn = new XCNExtendedCompositeIdNumberAndNameForPersons();
        xcn.setIdNumber(getValue(source.getXcn1_IDNumber()));
        xcn.setLastName(getValue(source.getXcn2_FamilyName().getFn1_Surname())); //todo test emptiness
        xcn.setFirstName(getValue(source.getXcn3_GivenName()));
        xcn.setMiddleName(getValue(source.getXcn4_SecondAndFurtherGivenNamesOrInitialsThereof()));
        xcn.setSuffix(getValue(source.getXcn5_SuffixEgJRorIII()));
        xcn.setPrefix(getValue(source.getXcn6_PrefixEgDR()));
        xcn.setDegree(getValue(source.getXcn7_DegreeEgMD()));
        xcn.setSourceTable(getValue(source.getXcn8_SourceTable()));
        xcn.setAssigningAuthority(createHd(source.getXcn9_AssigningAuthority()));
        xcn.setNameTypeCode(getValue(source.getXcn10_NameTypeCode()));
        xcn.setIdentifierTypeCode(getValue(source.getXcn13_IdentifierTypeCode()));
        xcn.setAssigningFacility(createHd(source.getXcn14_AssigningFacility()));
        xcn.setNameRepresentationCode(getValue(source.getXcn15_NameRepresentationCode()));
        return xcn;
    }

    @Override
    public XCNExtendedCompositeIdNumberAndNameForPersons createFirstPresentXCN(XCN[] xnc231List) {
        if (xnc231List == null || xnc231List.length == 0) {
            return null;
        }
        for (XCN xcn231 : xnc231List) {
            if (!isEmpty(xcn231)) {
                return createXCN(xcn231);
            }
        }
        return null;
    }

    @Override
    public List<XCNExtendedCompositeIdNumberAndNameForPersons> createXCNList(XCN[] xnc231List) {
        if (xnc231List == null || xnc231List.length == 0) {
            return null;
        }
        var result = new ArrayList<XCNExtendedCompositeIdNumberAndNameForPersons>(xnc231List.length);
        for (var xcn : xnc231List) {
            addIfPresent(result, createXCN(xcn));
        }
        return result;
    }

    @Override
    public XONExtendedCompositeNameAndIdForOrganizations createXON(XON xon231) {
        if (isEmpty(xon231)) {
            return null;
        }
        final XONExtendedCompositeNameAndIdForOrganizations xon = new XONExtendedCompositeNameAndIdForOrganizations();
        xon.setOrganizationName(getValue(xon231.getXon1_OrganizationName()));
        xon.setOrganizationNameTypeCode(getValue(xon231.getXon2_OrganizationNameTypeCode()));
        xon.setIdNumber(getValue(xon231.getXon3_IDNumber()));
        xon.setAssigningAuthority(createHd(xon231.getXon6_AssigningAuthority()));
        xon.setIdentifierTypeCode(getValue(xon231.getXon7_IdentifierTypeCode()));
        xon.setAssigningFacility(createHd(xon231.getXon8_AssigningFacility()));
        xon.setNameRepresentationCode(getValue(xon231.getXon9_NameRepresentationCode()));
        return xon;
    }

    @Override
    public List<XONExtendedCompositeNameAndIdForOrganizations> createXONList(XON[] xons231) {
        if (xons231 == null || xons231.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<XONExtendedCompositeNameAndIdForOrganizations>(xons231.length);
        for (XON xon : xons231) {
            addIfPresent(result, createXON(xon));
        }
        return result;
    }

    @Override
    public HDHierarchicDesignator createHd(HD identifier) {
        if (isEmpty(identifier)) {
            return null;
        }
        return new HDHierarchicDesignator(
                getValue(identifier.getNamespaceID()),
                getValue(identifier.getUniversalID()),
                getValue(identifier.getUniversalIDType())
        );
    }

    @Override
    public MSGMessageType createMSG(MSG msg) {
        if (isEmpty(msg)) {
            return null;
        }
        return new MSGMessageType(
                getValue(msg.getMessageCode()),
                getValue(msg.getTriggerEvent()),
                getValue(msg.getMessageStructure())
        );
    }

    @Override
    public Instant convertTS(TS tsDate) {
        if (isEmpty(tsDate)) {
            return null;
        }
        //TS format is YYYY[MM[DD[HH[MM[SS[.S[S[S[S]]]]]]]]][+/-ZZZZ]
        return convertHL7Date(getValue(tsDate.getTs1_Time()), Instant::from);
    }

    @Override
    public LocalDateTime convertTSToLocalDateTime(TS tsDate) {
        if (isEmpty(tsDate)) {
            return null;
        }
        //TS format is YYYY[MM[DD[HH[MM[SS[.S[S[S[S]]]]]]]]][+/-ZZZZ]
        return convertHL7Date(getValue(tsDate.getTs1_Time()), LocalDateTime::from);
    }

    @Override
    public LocalDate convertTSToLocalDate(TS tsDate) {
        if (isEmpty(tsDate)) {
            return null;
        }
        //TS format is YYYY[MM[DD[HH[MM[SS[.S[S[S[S]]]]]]]]][+/-ZZZZ]
        return convertHL7Date(getValue(tsDate.getTs1_Time()), LocalDate::from);
    }

    @Override
    public Instant convertDtToInstant(DT dt) {
        if (isEmpty(dt)) {
            return null;
        }
        return convertHL7Date(getValue(dt));
    }

    @Override
    public Instant convertHL7Date(String fromDate) {
        return convertHL7Date(fromDate, Instant::from);
    }

    private <T> T convertHL7Date(String fromDate, TemporalQuery<T> query) {
        if (StringUtils.isBlank(fromDate)) {
            return null;
        }
        var negativeSignPos = fromDate.indexOf('-');
        var positiveSignPos = fromDate.indexOf('+');
        var lengthWithoutZoneOffset = fromDate.length();
        if (negativeSignPos > 0 || positiveSignPos > 0) {
            lengthWithoutZoneOffset = Math.max(negativeSignPos, positiveSignPos);
        }
        if (lengthWithoutZoneOffset == 4) {
            return yearPrecision.parse(fromDate, query);
        }
        if (lengthWithoutZoneOffset == 6) {
            return monthPrecision.parse(fromDate, query);
        }
        if (lengthWithoutZoneOffset == 8) {
            return dayPrecision.parse(fromDate, query);
        }
        if (lengthWithoutZoneOffset == 10) {
            return hourPrecision.parse(fromDate, query);
        }
        if (lengthWithoutZoneOffset == 12) {
            return minutePrecision.parse(fromDate, query);
        }
        if (lengthWithoutZoneOffset == 14) {
            return secondPrecision.parse(fromDate, query);
        }
        if (lengthWithoutZoneOffset == 16) {
            return nano1Precision.parse(fromDate, query);
        }
        if (lengthWithoutZoneOffset == 17) {
            return nano2Precision.parse(fromDate, query);
        }
        if (lengthWithoutZoneOffset == 18) {
            return nano3Precision.parse(fromDate, query);
        }
        if (lengthWithoutZoneOffset == 19) {
            return nano4Precision.parse(fromDate, query);
        }
        return null;
    }

    @Override
    public PLPatientLocation createPL(PL pl) {
        if (isEmpty(pl)) {
            return null;
        }
        var plPatientLocation = new PLPatientLocation();
        plPatientLocation.setPointOfCare(getValue(pl.getPl1_PointOfCare()));
        plPatientLocation.setRoom(getValue(pl.getPl2_Room()));
        plPatientLocation.setBed(getValue(pl.getPl3_Bed()));
        plPatientLocation.setFacility(createHd(pl.getPl4_Facility()));
        plPatientLocation.setLocationStatus(getValue(pl.getPl5_LocationStatus()));
        plPatientLocation.setPersonLocationType(getValue(pl.getPl6_PersonLocationType()));
        plPatientLocation.setBuilding(getValue(pl.getPl7_Building()));
        plPatientLocation.setFloor(getValue(pl.getPl8_Floor()));
        plPatientLocation.setLocationDescription(getValue(pl.getPl9_LocationDescription()));
        return plPatientLocation;
    }


    @Override
    public DLDDischargeLocation createDLD(DLD dld) {
        if (isEmpty(dld)) {
            return null;
        }
        return new DLDDischargeLocation(getValue(dld.getDischargeLocation()), convertTS(dld.getEffectiveDate()));
    }

    @Override
    public XTNPhoneNumber createXTN(XTN xtn) {
        if (isEmpty(xtn)) {
            return null;
        }
        var xtnPhoneNumber = new XTNPhoneNumber();
        xtnPhoneNumber.setTelephoneNumber(getValue(xtn.getXtn1_TelephoneNumber()));
        xtnPhoneNumber.setTelecommunicationUseCode(createID(xtn.getXtn2_TelecommunicationUseCode(), HL7CodeTable0201TelecommunicationUseCode.class));
        xtnPhoneNumber.setTelecommunicationEquipmentType(createID(xtn.getXtn3_TelecommunicationEquipmentType(), HL7CodeTable0202TelecommunicationEquipmentType.class));
        xtnPhoneNumber.setEmail(getValue(xtn.getXtn4_EmailAddress()));
        xtnPhoneNumber.setCountryCode(getValue(xtn.getXtn5_CountryCode()));
        xtnPhoneNumber.setAreaCode(getValue(xtn.getXtn6_AreaCityCode()));
        xtnPhoneNumber.setPhoneNumber(getValue(xtn.getXtn7_LocalNumber()));
        xtnPhoneNumber.setExtension(getValue(xtn.getXtn8_Extension()));
        xtnPhoneNumber.setAnyText(getValue(xtn.getXtn9_AnyText()));
        return xtnPhoneNumber;
    }

    @Override
    public List<XTNPhoneNumber> createXTNList(XTN[] xtns) {
        if (xtns == null || xtns.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<XTNPhoneNumber>(xtns.length);
        for (XTN xtn : xtns) {
            addIfPresent(result, createXTN(xtn));
        }
        return result;
    }

    @Override
    public XPNPersonName createXPN(XPN xpn231) {
        if (isEmpty(xpn231)) {
            return null;
        }
        final XPNPersonName xpn = new XPNPersonName();
        xpn.setLastName(getValue(xpn231.getXpn1_FamilyName().getFn1_Surname()));
        xpn.setFirstName(getValue(xpn231.getXpn2_GivenName()));
        xpn.setMiddleName(getValue(xpn231.getXpn3_SecondAndFurtherGivenNamesOrInitialsThereof()));
        xpn.setSuffix(getValue(xpn231.getXpn4_SuffixEgJRorIII()));
        xpn.setPrefix(getValue(xpn231.getXpn5_PrefixEgDR()));
        xpn.setDegree(getValue(xpn231.getXpn6_DegreeEgMD()));
        xpn.setNameTypeCode(getValue(xpn231.getXpn7_NameTypeCode()));
        xpn.setNameRepresentationCode(getValue(xpn231.getXpn8_NameRepresentationCode()));
        return xpn;
    }

    @Override
    public List<XPNPersonName> createXPNList(XPN[] xpns231) {
        if (xpns231 == null || xpns231.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<XPNPersonName>(xpns231.length);
        for (XPN xpn : xpns231) {
            addIfPresent(result, createXPN(xpn));
        }
        return result;
    }

    @Override
    public XADPatientAddress createXAD(final XAD xad231) {
        if (isEmpty(xad231)) {
            return null;
        }
        final XADPatientAddress xad = new XADPatientAddress();
        xad.setStreetAddress(getValue(xad231.getXad1_StreetAddress().getSad1_StreetOrMailingAddress()));
        xad.setOtherDesignation(getValue(xad231.getXad2_OtherDesignation()));
        xad.setCity(getValue(xad231.getXad3_City()));
        xad.setState(getValue(xad231.getXad4_StateOrProvince()));
        xad.setZip(getValue(xad231.getXad5_ZipOrPostalCode()));
        xad.setCountry(createID(xad231.getXad6_Country(), HL7CodeTable0399CountryCode.class));
        xad.setAddressType(createID(xad231.getXad7_AddressType(), HL7CodeTable0190AddressType.class));
        xad.setOtherGeographicDesignation(getValue(xad231.getXad8_OtherGeographicDesignation()));
        xad.setCounty(getValue(xad231.getXad9_CountyParishCode()));
        xad.setCensusTract(getValue(xad231.getXad10_CensusTract()));
        xad.setAddressRepresentationCode(createID(xad231.getXad11_AddressRepresentationCode(), HL7CodeTable0465NameAddressRepresentation.class));
        return xad;
    }

    @Override
    public List<XADPatientAddress> createXADList(XAD[] xads231) {
        if (xads231 == null || xads231.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<XADPatientAddress>(xads231.length);
        for (XAD xad : xads231) {
            addIfPresent(result, createXAD(xad));
        }
        return result;
    }

    @Override
    public <T extends HL7UserDefinedCodeTable> ISCodedValueForUserDefinedTables<T> createIS(IS is231, Class<T> tableClass) {
        if (is231 == null) {
            return null;
        }

        var raw = getValue(is231);
        if (StringUtils.isEmpty(raw)) {
            return null;
        }

        var is = new ISCodedValueForUserDefinedTables<T>();
        is.setRawCode(is231.getValue());
        is.setHl7TableCode(hL7CodeTableDao.findByCodeAndType(is231.getValue(), tableClass).orElse(null));
        return is;
    }

    @Override
    public <T extends HL7UserDefinedCodeTable> ISCodedValueForUserDefinedTables<T> createISFromCE(CE ce213, Class<T> tableClass) {
        if (ce213 == null) {
            return null;
        }

        var raw = getValue(ce213.getCe1_Identifier());
        if (StringUtils.isEmpty(raw)) {
            return null;
        }

        var is = new ISCodedValueForUserDefinedTables<T>();
        is.setRawCode(ce213.getIdentifier().getValue());
        is.setHl7TableCode(hL7CodeTableDao.findByCodeAndType(ce213.getIdentifier().getValue(), tableClass).orElse(null));
        return is;
    }

    @Override
    public <T extends HL7UserDefinedCodeTable> List<ISCodedValueForUserDefinedTables<T>> createISList(IS[] is231Array, Class<T> tableClass) {
        if (is231Array == null || is231Array.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<ISCodedValueForUserDefinedTables<T>>(is231Array.length);
        for (IS is231 : is231Array) {
            result.add(createIS(is231, tableClass));
        }
        return result;
    }

    @Override
    public <T extends HL7DefinedCodeTable> IDCodedValueForHL7Tables<T> createID(ID id231, Class<T> tableClass) {
        if (isEmpty(id231)) {
            return null;
        }

        var raw = getValue(id231);
        if (StringUtils.isEmpty(raw)) {
            return null;
        }
        final IDCodedValueForHL7Tables<T> id = new IDCodedValueForHL7Tables<T>();
        id.setRawCode(raw);
        id.setHl7TableCode(hL7CodeTableDao.findByCodeAndType(raw, tableClass).orElse(null));
        return id;
    }

    @Override
    public List<String> createStringList(ST[] st231Array) {
        if (st231Array == null || st231Array.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<String>(st231Array.length);
        for (ST st : st231Array) {
            addIfPresent(result, getValue(st));
        }
        return result;
    }

    @Override
    public List<String> createStringList(Varies[] varies) {
        if (varies == null || varies.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<String>(varies.length);
        for (var v : varies) {
            addIfPresent(result, convertString(v));
        }
        return result;
    }

    @Override
    public List<String> createStringList(FT[] ftArr) {
        if (ftArr == null || ftArr.length == 0) {
            return Collections.emptyList();
        }
        var result = new ArrayList<String>(ftArr.length);
        for (var ft : ftArr) {
            addIfPresent(result, getValue(ft));
        }
        return result;
    }

    private String convertString(Varies source) {
        if (isEmpty(source)) {
            return null;
        }
        try {
            return source.getData().encode();
        } catch (HL7Exception e) {
            logger.warn("Failed to convert VARIES to String", e);
            return null;
        }
    }

    @Override
    public Integer convertNM(NM nm231) {
        if (isEmpty(nm231)) {
            return null;
        }
        return Integer.valueOf(getValue(nm231));
    }

    @Override
    public DLNDriverSLicenseNumber createDLN(DLN dln231) {
        if (isEmpty(dln231)) {
            return null;
        }
        final DLNDriverSLicenseNumber dln = new DLNDriverSLicenseNumber();
        dln.setLicenseNumber(getValue(dln231.getDln1_LicenseNumber()));
        dln.setIssuingStateProvinceCountry(getValue(dln231.getDln2_IssuingStateProvinceCountry()));
        dln.setExpirationDate(convertDtToInstant(dln231.getExpirationDate()));
        return dln;
    }

    @Override
    public PTProcessingType convertPT(PT source) {
        if (isEmpty(source)) {
            return null;
        }
        var pt = new PTProcessingType();
        pt.setProcessingId(getValue(source.getPt1_ProcessingID()));
        pt.setProcessingMode(getValue(source.getPt2_ProcessingMode()));
        return pt;
    }

    @Override
    public String getValue(AbstractPrimitive abstractPrimitive) {
        if (isEmpty(abstractPrimitive)) {
            return null;
        }
        return abstractPrimitive.getValue();
    }

    private <T> void addIfPresent(List<T> list, T t) {
        if (list != null && t != null) {
            list.add(t);
        }
    }

    private boolean isEmpty(Type type) {
        try {
            return type == null || type.isEmpty();
        } catch (HL7Exception e) {
            logger.warn("Exception during determining if type is empty", e);
            return true;
        }
    }
}
