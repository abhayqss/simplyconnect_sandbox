package org.openhealthtools.openxds.registry.patient.parser.datatype.impl;

import ca.uhn.hl7v2.model.AbstractPrimitive;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v231.datatype.*;
import com.misyshealthcare.connect.base.demographicdata.Address;
import com.misyshealthcare.connect.base.demographicdata.PhoneNumber;
import com.misyshealthcare.connect.net.Identifier;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openhealthexchange.openpixpdq.data.PersonName;
import org.openhealthtools.openxds.dao.HL7CodeTableDao;
import org.openhealthtools.openxds.entity.datatype.*;
import org.openhealthtools.openxds.entity.hl7table.*;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DataTypeServiceImpl implements DataTypeService {

    private static final Logger logger = Logger.getLogger(DataTypeServiceImpl.class);

    private static SimpleDateFormat hl7formatter1 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat hl7formatter2 = new SimpleDateFormat("yyyyMMddHHmm");
    private static SimpleDateFormat hl7formatter3 = new SimpleDateFormat("yyyyMMddHHmmss");
    private static SimpleDateFormat DTMformatter = new SimpleDateFormat("yyyyMMddHHmmssZ");

    @Autowired
    private HL7CodeTableDao hL7CodeTableDao;

    @Override
    public CECodedElement createCE(CE ce231) {
        CECodedElement ce = new CECodedElement();
        if (ce231.getIdentifier() != null) {
            ce.setIdentifier(ce231.getIdentifier().getValue());
        }
        if (ce231.getText() != null) {
            ce.setText(ce231.getText().getValue());
        }
        if (ce231.getNameOfCodingSystem() != null) {
            ce.setNameOfCodingSystem(ce231.getNameOfCodingSystem().getValue());
        }

        if (ce231.getAlternateIdentifier() != null) {
            ce.setAlternateIdentifier(ce231.getAlternateIdentifier().getValue());
        }
        if (ce231.getAlternateText() != null) {
            ce.setAlternateText(ce231.getAlternateText().getValue());
        }
        if (ce231.getNameOfAlternateCodingSystem() != null) {
            ce.setNameOfalternateCodingSystem(ce231.getNameOfAlternateCodingSystem().getValue());
        }
        return ce;
//	}


//        rawXdsMessage.setMessage(message);
//
//
//
//
//
//        rawXdsMessageDao.saveRawXdsMessage(rawXdsMessage);
    }

    @Override
    public List<CECodedElement> createCEList(CE[] ces231) {
        if (ces231 == null || ces231.length == 0) {
            return Collections.emptyList();
        }
        final List<CECodedElement> result = new ArrayList<CECodedElement>(ces231.length);
        for (CE ce : ces231) {
            addIfPresent(result, createCE(ce));
        }
        return result;
    }

    @Override
    public XTNPhoneNumber createXTNPhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        XTNPhoneNumber xtnPhoneNumber = new XTNPhoneNumber();
        xtnPhoneNumber.setPhoneNumber(phoneNumber.getNumber());
        xtnPhoneNumber.setEmail(phoneNumber.getEmail());
        xtnPhoneNumber.setCountryCode(phoneNumber.getCountryCode());
        xtnPhoneNumber.setAreaCode(phoneNumber.getAreaCode());
        xtnPhoneNumber.setExtension(phoneNumber.getExtension());
        xtnPhoneNumber.setAnyText(phoneNumber.getNote());
        return xtnPhoneNumber;
    }

    @Override
    public XADPatientAddress createXADPatientAddress(List<Address> addresses) {
        if (CollectionUtils.isEmpty(addresses)) {
            return null;
        }
        return createXADPatientAddress(addresses.get(0));
    }

    @Override
    public XADPatientAddress createXADPatientAddress(final Address address) {
        if (address == null) {
            return null;
        }
        XADPatientAddress xadPatientAddress = new XADPatientAddress();
        xadPatientAddress.setStreetAddress(address.getAddLine1());
        xadPatientAddress.setOtherDesignation(address.getAddLine2());
        xadPatientAddress.setCity(address.getAddCity());
        xadPatientAddress.setCountry(createID(address.getAddCountry(), HL7CodeTable0399CountryCode.class));
        xadPatientAddress.setCounty(address.getAddCounty());
        xadPatientAddress.setState(address.getAddState());
        xadPatientAddress.setZip(address.getAddZip());
        xadPatientAddress.setAddressType(createID(address.getAddType().getValue(), HL7CodeTable0190AddressType.class));
        return xadPatientAddress;
    }

    @Override
    public XPNPersonName createPersonName(PersonName patientName) {
        XPNPersonName xpnPersonName = new XPNPersonName();
        xpnPersonName.setFirstName(patientName.getFirstName());
        xpnPersonName.setLastName(patientName.getLastName());
        xpnPersonName.setMiddleName(patientName.getSecondName());
        xpnPersonName.setPrefix(patientName.getPrefix());
        xpnPersonName.setSuffix(patientName.getSuffix());
        xpnPersonName.setDegree(patientName.getDegree());
        xpnPersonName.setNameTypeCode(patientName.getNameTypeCode());
        xpnPersonName.setNameRepresentationCode(patientName.getNameRepresentationCode());
        return xpnPersonName;
    }

    @Override
    public XCNExtendedCompositeIdNumberAndNameForPersons createXCN(XCN[] xnc231List) {
//        List<XCNExtendedCompositeIdNumberAndNameForPersons> xcnList = new ArrayList<XCNExtendedCompositeIdNumberAndNameForPersons>();
        if (xnc231List != null) {
            for (XCN xcn231 : xnc231List) {
                return createXCN(xcn231);
            }
        }
        return null;
    }

    @Override
    public XCNExtendedCompositeIdNumberAndNameForPersons createXCN(final XCN xcn231) {
        XCNExtendedCompositeIdNumberAndNameForPersons xcn = new XCNExtendedCompositeIdNumberAndNameForPersons();
        if (xcn231.getGivenName() != null)
            xcn.setFirstName(xcn231.getGivenName().getValue());
        if (xcn231.getFamilyLastName() != null)
            xcn.setLastName(xcn231.getFamilyLastName().getFamilyName().getValue());
        if (xcn231.getMiddleInitialOrName() != null)
            xcn.setMiddleName(xcn231.getMiddleInitialOrName().getValue());
        if (xcn231.getPrefixEgDR() != null)
            xcn.setPrefix(xcn231.getPrefixEgDR().getValue());
        if (xcn231.getSuffixEgJRorIII() != null)
            xcn.setSuffix(xcn231.getSuffixEgJRorIII().getValue());
        if (xcn231.getDegreeEgMD() != null)
            xcn.setDegree(xcn231.getDegreeEgMD().getValue());
        if (xcn231.getSourceTable() != null)
            xcn.setSourceTable(xcn231.getSourceTable().getValue());
        if (xcn231.getAssigningAuthority() != null)
            xcn.setAssigningAuthority(createHd(xcn231.getAssigningAuthority()));
        if (xcn231.getAssigningFacility() != null)
            xcn.setAssigningFacility(createHd(xcn231.getAssigningFacility()));
        if (xcn231.getNameTypeCode() != null)
            xcn.setNameTypeCode(xcn231.getNameTypeCode().getValue());
        if (xcn231.getIdentifierTypeCode() != null)
            xcn.setIdentifierTypeCode(xcn231.getIdentifierTypeCode().getValue());
        if (xcn231.getNameRepresentationCode() != null)
            xcn.setNameRepresentationCode(xcn231.getNameRepresentationCode().getValue());
        return xcn;
    }


    @Override
    public XONExtendedCompositeNameAndIdForOrganizations createXON(XON xon231) {
        if (xon231 == null) {
            return null;
        }
        final XONExtendedCompositeNameAndIdForOrganizations xon = new XONExtendedCompositeNameAndIdForOrganizations();
        xon.setOrganizationName(getValue(xon231.getOrganizationName()));
        xon.setOrganizationNameTypeCode(getValue(xon231.getOrganizationNameTypeCode()));
        xon.setIdNumber(getValue(xon231.getIDNumber()));
        xon.setAssigningAuthority(createHd(xon231.getAssigningAuthority()));
        xon.setAssigningFacility(createHd(xon231.getAssigningFacilityID()));
        xon.setIdentifierTypeCode(getValue(xon231.getIdentifierTypeCode()));
        xon.setNameRepresentationCode(getValue(xon231.getNameRepresentationCode()));
        return xon;
    }

    @Override
    public CXExtendedCompositeId createCX(org.openhealthexchange.openpixpdq.data.PersonIdentifier patientIdentifier) {
        CXExtendedCompositeId cxPersonIdentifier = new CXExtendedCompositeId();
        cxPersonIdentifier.setpId(patientIdentifier.getId());
        cxPersonIdentifier.setAssigningAuthority(createHd(patientIdentifier.getAssigningAuthority()));
        cxPersonIdentifier.setAssigningFacility(createHd(patientIdentifier.getAssigningFacility()));
        cxPersonIdentifier.setIdentifierTypeCode(patientIdentifier.getIdentifierTypeCode());
        return cxPersonIdentifier;
    }

    @Override
    public CXExtendedCompositeId createCX(CX cx231) {
        if (cx231 == null) {
            return null;
        }
        final CXExtendedCompositeId cx = new CXExtendedCompositeId();
        cx.setpId(getValue(cx231.getID()));
        cx.setAssigningAuthority(createHd(cx231.getAssigningAuthority()));
        cx.setAssigningFacility(createHd(cx231.getAssigningFacility()));
        cx.setIdentifierTypeCode(getValue(cx231.getIdentifierTypeCode()));
        return cx;
    }

    @Override
    public List<CXExtendedCompositeId> createCXList(CX[] cx231Array) {
        if (cx231Array == null || cx231Array.length == 0) {
            return Collections.emptyList();
        }
        final List<CXExtendedCompositeId> result = new ArrayList<CXExtendedCompositeId>(cx231Array.length);
        for (CX cx : cx231Array) {
            addIfPresent(result, createCX(cx));
        }
        return result;
    }

    @Override
    public HDHierarchicDesignator createHd(Identifier identifier) {
        if (identifier == null) {
            return null;
        }
        return new HDHierarchicDesignator(identifier.getNamespaceId(), identifier.getUniversalId(), identifier.getUniversalIdType());
    }

    @Override
    public HDHierarchicDesignator createHd(HD identifier) {
        if (identifier == null) {
            return null;
        }
        return new HDHierarchicDesignator(identifier.getNamespaceID().getValue(), identifier.getUniversalID().getValue(), identifier.getUniversalIDType().getValue());
    }

