package com.scnsoft.eldermark.services.cda.util;

import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.connect.ConnectUtil;
import com.scnsoft.eldermark.shared.ccd.CcdCodeDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CcdUtils {
    private static final Logger logger = LoggerFactory.getLogger(CcdUtils.class);

    public static String ID_ROOT = ConnectUtil.EXCHANGE_HCID;

    public static II getId(Long id) {
        return (id == null) ? getNullId() : getId(id.toString());
    }

    public static II getId(String id) {
        return getId(id, null);
    }

    public static II getConsanaId(String id) {
        return getId(id, "XCL");
    }

    public static void addConsanaId(EList<II> ids, String id) {
        if (StringUtils.isNotEmpty(id)) {
            ids.add(getConsanaId(id));
        }
    }

    public static II getId(String id, String assigningAuthorityName) {
        if (id == null) {
            return getNullId();
        }

        II ccdId = DatatypesFactory.eINSTANCE.createII();
        ccdId.setRoot(ID_ROOT);
        ccdId.setExtension(id);
        if (StringUtils.isNotEmpty(assigningAuthorityName)) {
            ccdId.setAssigningAuthorityName(assigningAuthorityName);
        }
        return ccdId;
    }

    public static II getNullId() {
        II nullId = DatatypesFactory.eINSTANCE.createII();
        nullId.setNullFlavor(NullFlavor.NI);
        return nullId;
    }

    public static IVL_TS getNullEffectiveTime() {
        IVL_TS nullTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        nullTime.setNullFlavor(NullFlavor.NI);
        return nullTime;
    }

    public static TEL getNullTelecom() {
        TEL nullTel = DatatypesFactory.eINSTANCE.createTEL();
        nullTel.setNullFlavor(NullFlavor.NI);
        return nullTel;
    }

    public static AD getNullAddress() {
        AD nullAd = DatatypesFactory.eINSTANCE.createAD();
        nullAd.setNullFlavor(NullFlavor.NI);
        return nullAd;
    }

    public static PN getNullName() {
        PN nullPn = DatatypesFactory.eINSTANCE.createPN();
        nullPn.setNullFlavor(NullFlavor.NI);
        return nullPn;
    }

    public static IVL_TS convertEffectiveTime(Date timeLow, Date timeHigh) {
        IVL_TS effectiveTime = null;

        if (timeLow != null || timeHigh != null) {
            effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
            if (timeLow != null) {
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                low.setValue(CcdUtils.formatSimpleDate(timeLow));
                effectiveTime.setLow(low);
            }
            if (timeHigh != null) {
                IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
                high.setValue(CcdUtils.formatSimpleDate(timeHigh));
                effectiveTime.setHigh(high);
            }
        }

        return effectiveTime;
    }

    public static IVL_TS convertEffectiveTime(Date time) {
        IVL_TS tsTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        if (time != null) {
            tsTime.setValue(formatSimpleDate(time));
        } else {
            tsTime.setNullFlavor(NullFlavor.NI);
        }
        return tsTime;
    }

    public static IVL_TS createCenterTime(Date date) {
        if (date == null) {
            return CcdUtils.getNullEffectiveTime();
        }
        IVL_TS tsTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        TS timeValue = DatatypesFactory.eINSTANCE.createTS();
        timeValue.setValue(formatSimpleDate(date));
        tsTime.setCenter(timeValue);
        return tsTime;
    }

    public static void setInterpretationCodes(Observation observation, List<CcdCode> interpretationCodes) {
        if (interpretationCodes != null) {
            for (CcdCode interpretationCode : interpretationCodes) {
                observation.getInterpretationCodes().add(CcdUtils.createCE(interpretationCode));
            }
        }
    }

    public static boolean addConvertedAddress(List<AD> ccdAddresses, Address address) {
        if (address == null) {
            return false;
        }
        checkNotNull(ccdAddresses);

        AD ad = convertAddress(address);
        return (ad != null) ? ccdAddresses.add(ad) : false;
    }

    public static boolean addConvertedName(List<PN> ccdNames, Name name) {
        if (name == null) {
            return false;
        }
        checkNotNull(ccdNames);

        PN pn = convertName(name);
        return (pn != null) ? ccdNames.add(pn) : false;
    }

    public static boolean addConvertedTelecom(List<TEL> ccdTelecoms, Telecom telecom) {
        if (telecom == null) {
            return false;
        }
        checkNotNull(ccdTelecoms);

        TEL tel = convertTelecom(telecom);
        return (tel != null) ? ccdTelecoms.add(tel) : false;
    }

    public static AD convertAddress(Address address) {
        AD ad = DatatypesFactory.eINSTANCE.createAD();
        boolean isSet = false;

        if (!StringUtils.isBlank(address.getPostalAddressUse())) {
            try {
                ad.getUses().add(PostalAddressUse.valueOf(address.getPostalAddressUse()));
            } catch (IllegalArgumentException e) { }
        }

        if (!StringUtils.isBlank(address.getStreetAddress())) {
            ad.addStreetAddressLine(address.getStreetAddress());
            isSet = true;
        } else if (address.getStreetAddress() != null) {
            ADXP nullStreet = DatatypesFactory.eINSTANCE.createADXP();
            nullStreet.setNullFlavor(NullFlavor.NI);
            ad.getStreetAddressLines().add(nullStreet);
        }

        if (!StringUtils.isBlank(address.getCity())) {
            ad.addCity(address.getCity());
            isSet = true;
        } else {
            ADXP nullCity = DatatypesFactory.eINSTANCE.createADXP();
            nullCity.setNullFlavor(NullFlavor.NI);
            ad.getCities().add(nullCity);
        }

        if (!StringUtils.isBlank(address.getState())) {
            ad.addState(address.getState());
            isSet = true;
        }

        if (!StringUtils.isBlank(address.getCountry())) {
            ad.addCountry(address.getCountry());
            isSet = true;
        }

        if (!StringUtils.isBlank(address.getPostalCode())) {
            ad.addPostalCode(address.getPostalCode());
            isSet = true;
        }

        return isSet ? ad : null;
    }

    public static TEL convertTelecom(Telecom telecom) {
        TEL tel = DatatypesFactory.eINSTANCE.createTEL();
        boolean isSet = false;

        if (!StringUtils.isBlank(telecom.getUseCode()) && !"EMAIL".equals(telecom.getUseCode())) {
            tel.getUses().add(TelecommunicationAddressUse.valueOf(telecom.getUseCode()));
        }
        if (!StringUtils.isBlank(telecom.getValue())) {
            tel.setValue(telecom.getValue());
            isSet = true;
        }

        return isSet ? tel : null;
    }

    public static PN convertName(Name name) {
        PN pn = DatatypesFactory.eINSTANCE.createPN();
        boolean isSet = false;

        if (!StringUtils.isBlank(name.getNameUse())) {
            pn.getUses().add(EntityNameUse.valueOf(name.getNameUse()));
        }
        if (!StringUtils.isBlank(name.getFamily())) {
            if (!StringUtils.isBlank(name.getFamilyQualifier())) {
                ENXP enxp = DatatypesFactory.eINSTANCE.createENXP();
                enxp.getQualifiers().add(EntityNamePartQualifier.valueOf(name.getFamilyQualifier()));
                enxp.setPartType(EntityNamePartType.FAM);
                enxp.addText(name.getFamily());
                pn.getFamilies().add(enxp);
            } else {
                pn.addFamily(name.getFamily());
            }
            isSet = true;
        } else {
            ENXP enxp = DatatypesFactory.eINSTANCE.createENXP();
            enxp.setNullFlavor(NullFlavor.NI);
            pn.getFamilies().add(enxp);
        }
        if (!StringUtils.isBlank(name.getGiven())) {
            if (!StringUtils.isBlank(name.getGivenQualifier())) {
                ENXP enxp = DatatypesFactory.eINSTANCE.createENXP();
                enxp.getQualifiers().add(EntityNamePartQualifier.valueOf(name.getGivenQualifier()));
                enxp.setPartType(EntityNamePartType.GIV);
                enxp.addText(name.getGiven());
                pn.getGivens().add(enxp);
            } else {
                pn.addGiven(name.getGiven());
            }
            isSet = true;
        } else {
            ENXP enxp = DatatypesFactory.eINSTANCE.createENXP();
            enxp.setNullFlavor(NullFlavor.NI);
            pn.getGivens().add(enxp);
        }
        if (!StringUtils.isBlank(name.getPreferredName())) {
            ENXP enxp = DatatypesFactory.eINSTANCE.createENXP();
            enxp.getQualifiers().add(EntityNamePartQualifier.CL);
            enxp.setPartType(EntityNamePartType.GIV);
            enxp.addText(name.getPreferredName());
            pn.getGivens().add(enxp);
            isSet = true;
        }
        if (!StringUtils.isBlank(name.getDegree())) {
            ENXP enxp = DatatypesFactory.eINSTANCE.createENXP();
            enxp.getQualifiers().add(EntityNamePartQualifier.AC);
            enxp.setPartType(EntityNamePartType.GIV);
            enxp.addText(name.getDegree());
            pn.getGivens().add(enxp);
            isSet = true;
        }
        if (!StringUtils.isBlank(name.getMiddle())) {
            if (!StringUtils.isBlank(name.getMiddleQualifier())) {
                ENXP enxp = DatatypesFactory.eINSTANCE.createENXP();
                enxp.getQualifiers().add(EntityNamePartQualifier.valueOf(name.getMiddleQualifier()));
                enxp.setPartType(EntityNamePartType.GIV);
                enxp.addText(name.getMiddle());
                pn.getGivens().add(enxp);
            } else {
                pn.addGiven(name.getMiddle());
            }
            isSet = true;
        }
        if (!StringUtils.isBlank(name.getPrefix())) {
            if (!StringUtils.isBlank(name.getPrefixQualifier())) {
                ENXP enxp = DatatypesFactory.eINSTANCE.createENXP();
                enxp.getQualifiers().add(EntityNamePartQualifier.valueOf(name.getPrefixQualifier()));
                enxp.setPartType(EntityNamePartType.PFX);
                enxp.addText(name.getPrefix());
                pn.getPrefixes().add(enxp);
            } else {
                pn.addPrefix(name.getPrefix());
            }
            isSet = true;
        }
        if (!StringUtils.isBlank(name.getSuffix())) {
            if (!StringUtils.isBlank(name.getSuffixQualifier())) {
                ENXP enxp = DatatypesFactory.eINSTANCE.createENXP();
                enxp.getQualifiers().add(EntityNamePartQualifier.valueOf(name.getSuffixQualifier()));
                enxp.setPartType(EntityNamePartType.SFX);
                enxp.addText(name.getSuffix());
                pn.getSuffixes().add(enxp);
            } else {
                pn.addSuffix(name.getSuffix());
            }
            isSet = true;
        }
        if (!isSet && !StringUtils.isBlank(name.getFullName())) {
            pn.getFamilies().clear();
            pn.getGivens().clear();
            pn.addText(name.getFullName());
            isSet = true;
        }

        return isSet ? pn : null;
    }

    public static CE createCE(String code, String codeSystem) {
        return createCE(code, codeSystem, null, null);
    }

    public static CE createCE(String code, String codeSystem, String codeSystemName) {
        return createCE(code, codeSystem, codeSystemName, null);
    }

    public static CE createCE(String code, String displayName, CodeSystem codeSystem) {
        return createCE(code, codeSystem.getOid(), codeSystem.getDisplayName(), displayName);
    }

    public static CE createCE(String code, String codeSystem, String codeSystemName, String displayName) {
        CE ccdCode = DatatypesFactory.eINSTANCE.createCE();
        if (code != null) {
            ccdCode.setCodeSystem(codeSystem);
            if (StringUtils.isNotBlank(codeSystemName)) {
                ccdCode.setCodeSystemName(codeSystemName);
            }
            if (StringUtils.isNotBlank(displayName)) {
                ccdCode.setDisplayName(displayName);
            }

            ccdCode.setCode(code);
        } else {
            ccdCode.setNullFlavor(NullFlavor.NI);
        }
        return ccdCode;
    }

    /**
     * Return 'statusCode' element with @code = {@code cd.getCode()} or @nullFlavor = "NI"<br/>
     * Attributes @codeSystem, @codeSystemName, @displayName are not allowed to appear in 'statusCode'
     */
    public static CS createCS(ConceptDescriptor cd) {
        CS ccdCode = DatatypesFactory.eINSTANCE.createCS();
        if (cd == null || cd.getCode() == null) {
            ccdCode.setNullFlavor(NullFlavor.NI);
        } else {
            ccdCode.setCode(cd.getCode());
        }
        return ccdCode;
    }

    /**
     * Return 'statusCode' element with @code = {@code code} or @nullFlavor = "NI"<br/>
     * Attributes @codeSystem, @codeSystemName, @displayName are not allowed to appear in 'statusCode'
     */
    public static CS createCS(String code) {
        CS ccdCode = DatatypesFactory.eINSTANCE.createCS();
        if (code != null) {
            ccdCode.setCode(code);
        } else {
            ccdCode.setNullFlavor(NullFlavor.NI);
        }
        return ccdCode;
    }

    /**
     * @deprecated
     */
    public static CD createCD(String code, String codeSystem) {
        return createCD(code, null, codeSystem, null);
    }

    public static CD createCD(ConceptDescriptor cd) {
        if (cd == null) {
            return createCD(null, null, null, null);
        } else {
            return createCD(cd.getCode(), cd.getDisplayName(), cd.getCodeSystem(), cd.getCodeSystemName());
        }
    }

    public static CD createCD(String code, String displayName, CodeSystem codeSystem) {
        return createCD(code, displayName, codeSystem.getOid(), codeSystem.getDisplayName());
    }

    public static CD createCD(String code, String displayName, String codeSystem, String codeSystemName) {
        CD ccdCode = DatatypesFactory.eINSTANCE.createCD();
        if (code != null) {
            ccdCode.setCode(code);
            if (StringUtils.isNotBlank(codeSystemName)) {
                ccdCode.setDisplayName(displayName);
            }
            ccdCode.setCodeSystem(codeSystem);
            if (StringUtils.isNotBlank(codeSystemName)) {
                ccdCode.setCodeSystemName(codeSystemName);
            }
        } else {
            ccdCode.setNullFlavor(NullFlavor.NI);
        }
        return ccdCode;
    }

    /**
     * @deprecated
     */
    public static CD createCD(String code, String codeSystem, String codeSystemName) {
        return createCD(code, null, codeSystem, codeSystemName);
    }

    public static CD createCD(ConceptDescriptor code, String alternativeCodeSystem) {
        checkCode(code, alternativeCodeSystem);
        return createCD(code);
    }

    public static CD createCDWithDefaultDisplayName(ConceptDescriptor code, String defaultDisplayName, String alternativeCodeSystem) {
        checkCode(code, alternativeCodeSystem);
        return createCDWithDefaultDisplayName(code, defaultDisplayName);
    }

    public static CD createCDWithDefaultDisplayName(ConceptDescriptor code, String defaultDisplayName) {
        CD cd = createCD(code);
        cd.setDisplayName(StringUtils.defaultIfEmpty(cd.getDisplayName(), defaultDisplayName));
        return cd;
    }

    public static CV createCV(String code, String displayName, CodeSystem codeSystem) {
        return createCV(code, displayName, codeSystem.getOid(), codeSystem.getDisplayName());
    }

    public static CV createCV(String code, String displayName, String codeSystem, String codeSystemName) {
        CV ccdCode = DatatypesFactory.eINSTANCE.createCV();
        if (code != null) {
            ccdCode.setCode(code);
            if (StringUtils.isNotBlank(codeSystemName)) {
                ccdCode.setDisplayName(displayName);
            }
            ccdCode.setCodeSystem(codeSystem);
            if (StringUtils.isNotBlank(codeSystemName)) {
                ccdCode.setCodeSystemName(codeSystemName);
            }
        } else {
            ccdCode.setNullFlavor(NullFlavor.NI);
        }
        return ccdCode;
    }

    public static void checkCode(ConceptDescriptor code, String alternativeCodeSystem) {
        if(code != null && alternativeCodeSystem != null ) {
            if(!alternativeCodeSystem.equals(code.getCodeSystem())) {
                String msg = String.format("CCD code system doesn't match with required by specification: actual='%s', expected ='%s'. ", code.getCodeSystem(), alternativeCodeSystem);
                msg += Thread.currentThread().getStackTrace()[3];
                logger.error(msg);
            }
        }
    }

    public static CE createCE(ConceptDescriptor code) {
        CE ccdCode = DatatypesFactory.eINSTANCE.createCE();

        if (code == null) {
            ccdCode.setNullFlavor(NullFlavor.NI);
            return ccdCode;
        }

        ccdCode.setCode(code.getCode());
        ccdCode.setCodeSystem(code.getCodeSystem());
        if (StringUtils.isNotBlank(code.getCodeSystemName())) {
            ccdCode.setCodeSystemName(code.getCodeSystemName());
        }
        ccdCode.setDisplayName(code.getDisplayName());

        return ccdCode;
    }

    public static CE createCEWithDefaultDisplayName(ConceptDescriptor code, String defaultDisplayName) {
        CE ce = createCE(code);
        ce.setDisplayName(StringUtils.defaultIfEmpty(ce.getDisplayName(), defaultDisplayName));
        return ce;
    }

    public static CE createCE(ConceptDescriptor code, String alternativeCodeSystem) {
        checkCode(code, alternativeCodeSystem);
        return createCE(code);
    }

    public static CE createCEWithDefaultDisplayName(ConceptDescriptor code, String defaultDisplayName, String alternativeCodeSystem) {
        checkCode(code, alternativeCodeSystem);
        return createCEWithDefaultDisplayName(code, defaultDisplayName);
    }

    public static ST createST(String text) {
        ST ccdCode = DatatypesFactory.eINSTANCE.createST();
        ccdCode.addText(text);
        return ccdCode;
    }

    public static CD createOtherCode(String originalText, CodeSystem codeSystem) {
        CD otherCode = DatatypesFactory.eINSTANCE.createCD();
        otherCode.setNullFlavor(NullFlavor.OTH);
        otherCode.setCodeSystem(codeSystem.getOid());
        otherCode.setCodeSystemName(codeSystem.getDisplayName());
        otherCode.setOriginalText(createEntryText(originalText));
        return otherCode;
    }

    public static CD createNillCode() {
        CD nillCode = DatatypesFactory.eINSTANCE.createCD();
        nillCode.setNullFlavor(NullFlavor.NI);
        return nillCode;
    }

    public static String formatSimpleDate(Date date) {
        return formatDate("yyyyMMdd", date);
    }


    public static String formatTableDate(Date date) {
        return formatDate("MM/dd/yyyy", date);
    }


    public static String formatDate(Date date) {
        return formatDate("yyyyMMddhhmmssZ", date);
    }

    public static String formatDate(String pattern, Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(pattern);
        return timeFormat.format(date);
    }

    public static ED createReferenceEntryText(String refId) {
        ED originalText = DatatypesFactory.eINSTANCE.createED();
        TEL ref = DatatypesFactory.eINSTANCE.createTEL();
        ref.setValue("#" + refId);
        originalText.setReference(ref);
        return originalText;
    }

    public static ED createEntryText(String text) {
        ED originalText = DatatypesFactory.eINSTANCE.createED();
        originalText.addText(text);
        return originalText;
    }

    public static void addReferenceCell(String refId, String text, StringBuilder block) {
        block.append("<td>");
        if (text != null) {
            addReferenceToSectionText(refId,text,block);
        }
        else {
            addEmptyCellToSectionText(block);
        }
        block.append("</td>");
    }


    public static void addReferenceToSectionText(String refId, String text, StringBuilder block) {
        if (!block.toString().contains(refId))
            block.append(String.format("<content ID=\"%s\">%s</content>", refId, StringEscapeUtils.escapeHtml4(text)));
        else
            block.append(text);
    }

    public static void addDateRangeCell(Date start, Date end, StringBuilder block) {
        block.append("<td>");
        if (start == null && end == null ) {
            addEmptyCellToSectionText(block);
        }
        else {
            addDateRangeToSectionText(start, end, block);
        }
        block.append("</td>");
    }

    public static void addDateRangeToSectionText(Date start, Date end, StringBuilder block) {
        String timeLow = "?";
        String timeHigh = "?";
        if (start != null) {
            timeLow = formatTableDate(start);
        }
        if (end != null) {
            timeHigh = formatTableDate(end);
        }
        block.append(String.format("%s - %s", timeLow, timeHigh));
    }

    public static String addDateRangeToSectionText(Date start, Date end) {
        String timeLow = "?";
        String timeHigh = "?";
        if (start != null) {
            timeLow = formatTableDate(start);
        }
        if (end != null) {
            timeHigh = formatTableDate(end);
        }
        return String.format("%s - %s", timeLow, timeHigh);
    }

    public static void addReferenceCellToSectionText(String refId, String text, StringBuilder block) {
        block.append("<td>");
        if (text != null) {
            block.append(text);
        } else {
            addReferenceToSectionText(refId, text, block);
        }
        block.append("</td>");
    }

    public static void addCellToSectionText(CcdCode code, StringBuilder block) {
        block.append("<td>");
        if (code != null && StringUtils.isNotBlank(code.getDisplayName())) {
            block.append(StringEscapeUtils.escapeHtml4(code.getDisplayName()));
        } else {
            CcdUtils.addEmptyCellToSectionText(block);
        }
        block.append("</td>");
    }

    public static void addCellToSectionText(String text, StringBuilder block) {
        block.append("<td>");
        if (text != null) {
            block.append(StringEscapeUtils.escapeHtml4(text));
        } else {
            CcdUtils.addEmptyCellToSectionText(block);
        }
        block.append("</td>");
    }

    public static void addDateCell(Date date, StringBuilder block) {
        block.append("<td>");
        if (date != null) {
            block.append(formatTableDate(date));
        } else {
            addEmptyCellToSectionText(block);
        }
        block.append("</td>");
    }

    public static void addEmptyCell(StringBuilder block) {
        block.append("<td>--</td>");
    }

    public static void addEmptyCellToSectionText(StringBuilder block) {
        block.append("--");
    }

    public static ED createText(String simpleName, Long id) {
        String refId = simpleName + id;
        ED originalText = DatatypesFactory.eINSTANCE.createED();
        TEL ref = DatatypesFactory.eINSTANCE.createTEL();
        ref.setValue("#" + refId);
        originalText.setReference(ref);
        return originalText;
    }

    public static List<CcdCodeDto> transform(List<CcdCode> ccdCodes) {
        if (CollectionUtils.isNotEmpty(ccdCodes)) {
            final List<CcdCodeDto> result = new ArrayList<>(ccdCodes.size());
            for (CcdCode code : ccdCodes) {
                result.add(transform(code));
            }
            return result;
        }
        return Collections.emptyList();
    }

    public static CcdCodeDto transform(CcdCode code) {
        if (code == null) {
            return null;
        }
        final CcdCodeDto result = new CcdCodeDto();
        result.setId(code.getId());
        result.setCode(code.getCode());
        result.setCodeSystemName(code.getCodeSystemName());
        result.setDisplayName(code.getDisplayName());
        return result;
    }

}
