package com.scnsoft.eldermark.util.cda;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.entity.ConceptDescriptor;
import com.scnsoft.eldermark.entity.Name;
import com.scnsoft.eldermark.entity.basic.Address;
import com.scnsoft.eldermark.entity.basic.Telecom;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CdaConstants;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.xds.datatype.CECodedElement;
import com.scnsoft.eldermark.entity.xds.datatype.XPNPersonName;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.mdht.uml.cda.Observation;
import org.eclipse.mdht.uml.hl7.datatypes.*;
import org.eclipse.mdht.uml.hl7.vocab.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public class CcdUtils {
    private static final Logger logger = LoggerFactory.getLogger(CcdUtils.class);

    private static final String TS_PATTERN = "[0-9]{1,8}|([0-9]{9,14}|[0-9]{14,14}\\.[0-9]+)([+\\-][0-9]{1,4})?";

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
        ccdId.setRoot(CdaConstants.ID_ROOT);
        ccdId.setExtension(id);
        if (StringUtils.isNotEmpty(assigningAuthorityName)) {
            ccdId.setAssigningAuthorityName(assigningAuthorityName);
        }
        return ccdId;
    }

    public static II getNullId() {
        return getNullId(NullFlavor.NI);
    }

    public static II getNullId(NullFlavor nf) {
        II nullId = DatatypesFactory.eINSTANCE.createII();
        nullId.setNullFlavor(nf);
        return nullId;
    }

    public static II getNpiId(String npi) {
        II ccdId = DatatypesFactory.eINSTANCE.createII();
        ccdId.setRoot(CdaConstants.US_NPI_ROOT);
        ccdId.setExtension(npi);
        return ccdId;
    }

    public static II getExtPharmacyId(String extPharmacyId) {
        II ccdId = DatatypesFactory.eINSTANCE.createII();
        ccdId.setRoot(CdaConstants.EXCHANGE_PRESCRIBER_ORGANIZATION_EXT_PHARMACY_ID);
        ccdId.setExtension(extPharmacyId);
        return ccdId;
    }

    public static II getMedicationPrescriptionNumber(String id) {
        II ccdId = DatatypesFactory.eINSTANCE.createII();
        ccdId.setRoot(CdaConstants.EXCHANGE_MEDICATION_PRESCRIPTION_NUMBER);
        ccdId.setExtension(id);
        return ccdId;
    }

    public static II getHpClaimBillingProviderRefId(String hpClaimBillingProviderRefId) {
        II ccdId = DatatypesFactory.eINSTANCE.createII();
        ccdId.setRoot(CdaConstants.EXCHANGE_HP_CLAIM_BILLING_PROVIDER_REF_ID);
        ccdId.setExtension(hpClaimBillingProviderRefId);
        ccdId.setAssigningAuthorityName("Health Partners Claim Billing Provider Reference");
        return ccdId;
    }

    public static void addEldermarkLegacyId(EList<II> ids, String id) {
        if (StringUtils.isNotEmpty(id)) {
            ids.add(getEldermarkLegacyId(id));
        }
    }

    public static void addMedicationPrescriptionNumber(EList<II> ids, String id) {
        if (StringUtils.isNotEmpty(id)) {
            ids.add(getMedicationPrescriptionNumber(id));
        }
    }

    public static void addHpClaimBillingProviderRefId(EList<II> ids, String hpClaimBillingProviderRefId) {
        if (StringUtils.isNotEmpty(hpClaimBillingProviderRefId)) {
            ids.add(getHpClaimBillingProviderRefId(hpClaimBillingProviderRefId));
        }
    }

    public static void addEldermarkLegacyId(EList<II> ids, Long id) {
        if (id != null) {
            addEldermarkLegacyId(ids, id.toString());
        }
    }

    public static II getEldermarkLegacyId(Long id) {
        return id == null ? getNullId() : getEldermarkLegacyId(id.toString());
    }

    public static II getEldermarkLegacyId(String id) {
        if (StringUtils.isEmpty(id)) {
            return getNullId();
        }
        II ccdId = DatatypesFactory.eINSTANCE.createII();
        ccdId.setRoot(CdaConstants.EXCHANGE_ELDERMARK_LEGACY_ID);
        ccdId.setExtension(id);
        return ccdId;
    }

    public static IVL_TS getNullEffectiveTime() {
        IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
        IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
        low.setNullFlavor(NullFlavor.NI);
        high.setNullFlavor(NullFlavor.NI);
        IVL_TS effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();
        effectiveTime.setLow(low);
        effectiveTime.setHigh(high);
        return effectiveTime;
    }

    public static TEL getNullTelecom() {
        TEL nullTel = DatatypesFactory.eINSTANCE.createTEL();
        nullTel.setNullFlavor(NullFlavor.NI);
        return nullTel;
    }

    public static AD getNullAddress() {
        AD nullAd = DatatypesFactory.eINSTANCE.createAD();

        ADXP nullStreet = DatatypesFactory.eINSTANCE.createADXP();
        ADXP nullCity = DatatypesFactory.eINSTANCE.createADXP();
        ADXP nullState = DatatypesFactory.eINSTANCE.createADXP();
        ADXP nullCountry = DatatypesFactory.eINSTANCE.createADXP();

        nullStreet.setNullFlavor(NullFlavor.NI);
        nullCity.setNullFlavor(NullFlavor.NI);
        nullState.setNullFlavor(NullFlavor.NI);
        nullCountry.setNullFlavor(NullFlavor.NI);

        nullAd.getStreetAddressLines().add(nullStreet);
        nullAd.getCities().add(nullCity);
        nullAd.getStates().add(nullState);
        nullAd.getCountries().add(nullCountry);

        return nullAd;
    }

    public static PN getNullName() {
        PN nullPn = DatatypesFactory.eINSTANCE.createPN();
        nullPn.setNullFlavor(NullFlavor.NI);
        return nullPn;
    }

    public static IVL_TS convertEffectiveTime(Date timeLow, Date timeHigh) {
        return convertEffectiveTime(timeLow, timeHigh, false, false);
    }

    public static IVL_TS convertEffectiveTime(Date timeLow, Date timeHigh, boolean lowRequired, boolean highRequired) {
        IVL_TS effectiveTime = null;

        if (timeLow != null || timeHigh != null) {
            effectiveTime = DatatypesFactory.eINSTANCE.createIVL_TS();

            if (timeLow != null) {
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                low.setValue(CcdUtils.formatSimpleDate(timeLow));
                effectiveTime.setLow(low);
            } else if (lowRequired) {
                IVXB_TS low = DatatypesFactory.eINSTANCE.createIVXB_TS();
                low.setNullFlavor(NullFlavor.NI);
                effectiveTime.setLow(low);
            }

            if (timeHigh != null) {
                IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
                high.setValue(CcdUtils.formatSimpleDate(timeHigh));
                effectiveTime.setHigh(high);
            } else if (highRequired) {
                IVXB_TS high = DatatypesFactory.eINSTANCE.createIVXB_TS();
                high.setNullFlavor(NullFlavor.NI);
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
        return ad != null && ccdAddresses.add(ad);
    }

    public static void addConvertedAddresses(Collection<? extends Address> addresses, EList<AD> adList, boolean required) {
        boolean added = false;
        if (CollectionUtils.isNotEmpty(addresses)) {
            for (var address : addresses) {
                added = added || CcdUtils.addConvertedAddress(adList, address);
            }
        }
        if (required && !added) {
            adList.add(CcdUtils.getNullAddress());
        }
    }

    public static boolean addConvertedName(List<PN> ccdNames, Name name) {
        if (name == null) {
            return false;
        }
        checkNotNull(ccdNames);

        PN pn = convertName(name);
        return pn != null && ccdNames.add(pn);
    }

    public static void addConvertedNames(Collection<? extends Name> names, EList<PN> pnList, boolean required) {
        boolean added = false;
        if (CollectionUtils.isNotEmpty(names)) {
            for (var name : names) {
                added = added || CcdUtils.addConvertedName(pnList, name);
            }
        }
        if (required && !added) {
            pnList.add(CcdUtils.getNullName());
        }
    }

    public static void addConvertedTelecoms(Collection<? extends Telecom> telecoms, EList<TEL> telList, boolean required) {
        boolean added = false;
        if (CollectionUtils.isNotEmpty(telecoms)) {
            for (var telecom : telecoms) {
                added = added || CcdUtils.addConvertedTelecom(telList, telecom);
            }
        }
        if (required && !added) {
            telList.add(CcdUtils.getNullTelecom());
        }
    }

    public static boolean addConvertedTelecom(List<TEL> ccdTelecoms, Telecom telecom) {
        if (telecom == null) {
            return false;
        }
        checkNotNull(ccdTelecoms);

        TEL tel = convertTelecom(telecom);
        return tel != null && ccdTelecoms.add(tel);
    }

    public static AD convertAddress(Address address) {
        AD ad = DatatypesFactory.eINSTANCE.createAD();
        boolean isSet = false;

        if (!StringUtils.isBlank(address.getPostalAddressUse())) {
            try {
                ad.getUses().add(PostalAddressUse.valueOf(address.getPostalAddressUse()));
            } catch (IllegalArgumentException e) {
            }
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

    public static CE createCE(ConceptDescriptor code) {
        return code == null ?
                createCE(null, null, null, null) :
                createCE(code.getCode(), code.getDisplayName(), code.getCodeSystem(), code.getCodeSystemName());
    }

    public static CE createCE(String code, String displayName, CodeSystem codeSystem) {
        return createCE(code, displayName, codeSystem.getOid(), codeSystem.getDisplayName());
    }

    public static CE createCE(String code, String displayName, String codeSystem, String codeSystemName) {
        CE ccdCode = DatatypesFactory.eINSTANCE.createCE();
        populateCode(ccdCode, code, displayName, codeSystem, codeSystemName);
        return ccdCode;
    }

    public static CE createCE(ConceptDescriptor code, String alternativeCodeSystem) {
        //should we instead of just logging add code as translation if codeSystems don't match?
        checkCode(code, alternativeCodeSystem);
        return createCE(code);
    }

    public static CE createCEWithDefaultDisplayName(ConceptDescriptor code, String defaultDisplayName) {
        CE ce = createCE(code);
        fillDefaultDisplayName(ce, defaultDisplayName);
        return ce;
    }

    public static CE createCEWithDefaultDisplayName(ConceptDescriptor code, String defaultDisplayName, String alternativeCodeSystem) {
        CE ce = createCE(code, alternativeCodeSystem);
        fillDefaultDisplayName(ce, defaultDisplayName);
        return ce;
    }

    /**
     * Creates CE from code. If code system doesn't matches required - creates code with translation.
     * If alsoPopulateRootCode is true - root code will also contain same code, codesystem, etc. as translation
     * along with OTH nullflavor.
     * <p>
     * alsoPopulateRootCode is needed to maintain backward compatibility with Consana - they fetch some codes
     * 'as is' without checking translations. Translations were not implemented when this integration was implemented.
     *
     * @param code
     * @param requiredCodeSystem
     * @param alsoPopulateRootCode
     * @return
     */
    public static CE createCEOrTranslation(ConceptDescriptor code, String requiredCodeSystem, boolean alsoPopulateRootCode) {
        return createCodeOrTranslation(code, requiredCodeSystem, alsoPopulateRootCode,
                CcdUtils::createCE, DatatypesFactory.eINSTANCE::createCE);
    }

    public static CE createCEFromValueSetOrTranslation(CcdCode code, ValueSetEnum requiredValueSet, boolean alsoPopulateRootCode) {
        return createFromValueSetOrTranslation(code, requiredValueSet, alsoPopulateRootCode,
                CcdUtils::createCE, DatatypesFactory.eINSTANCE::createCE);
    }

    /**
     * Return 'statusCode' element with @code = {@code cd.getCode()} or @nullFlavor
     * = "NI"<br/>
     * Attributes @codeSystem, @codeSystemName, @displayName are not allowed to
     * appear in 'statusCode'
     */
    public static CS createCS(ConceptDescriptor cd) {
        return cd == null ? createCS((String) null) : createCS(cd.getCode());
    }

    /**
     * Return 'statusCode' element with @code = {@code code} or @nullFlavor =
     * "NI"<br/>
     * Attributes @codeSystem, @codeSystemName, @displayName are not allowed to
     * appear in 'statusCode'
     */
    public static CS createCS(String code) {
        CS ccdCode = DatatypesFactory.eINSTANCE.createCS();
        populateCode(ccdCode, code, null, null, null);
        return ccdCode;
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
        populateCode(ccdCode, code, displayName, codeSystem, codeSystemName);
        return ccdCode;
    }

    public static CD createCD(ConceptDescriptor code, String alternativeCodeSystem) {
        //should we instead of just logging add code as translation if codeSystems don't match?
        checkCode(code, alternativeCodeSystem);
        return createCD(code);
    }

    public static CD createCDWithDefaultDisplayName(ConceptDescriptor code, String defaultDisplayName, String alternativeCodeSystem) {
        var cd = createCD(code, alternativeCodeSystem);
        fillDefaultDisplayName(cd, defaultDisplayName);
        return cd;
    }

    public static CD createCDFromValueSetOrTranslation(CcdCode code, ValueSetEnum requiredValueSet, boolean alsoPopulateRootCode) {
        return createFromValueSetOrTranslation(code, requiredValueSet, alsoPopulateRootCode,
                CcdUtils::createCD, DatatypesFactory.eINSTANCE::createCD);
    }

    public static CD createCDFromValueSetOrTranslationDefaultDisplayName(CcdCode code, ValueSetEnum requiredValueSet, String displayName) {
        //root always populated
        var rootCode = createCDWithDefaultDisplayName(code, displayName);
        if (code != null && !isFromValueSet(code, requiredValueSet)) {
            rootCode.setNullFlavor(NullFlavor.OTH);
            var cd = createCD(code);
            rootCode.getTranslations().add(cd);
        }

        return rootCode;
    }

    public static CD createCDWithDefaultDisplayName(ConceptDescriptor code, String defaultDisplayName) {
        var cd = createCD(code);
        fillDefaultDisplayName(cd, defaultDisplayName);
        return cd;
    }

    /**
     * Creates CD from code. If code system doesn't matches required - creates code with translation.
     * If alsoPopulateRootCode is true - root code will also contain same code, codesystem, etc. as translation
     * along with OTH nullflavor.
     * <p>
     * alsoPopulateRootCode is needed to maintain backward compatibility with Consana - they fetch some codes
     * 'as is' without checking translations. Translations were not implemented when this integration was implemented.
     *
     * @param code
     * @param requiredCodeSystem
     * @param alsoPopulateRootCode
     * @return
     */
    public static CD createCDOrTranslation(ConceptDescriptor code, String requiredCodeSystem, boolean alsoPopulateRootCode) {
        return createCodeOrTranslation(code, requiredCodeSystem, alsoPopulateRootCode,
                CcdUtils::createCD, DatatypesFactory.eINSTANCE::createCD);
    }

    private static <T extends CD> T createCodeOrTranslation(ConceptDescriptor code, String requiredCodeSystem, boolean alsoPopulateRootCode,
                                                            Function<ConceptDescriptor, T> codeCreator,
                                                            Supplier<T> emptyCodeCreator) {
        var cd = codeCreator.apply(code);

        if (code != null && !requiredCodeSystem.equals(cd.getCodeSystem())) {
            var result = alsoPopulateRootCode ? codeCreator.apply(code) : emptyCodeCreator.get();
            result.setNullFlavor(NullFlavor.OTH);
            result.getTranslations().add(cd);
            return result;
        }
        return cd;
    }

    public static <T extends CD> T createFromValueSetOrTranslation(CcdCode code, ValueSetEnum requiredValueSet, boolean alsoPopulateRootCode,
                                                                   Function<ConceptDescriptor, T> codeCreator,
                                                                   Supplier<T> emptyCodeCreator) {
        var cd = codeCreator.apply(code);

        if (code != null && !isFromValueSet(code, requiredValueSet)) {
            var result = alsoPopulateRootCode ? codeCreator.apply(code) : emptyCodeCreator.get();
            result.setNullFlavor(NullFlavor.OTH);
            result.getTranslations().add(cd);
            return result;
        }
        return cd;
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

    public static CV createCV(String code, String displayName, CodeSystem codeSystem) {
        return createCV(code, displayName, codeSystem.getOid(), codeSystem.getDisplayName());
    }

    public static CV createCV(String code, String displayName, String codeSystem, String codeSystemName) {
        CV ccdCode = DatatypesFactory.eINSTANCE.createCV();
        populateCode(ccdCode, code, displayName, codeSystem, codeSystemName);
        return ccdCode;
    }

    public static void checkCode(ConceptDescriptor code, String alternativeCodeSystem) {
        if (code != null && alternativeCodeSystem != null) {
            if (!alternativeCodeSystem.equals(code.getCodeSystem())) {
                String msg = String.format(
                        "CCD code system doesn't match with required by specification: actual='%s', expected ='%s'. ",
                        code.getCodeSystem(), alternativeCodeSystem);
                msg += Thread.currentThread().getStackTrace()[3];
                logger.error(msg);
            }
        }
    }

    private static void fillDefaultDisplayName(CD cd, String defaultDisplayName) {
        var displayName = StringUtils.defaultIfEmpty(cd.getDisplayName(), defaultDisplayName);
        if (StringUtils.isNotEmpty(displayName)) {
            cd.setDisplayName(displayName);
        }
    }

    public static ST createST(String text) {
        ST ccdCode = DatatypesFactory.eINSTANCE.createST();
        ccdCode.addText(text);
        return ccdCode;
    }

    private static void populateCode(CD ccdCode, String code, String displayName, String codeSystem, String codeSystemName) {
        if (code != null) {
            if (codeSystem != null && CodeSystem.RETIRED_CODE_SYSTEM_OIDS.containsKey(codeSystem)) {
                var updatedCodeSystem = CodeSystem.RETIRED_CODE_SYSTEM_OIDS.get(codeSystem);

                codeSystem = updatedCodeSystem.getOid();
                codeSystemName = updatedCodeSystem.getDisplayName();
            }

            ccdCode.setCode(code);
            ccdCode.setCodeSystem(codeSystem);
            if (StringUtils.isNotBlank(codeSystemName)) {
                ccdCode.setCodeSystemName(codeSystemName);
            }
            if (StringUtils.isNotBlank(displayName)) {
                ccdCode.setDisplayName(displayName);
            }
        } else {
            ccdCode.setNullFlavor(NullFlavor.NI);
        }
    }

    public static String formatSimpleDate(Date date) {
        return formatDate("yyyyMMdd", date);
    }

    public static String formatSimpleLocalDate(LocalDate date) {
        return localDateFormat("yyyyMMdd", date);
    }

    public static String formatTableDate(Date date) {
        return formatDate("MM/dd/yyyy", date);
    }

    public static String formatHypenDate(Date date) {
        return formatDate("MM-dd-yyyy", date);
    }

    public static String formatDate(Date date) {
        return formatDate("yyyyMMddhhmmssZ", date);
    }

    public static String formatTableLocalDate(LocalDate date) {
        return localDateFormat("MM/dd/yyyy", date);
    }

    public static String formatHypenLocalDate(LocalDate date) {
        return localDateFormat("MM-dd-yyyy", date);
    }

    public static String formatDate(String pattern, Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(pattern);
        return timeFormat.format(date);
    }

    public static String localDateFormat(String pattern, LocalDate date) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormat.format(date);
    }

    public static String localDateTimeFormat(String pattern, LocalDateTime date) {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern(pattern);
        return dateTimeFormat.format(date);
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
            addReferenceToSectionText(refId, text, block);
        } else {
            addEmptyCellToSectionText(block);
        }
        block.append("</td>");
    }

    public static void addReferenceToSectionText(String refId, String text, StringBuilder block) {
        if (block.indexOf(refId) == -1)
            block.append(String.format("<content ID=\"%s\">%s</content>", refId, StringEscapeUtils.escapeHtml(text)));
        else
            block.append(text);
    }

    public static void addDateRangeCell(Date start, Date end, StringBuilder block) {
        block.append("<td>");
        if (start == null && end == null) {
            addEmptyCellToSectionText(block);
        } else {
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
            block.append(StringEscapeUtils.escapeHtml(code.getDisplayName()));
        } else {
            CcdUtils.addEmptyCellToSectionText(block);
        }
        block.append("</td>");
    }

    public static void addCellToSectionText(String text, StringBuilder block) {
        block.append("<td>");
        if (text != null) {
            block.append(StringEscapeUtils.escapeHtml(text));
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

    public static String displayName(ConceptDescriptor ccdCode) {
        return Optional.ofNullable(ccdCode).map(ConceptDescriptor::getDisplayName).orElse(null);
    }

    public static void addContent(StringBuilder sectionText, CharSequence content, ContentTag tag) {
        if (StringUtils.isNotEmpty(content)) {
            sectionText.append(tag.open);
            sectionText.append(content);
            sectionText.append(tag.close);
        }
    }

    public static Optional<EldermarkMedicationRecurrence> parseEldermarkRecurrence(String recurrence) {
        try {
            return Optional.of(new EldermarkMedicationRecurrence(recurrence));
        } catch (EldermarkRecurrenceParseException e) {
            logger.info("Recurrence [{}] is not recognized as eldermark recurrence", recurrence);
            return Optional.empty();
        }
    }

    public enum ContentTag {
        TBODY,
        PARAGRAPH;

        ContentTag() {
            this.open = "<" + this.name().toLowerCase() + ">";
            this.close = "</" + this.name().toLowerCase() + ">";
        }

        private final String open;
        private final String close;
    }

    public static String buildFullName(XPNPersonName xpnPersonName) {
        return Stream.of(xpnPersonName.getFirstName(), xpnPersonName.getLastName())
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }

    public static String getIdentifier(CECodedElement ceCodedElement) {
        return Optional.ofNullable(ceCodedElement).map(CECodedElement::getIdentifier).orElse(null);
    }

    public static boolean isTS(String str) {
        return str != null && str.matches(TS_PATTERN);
    }

    public static boolean isFromValueSet(CcdCode ccdCode, ValueSetEnum valueSet) {
        if (ccdCode == null) {
            return false;
        }

        //if code is specified inside ValueSetEnum
        if (valueSet.isFromValueSet(ccdCode.getCode(), ccdCode.getCodeSystem())) {
            return true;
        }

        //if there exists relation between code and ValueSet entity
        if (CollectionUtils.emptyIfNull(ccdCode.getValueSets()).stream()
                .anyMatch(v -> valueSet.getOid().equals(v.getOid()))) {
            return true;
        }

        //if valueSet is specified in CcdCode field
        return valueSet.getOid().equals(ccdCode.getValueSet());
    }
}