//    private static Date convertDateTime(String dateTimeStr) {
//        if (StringUtils.isNotBlank(dateTimeStr)) {
//            Calendar calendar = DateUtil.convertHL7DateToCalender(dateTimeStr);
//            return calendar.getTime();
//        }
//        return null;
//    }

    @Override
    public Date convertTsToDate(TS tsDate) {
        if (tsDate != null) {
            String dateTimeStr = getValue(tsDate.getTimeOfAnEvent());
            return convertHL7Date(dateTimeStr);
        }
        return null;
    }


//    public static Calendar convertHL7DateToCalender(String fromDate) {
//        if (fromDate == null) {
//            return null;
//        } else {
//            try {
//                Date date = null;
//                if (fromDate.length() == 8) {
//                    date = hl7formatter1.parse(fromDate);
//                } else {
//                    date = hl7formatter2.parse(fromDate);
//                }
//
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(date);
//                return cal;
//            } catch (ParseException var3) {
//                return null;
//            }
//        }
//    }

    @Override
    public Date convertHL7Date(String fromDate) {
        if (StringUtils.isBlank(fromDate)) {
            return null;
        } else {
            try {
                if (fromDate.length() == 8) {
                    return hl7formatter1.parse(fromDate);
                } else if (fromDate.length() == 12) {
                    return hl7formatter2.parse(fromDate);
                } else if (fromDate.length() == 14) {
                    return hl7formatter3.parse(fromDate);
                } else if (fromDate.length() > 14) {
                    return DTMformatter.parse(fromDate);
                } else {
                    return null;
                }
            } catch (ParseException var3) {
                return null;
            }
        }
    }

//    public static Calendar convertHL7DateToCalender(String fromDate) {
//        if (fromDate == null) {
//            return null;
//        } else {
//            try {
//                Date date = null;
//                if (fromDate.length() == 8) {
//                    date = hl7formatter1.parse(fromDate);
//                } else {
//                    date = hl7formatter2.parse(fromDate);
//                }
//
//                Calendar cal = Calendar.getInstance();
//                cal.setTime(date);
//                return cal;
//            } catch (ParseException var3) {
//                return null;
//            }
//        }
//    }

    @Override
    public Date convertDtToDate(DT dt) throws DataTypeException {
        if (dt == null || dt.getValue() == null) {
            return null;
        }
        Calendar c = Calendar.getInstance();
        c.set(dt.getYear(), dt.getMonth() - 1, dt.getDay());
        return c.getTime();
    }

    @Override
    public PLPatientLocation createPL(PL pl) {
        if (pl == null) {
            return null;
        }
        final PLPatientLocation plPatientLocation = new PLPatientLocation();
        plPatientLocation.setPointOfCare(getValue(pl.getPointOfCare()));
        plPatientLocation.setRoom(getValue(pl.getRoom()));
        plPatientLocation.setBed(getValue(pl.getBed()));
        plPatientLocation.setFacility(createHd(pl.getFacility()));
        plPatientLocation.setLocationStatus(getValue(pl.getLocationStatus()));
        plPatientLocation.setPersonLocationType(getValue(pl.getPersonLocationType()));
        plPatientLocation.setBuilding(getValue(pl.getBuilding()));
        plPatientLocation.setFloor(getValue(pl.getFloor()));
        plPatientLocation.setLocationDescription(getValue(pl.getLocationDescription()));
        return plPatientLocation;
    }

    @Override
    public DLDDischargeLocation createDLD(DLD dld) {
        if (dld == null) {
            return null;
        }
        return new DLDDischargeLocation(getValue(dld.getDischargeLocation()), convertTsToDate(dld.getEffectiveDate()));
    }

    @Override
    public String getValue(AbstractPrimitive abstractPrimitive) {
        if (abstractPrimitive == null) {
            return null;
        }
        return abstractPrimitive.getValue();
    }

    @Override
    public XTNPhoneNumber createXTN(XTN xtn) {
        if (xtn == null) {
            return null;
        }
        final XTNPhoneNumber xtnPhoneNumber = new XTNPhoneNumber();
        xtnPhoneNumber.setTelephoneNumber(getValue(xtn.get9999999X99999CAnyText()));
        xtnPhoneNumber.setEmail(getValue(xtn.getEmailAddress()));
        xtnPhoneNumber.setCountryCode(getValue(xtn.getCountryCode()));
        xtnPhoneNumber.setAreaCode(getValue(xtn.getAreaCityCode()));
        xtnPhoneNumber.setPhoneNumber(getValue(xtn.getPhoneNumber()));
        xtnPhoneNumber.setExtension(getValue(xtn.getExtension()));
        xtnPhoneNumber.setAnyText(getValue(xtn.getAnyText()));
        return xtnPhoneNumber;
    }

    @Override
    public List<XTNPhoneNumber> createXTNList(XTN[] xtns) {
        if (xtns == null || xtns.length == 0) {
            return Collections.emptyList();
        }
        final List<XTNPhoneNumber> result = new ArrayList<XTNPhoneNumber>(xtns.length);
        for (XTN xtn : xtns) {
            addIfPresent(result, createXTN(xtn));
        }
        return result;
    }

    @Override
    public List<XONExtendedCompositeNameAndIdForOrganizations> createXONList(XON[] xons231) {
        if (xons231 == null || xons231.length == 0) {
            return Collections.emptyList();
        }
        final List<XONExtendedCompositeNameAndIdForOrganizations> result = new ArrayList<XONExtendedCompositeNameAndIdForOrganizations>(xons231.length);
        for (XON xon : xons231) {
            addIfPresent(result, createXON(xon));
        }
        return result;
    }

    @Override
    public List<XPNPersonName> createXPNList(XPN[] xpns231) {
        if (xpns231 == null || xpns231.length == 0) {
            return Collections.emptyList();
        }
        final List<XPNPersonName> result = new ArrayList<XPNPersonName>(xpns231.length);
        for (XPN xpn : xpns231) {
            addIfPresent(result, createXPN(xpn));
        }
        return result;
    }

    @Override
    public XPNPersonName createXPN(XPN xpn231) {
        if (xpn231 == null) {
            return null;
        }
        final XPNPersonName xpn = new XPNPersonName();
        xpn.setLastName(getValue(xpn231.getFamilyLastName() != null ? xpn231.getFamilyLastName().getFamilyName() : null));
        xpn.setFirstName(getValue(xpn231.getGivenName()));
        xpn.setMiddleName(getValue(xpn231.getMiddleInitialOrName()));
        xpn.setSuffix(getValue(xpn231.getSuffixEgJRorIII()));
        xpn.setPrefix(getValue(xpn231.getPrefixEgDR()));
        xpn.setDegree(getValue(xpn231.getDegreeEgMD()));
        xpn.setNameTypeCode(getValue(xpn231.getNameTypeCode()));
        return xpn;
    }

    @Override
    public XADPatientAddress createXAD(final XAD xad231) {
        if (xad231 == null) {
            return null;
        }
        final XADPatientAddress xad = new XADPatientAddress();
        xad.setStreetAddress(getValue(xad231.getStreetAddress()));
        xad.setOtherDesignation(getValue(xad231.getOtherDesignation()));
        xad.setCity(getValue(xad231.getCity()));
        xad.setState(getValue(xad231.getStateOrProvince()));
        xad.setZip(getValue(xad231.getZipOrPostalCode()));
        xad.setCountry(createID(xad231.getCountry(), HL7CodeTable0399CountryCode.class));
        xad.setAddressType(createID(xad231.getAddressType(), HL7CodeTable0190AddressType.class));
        xad.setOtherGeographicDesignation(getValue(xad231.getOtherGeographicDesignation()));
        xad.setCounty(getValue(xad231.getCountyParishCode()));
        xad.setCensusTract(getValue(xad231.getCensusTract()));
        xad.setAddressRepresentationCode(createID(xad231.getAddressRepresentationCode(), HL7CodeTable0465NameAddressRepresentation.class));
        return xad;
    }

    @Override
    public List<XADPatientAddress> createXADList(XAD[] xads231) {
        if (xads231 == null || xads231.length == 0) {
            return Collections.emptyList();
        }
        final List<XADPatientAddress> result = new ArrayList<XADPatientAddress>(xads231.length);
        for (XAD xad: xads231) {
            addIfPresent(result, createXAD(xad));
        }
        return result;
    }

    @Override
    public <T extends HL7UserDefinedCodeTable> ISCodedValueForUserDefinedTables<T> createIS(IS is231, Class<T> tableClass) {
        //code might be empty string
        if (is231 == null || is231.getValue() == null) {
            return null;
        }
        final ISCodedValueForUserDefinedTables<T> is = new ISCodedValueForUserDefinedTables<T>();
        is.setRawCode(is231.getValue());
        is.setHl7TableCode(hL7CodeTableDao.findCode(is231.getValue(), tableClass));
        return is;
    }

    @Override
    public <T extends HL7UserDefinedCodeTable> List<ISCodedValueForUserDefinedTables<T>> createISList(IS[] is231Array, Class<T> tableClass) {
        if (is231Array == null || is231Array.length == 0) {
            return Collections.emptyList();
        }
        final List<ISCodedValueForUserDefinedTables<T>> result = new ArrayList<ISCodedValueForUserDefinedTables<T>>(is231Array.length);
        for (IS is231 : is231Array) {
            result.add(createIS(is231, tableClass));
        }
        return result;
    }

    @Override
    public CECodedElement createCEWithCodedValues(CE ce231, Class<? extends HL7CodeTable>... tableClasses) {
        final CECodedElement ce = createCE(ce231);
        if (ce == null || ce.getIdentifier() == null || tableClasses == null || tableClasses.length == 0) {
            return ce;
        }
        final List<Class<? extends HL7CodeTable>> classes = Arrays.asList(tableClasses);
        ce.setHl7CodeTable(hL7CodeTableDao.findCode(ce.getIdentifier(), classes));
        return ce;
    }

    @Override
    public List<CECodedElement> createCEListWithCodedValues(CE[] ces231, Class<? extends HL7CodeTable>... tableClasses) {
        if (ces231 == null || ces231.length == 0) {
            return Collections.emptyList();
        }
        final List<CECodedElement> result = new ArrayList<CECodedElement>(ces231.length);
        for (CE ce: ces231) {
            addIfPresent(result, createCEWithCodedValues(ce, tableClasses));
        }
        return result;
    }

    @Override
    public CECodedElement createCEWithCodedValuesFromIS(IS is231, Class<? extends HL7CodeTable>... tableClasses) {
        if (is231 == null || is231.getValue() == null || tableClasses == null || tableClasses.length == 0) {
            return null;
        }
        final CECodedElement ce = new CECodedElement();
        ce.setIdentifier(is231.getValue());
        final List<Class<? extends HL7CodeTable>> classes = Arrays.asList(tableClasses);
        ce.setHl7CodeTable(hL7CodeTableDao.findCode(ce.getIdentifier(), classes));
        return ce;
    }

    @Override
    public <T extends HL7DefinedCodeTable> IDCodedValueForHL7Tables<T> createID(ID id231, Class<T> tableClass) {
        if (id231 == null) {
            return null;
        }
        return createID(id231.getValue(), tableClass);
    }

    @Override
    public <T extends HL7DefinedCodeTable> IDCodedValueForHL7Tables<T> createID(String rawCode, Class<T> tableClass) {
        //code might be empty string
        if (rawCode == null) {
            return null;
        }
        final IDCodedValueForHL7Tables<T> id = new IDCodedValueForHL7Tables<T>();
        id.setRawCode(rawCode);
        id.setHl7TableCode(hL7CodeTableDao.findCode(rawCode, tableClass));
        return id;
    }

    @Override
    public List<String> createStringList(ST[] st231Array) {
        if (st231Array == null || st231Array.length == 0) {
            return Collections.emptyList();
        }
        final List<String> result = new ArrayList<String>(st231Array.length);
        for (ST st : st231Array) {
            addIfPresent(result, getValue(st));
        }
        return result;
    }

    @Override
    public Integer convertNM(NM nm231) {
        if (nm231 == null || StringUtils.isBlank(getValue(nm231))) {
            return null;
        }
        return Integer.valueOf(getValue(nm231));
    }

    @Override
    public DLNDriverSLicenseNumber createDLN(DLN dln231) {
        if (dln231 == null) {
            return null;
        }
        final DLNDriverSLicenseNumber dln = new DLNDriverSLicenseNumber();
        dln.setLicenseNumber(getValue(dln231.getDriverSLicenseNumber()));
        dln.setIssuingStateProvinceCountry(getValue(dln231.getIssuingStateProvinceCountry()));
        try {
            dln.setExpirationDate(convertDtToDate(dln231.getExpirationDate()));
        } catch (DataTypeException e) {
            logger.info("Can parse date for DT " + dln231.getExpirationDate().toString());
        }
        return dln;
    }

    private <T> void addIfPresent(List<T> list, T t) {
        if (list != null && t != null) {
            list.add(t);
        }
    }

}
