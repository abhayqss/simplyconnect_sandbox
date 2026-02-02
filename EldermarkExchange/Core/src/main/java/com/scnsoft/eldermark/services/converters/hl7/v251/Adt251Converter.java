package com.scnsoft.eldermark.services.converters.hl7.v251;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.AbstractMessage;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.ModelClassFactory;
import ca.uhn.hl7v2.util.ReflectionUtil;
import ca.uhn.hl7v2.util.Terser;
import com.neovisionaries.i18n.LanguageAlpha3Code;
import com.scnsoft.eldermark.entity.AdtTypeEnum;
import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.message.*;
import com.scnsoft.eldermark.entity.xds.segment.*;
import com.scnsoft.eldermark.services.connect.ConnectUtil;
import com.scnsoft.eldermark.services.exceptions.hl7.HL7ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.scnsoft.eldermark.services.hl7.util.Hl7Utils.*;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class Adt251Converter implements Converter<AdtMessage, Message> {

    private static final Logger logger = LoggerFactory.getLogger(Adt251Converter.class);

    @Override
    public Message convert(AdtMessage adtMessage) {
        try {
            final Message message = instantiateMessage(adtMessage);
            Terser messageTerser = new Terser(message);

            fillMSH(messageTerser);

            if (adtMessage instanceof EVNSegmentContainingMessage) {
                fillEVN(((EVNSegmentContainingMessage) adtMessage).getEvn(), messageTerser);
            }

            if (adtMessage instanceof PIDSegmentContainingMessage) {
                fillPID(((PIDSegmentContainingMessage) adtMessage).getPid(), messageTerser);
            }

            if (adtMessage instanceof PD1SegmentContainingMessage) {//not supported by Koble
                fillPD1(((PD1SegmentContainingMessage) adtMessage).getPd1(), messageTerser);
            }

            if (adtMessage instanceof PV1SegmentContainingMessage) {
                fillPV1(((PV1SegmentContainingMessage) adtMessage).getPv1(), messageTerser);
            }

            if (adtMessage instanceof AL1ListSegmentContainingMessage) {
                fillAL1List(((AL1ListSegmentContainingMessage) adtMessage).getAL1List(), messageTerser);
            }

            if (adtMessage instanceof DG1ListSegmentContainingMessage) {
                fillDG1List(((DG1ListSegmentContainingMessage) adtMessage).getDg1List(), messageTerser);
            }

            if (adtMessage instanceof PR1ListSegmentContaingMessage) {
                fillPR1List(((PR1ListSegmentContaingMessage) adtMessage).getPr1List(), messageTerser);
            }

            if (adtMessage instanceof GT1ListSegmentContainingMessage) { //not supported by Koble
                fillGT1List(((GT1ListSegmentContainingMessage) adtMessage).getGt1List(), messageTerser);
            }

            if (adtMessage instanceof IN1ListSegmentContainingMessage) { //not supported by Koble
                fillIN1List(((IN1ListSegmentContainingMessage) adtMessage).getIn1List(), messageTerser);
            }

            return message;
        } catch (IOException | HL7Exception e) {
            logger.warn("Error during Adt message [{}] conversion", adtMessage.getId(), e);
            throw new HL7ConversionException(e);
        }
    }


    private Message instantiateMessage(AdtMessage message) throws HL7Exception, IOException {
        final ModelClassFactory modelClassFactory = new CanonicalModelClassFactory("2.5.1");
        final AdtTypeEnum adtType = AdtTypeEnum.byEntityClass(message.getClass());
        final Class<? extends Message> messageClass = modelClassFactory.getMessageClass("ADT_" + adtType.name(), "2.5.1", false);

        AbstractMessage m = (AbstractMessage) ReflectionUtil.instantiateMessage(messageClass, modelClassFactory);

        m.initQuickstart("ADT", adtType.name(), "P_" + message.getId() + "_" + new Date().getTime());
        return m;
    }

    private void fillMSH(Terser messageTerser) throws HL7Exception {
        //Sending Application
        messageTerser.set("MSH-3-1", "SimplyConnect");
        messageTerser.set("MSH-3-2", ConnectUtil.EXCHANGE_HCID);

        //Sending Facility
        messageTerser.set("MSH-4-1", "SimplyConnect");
        messageTerser.set("MSH-4-2", ConnectUtil.EXCHANGE_HCID);
    }

    private void fillEVN(EVNEventTypeSegment evn, Terser messageTerser) throws HL7Exception {
        if (evn == null) {
            return;
        }
        messageTerser.set("EVN-1", evn.getEventTypeCode());
        messageTerser.set("EVN-2", toHl7TSFormat(evn.getRecordedDatetime()));
        messageTerser.set("EVN-4", getRawCode(evn.getEventReasonCode()));
        messageTerser.set("EVN-6", toHl7TSFormat(evn.getEventOccurred()));
    }

    private void fillPID(PIDPatientIdentificationSegment pid, Terser messageTerser) throws HL7Exception {
        if (pid == null) {
            return;
        }
        messageTerser.set("PID-1", "1");
        fillCXList("PID-3", messageTerser, pid.getPatientIdentifiers());
        messageTerser.set("PID-2", messageTerser.get("PID-3(0)-1"));
        fillXPNList("PID-5", messageTerser, pid.getPatientNames());
        fillXPNList("PID-6", messageTerser, pid.getMothersMaidenNames());
        messageTerser.set("PID-7", toHl7TSFormat(pid.getDateTimeOfBirth()));
        messageTerser.set("PID-8", getRawCode(pid.getAdministrativeSex()));
        fillXPNList("PID-9", messageTerser, pid.getPatientAliases());

        List<CECodedElement> races = prepareValidValues(pid.getRaces());
        if (!isEmpty(races)){
            fillCEList("PID-10", messageTerser, races);
        }
        fillXADList("PID-11", messageTerser, pid.getPatientAddresses());
        fillXTNList("PID-13", messageTerser, pid.getPhoneNumbersHome());
        fillXTNList("PID-14", messageTerser, pid.getPhoneNumbersBusiness());

        String primaryLanguageCode = getISO3LanguageCode(pid.getPrimaryLanguage().getIdentifier());
        if (primaryLanguageCode != null){
            pid.getPrimaryLanguage().setIdentifier(primaryLanguageCode.toUpperCase());
            fillCE("PID-15", messageTerser, pid.getPrimaryLanguage());
        }
        fillCE("PID-16", messageTerser, pid.getMaritalStatus());
        fillCE("PID-17", messageTerser, pid.getReligion());
        fillCX("PID-18", messageTerser, pid.getPatientAccountNumber());
        messageTerser.set("PID-19", pid.getSsnNumberPatient());
        fillDLN("PID-20", messageTerser, pid.getDriversLicenseNumber());
        fillCXList("PID-21", messageTerser, pid.getMothersIdentifiers());

        List<CECodedElement> ethnicGroups = prepareValidValues(pid.getEthnicGroups());
        if (!isEmpty(ethnicGroups)){
            fillCEList("PID-22", messageTerser, ethnicGroups);
        }

        messageTerser.set("PID-23", pid.getBirthPlace());

        if (pid.getBirthOrder() != null) {
            messageTerser.set("PID-25", pid.getBirthOrder().toString());
        }
        fillCEList("PID-26", messageTerser, pid.getCitizenships());
        fillCE("PID-27", messageTerser, pid.getVeteransMilitaryStatus());
        fillCE("PID-28", messageTerser, pid.getNationality());
        messageTerser.set("PID-29", toHl7TSFormat(pid.getPatientDeathDateAndTime()));
        messageTerser.set("PID-30", getRawCode(pid.getPatientDeathIndicator()));
    }

    private String getISO3LanguageCode(String languageName){
        List<LanguageAlpha3Code> langList = LanguageAlpha3Code.findByName(languageName);
        return !isEmpty(langList) && langList.get(0).getAlpha3B() != null ?
                langList.get(0).getAlpha3B().toString() :
                null;
    }

    private List<CECodedElement> prepareValidValues(List<CECodedElement> races){
        List<CECodedElement> resultList = new ArrayList<>();
        for (CECodedElement obj : races){
            if (obj.getHl7CodeTable() != null){
                resultList.add(obj);
            }
        }
        return resultList;
    }

    private void fillPD1(AdtPD1AdditionalDemographicSegment pd1, Terser messageTerser) throws HL7Exception {
        /*//not supported by Koble
        if (pd1 == null) {
            return;
        }
        messageTerser.set("PD1-2", pd1.getLivingArrangement());
        messageTerser.set("PD1-3", pd1.getPrimaryFacilityList());*/
    }

    private void fillPV1(PV1PatientVisitSegment pv1, Terser messageTerser) throws HL7Exception {
        if (pv1 == null) {
            return;
        }
        messageTerser.set("PV1-2", getRawCode(pv1.getPatientClass()));
        fillPL("PV1-3", messageTerser, pv1.getAssignedPatientLocation());
        messageTerser.set("PV1-4", getRawCode(pv1.getAdmissionType()));

        fillPL("PV1-6", messageTerser, pv1.getPriorPatientLocation());
        fillXCN("PV1-7", messageTerser, pv1.getAttendingDoctor());
        fillXCN("PV1-8", messageTerser, pv1.getRefferingDoctor());
        fillXCN("PV1-9", messageTerser, pv1.getConsultingDoctor());

        messageTerser.set("PV1-12", pv1.getPreadmitTestIndicator());
        messageTerser.set("PV1-13", getRawCode(pv1.getReadmissionIndicator()));
        messageTerser.set("PV1-14", getRawCode(pv1.getAdmitSource()));
        fillCodedValuesList("PV1-15", messageTerser, pv1.getAmbulatoryStatuses());
        messageTerser.set("PV1-36", pv1.getDischargeDisposition());

        DLDDischargeLocation location = pv1.getDischargedToLocation();
        if (location != null){
            messageTerser.set("PV1-37", location.getDischargeLocation());
        }

        messageTerser.set("PV1-44", toHl7TSFormat(pv1.getAdmitDatetime()));
        messageTerser.set("PV1-45", toHl7TSFormat(pv1.getDischargeDatetime()));
    }

    private void fillAL1List(List<AdtAL1AllergySegment> al1List, Terser terser) throws HL7Exception {
        fillList("AL1", terser, al1List, new EntityFiller<AdtAL1AllergySegment>() {
            @Override
            public void fill(String base, Terser messageTerser,  AdtAL1AllergySegment entity) throws HL7Exception {
                fillAL1(base, messageTerser, entity);
            }
        });
    }

    private void fillAL1(String base, Terser terser, AdtAL1AllergySegment entity) throws HL7Exception {
        terser.set(base + "-1",  entity.getSetId());
        fillCE(base + "-2", terser, entity.getAllergenType());
        fillCE(base + "-3", terser, entity.getAllergyCode());
        terser.set(base + "-4-1",  getRawCode(entity.getAllergySeverity()));
        fillAL1ReactionCodes(base + "-5", terser, entity);
        terser.set(base + "-6",  toHl7DTFormat(entity.getIdentificationDate()));
    }

    private void fillAL1ReactionCodes(String base, Terser terser, AdtAL1AllergySegment allergySegment) throws HL7Exception {
        fillList(base, terser, allergySegment.getAllergyReactionList(), new EntityFiller<String>() {
            @Override
            public void fill(String base, Terser messageTerser, String reaction) throws HL7Exception {
                messageTerser.set(base, reaction);
            }
        });
    }

    private void fillDG1List(List<AdtDG1DiagnosisSegment> dg1List, Terser terser) throws HL7Exception {
        fillList("DG1", terser, dg1List, new EntityFiller<AdtDG1DiagnosisSegment>() {
            @Override
            public void fill(String base, Terser messageTerser,  AdtDG1DiagnosisSegment entity) throws HL7Exception {
                fillDG1(base, messageTerser, entity);
            }
        });
    }

    private void fillDG1(String base, Terser terser, AdtDG1DiagnosisSegment entity) throws HL7Exception {
        fillCE(base + "-3", terser, entity.getDiagnosisCode());
        terser.set(base + "-5", toHl7DTFormat(entity.getDiagnosisDateTime()));
        terser.set(base + "-6-1", getRawCode(entity.getDiagnosisType()));
        fillXCNList(base + "-16", terser, entity.getDiagnosingClinicianList());
    }

    private void fillPR1List(List<PR1ProceduresSegment> pr1List, Terser terser) throws HL7Exception {
        fillList("/PROCEDURE", terser, pr1List, new EntityFiller<PR1ProceduresSegment>() {
            @Override
            public void fill(String base, Terser messageTerser,  PR1ProceduresSegment entity) throws HL7Exception {
                fillPR1(base, messageTerser, entity);
            }
        });
    }

    private void fillPR1(String base, Terser terser, PR1ProceduresSegment entity) throws HL7Exception {
        base = base + "/PR1";
        terser.set(base + "-2", entity.getProcedureCodingMethod());
        fillCE(base + "-3", terser, entity.getProcedureCode());
        terser.set(base + "-4", entity.getProcedureDescription());
        terser.set(base + "-5", toHl7DTFormat(entity.getProcedureDatetime()));
        terser.set(base + "-6", getRawCode(entity.getProcedureFunctionalType()));
        fillCE(base + "-15", terser, entity.getAssociatedDiagnosisCode());
    }

    private void fillGT1List(List<AdtGT1GuarantorSegment> gt1List, Terser terser) {
        //not supported by Koble
    }

    private void fillIN1List(List<IN1InsuranceSegment> in1List, Terser messageTerser) {
        //not supported by Koble
    }

    // ============================= HELPER METHODS ==================================

    private void fillXCNList(String base, Terser terser, List<XCNExtendedCompositeIdNumberAndNameForPersons> list) throws HL7Exception {
        fillList(base, terser, list, new EntityFiller<XCNExtendedCompositeIdNumberAndNameForPersons>() {
            @Override
            public void fill(String base, Terser messageTerser,  XCNExtendedCompositeIdNumberAndNameForPersons entity) throws HL7Exception {
                fillXCN(base, messageTerser, entity);
            }
        });
    }

    private void fillCE(String base, Terser terser, CECodedElement codedElement) throws HL7Exception {
        if (codedElement != null) {
            terser.set(base + "-1", codedElement.getIdentifier());
            terser.set(base + "-2", codedElement.getText());
            terser.set(base + "-3", codedElement.getNameOfCodingSystem());
            terser.set(base + "-4", codedElement.getAlternateIdentifier());
            terser.set(base + "-5", codedElement.getAlternateText());
            terser.set(base + "-6", codedElement.getNameOfAlternateCodingSystem());
        }
    }

    private void fillCXList(String base, Terser messageTerser, List<CXExtendedCompositeId> cxList) throws HL7Exception {
        fillList(base, messageTerser, cxList, new EntityFiller<CXExtendedCompositeId>() {
            @Override
            public void fill(String base, Terser messageTerser, CXExtendedCompositeId entity) throws HL7Exception {
                fillCX(base, messageTerser, entity);
            }
        });
    }

    private void fillXPNList(String base, Terser messageTerser, List<XPNPersonName> xpnList) throws HL7Exception {
        fillList(base, messageTerser, xpnList, new EntityFiller<XPNPersonName>() {
            @Override
            public void fill(String base, Terser messageTerser, XPNPersonName entity) throws HL7Exception {
                fillXPN(base, messageTerser, entity);
            }
        });
    }

    private void fillCEList(String base, Terser messageTerser, List<CECodedElement> ceList) throws HL7Exception {
        fillList(base, messageTerser, ceList, new EntityFiller<CECodedElement>() {
            @Override
            public void fill(String base, Terser messageTerser, CECodedElement entity) throws HL7Exception {
                fillCE(base, messageTerser, entity);
            }
        });
    }

    private void fillXADList(String base, Terser messageTerser, List<XADPatientAddress> xadList) throws HL7Exception {
        fillList(base, messageTerser, xadList, new EntityFiller<XADPatientAddress>() {
            @Override
            public void fill(String base, Terser messageTerser, XADPatientAddress entity) throws HL7Exception {
                fillXAD(base, messageTerser, entity);
            }
        });
    }

    private void fillXTNList(String base, Terser messageTerser, List<XTNPhoneNumber> xtnList) throws HL7Exception {
        fillList(base, messageTerser, xtnList, new EntityFiller<XTNPhoneNumber>() {
            @Override
            public void fill(String base, Terser messageTerser, XTNPhoneNumber entity) throws HL7Exception {
                fillXTN(base, messageTerser, entity);
            }
        });
    }

    private <T extends CodedValueForHL7Table> void fillCodedValuesList(String base, Terser messageTerser, List<T> codedValues) throws HL7Exception {
        fillList(base, messageTerser, codedValues, new EntityFiller<T>() {
            @Override
            public void fill(String base, Terser messageTerser, T entity) throws HL7Exception {
                messageTerser.set(base, getRawCode(entity));
            }
        });
    }

    private <T> void fillList(String base, Terser messageTerser, List<T> entitiesList, EntityFiller<T> entityFiller) throws HL7Exception {
        if (isEmpty(entitiesList)) {
            return;
        }
        int i = 0;
        for (T entity : entitiesList) {
            String currentLocation = base + "(" + i + ")";
            entityFiller.fill(currentLocation, messageTerser, entity);
            i++;
        }
    }

    private void fillCX(String base, Terser messageTerser, CXExtendedCompositeId cx) throws HL7Exception {
        if (cx == null) {
            return;
        }
        messageTerser.set(base + "-1", cx.getpId());

        fillHD(base + "-4", messageTerser, cx.getAssigningAuthority());
        messageTerser.set(base + "-5", cx.getIdentifierTypeCode());
        fillHD(base + "-6", messageTerser, cx.getAssigningFacility());
    }


    private void fillHD(String base, Terser messageTerser, HDHierarchicDesignator hd) throws HL7Exception {
        if (hd == null) {
            return;
        }
        messageTerser.set(base + "-1", hd.getNamespaceID());
        messageTerser.set(base + "-2", hd.getUniversalID());
        messageTerser.set(base + "-3", hd.getUniversalIDType());
    }

    private void fillXPN(String base, Terser messageTerser, XPNPersonName xpn) throws HL7Exception {
        if (xpn == null) {
            return;
        }
        messageTerser.set(base + "-1", xpn.getLastName());
        messageTerser.set(base + "-2", xpn.getFirstName());
        messageTerser.set(base + "-3", xpn.getMiddleName());
        messageTerser.set(base + "-4", xpn.getSuffix());
        messageTerser.set(base + "-5", xpn.getPrefix());
        messageTerser.set(base + "-6", xpn.getDegree());
        messageTerser.set(base + "-7", xpn.getNameTypeCode());
        messageTerser.set(base + "-8", xpn.getNameRepresentationCode());
    }

    private void fillXAD(String base, Terser messageTerser, XADPatientAddress xad) throws HL7Exception {
        if (xad == null) {
            return;
        }
        messageTerser.set(base + "-1", xad.getStreetAddress());
        messageTerser.set(base + "-2", xad.getOtherDesignation());
        messageTerser.set(base + "-3", xad.getCity());
        messageTerser.set(base + "-4", xad.getState());
        messageTerser.set(base + "-5", xad.getZip());

        messageTerser.set(base + "-7", getRawCode(xad.getAddressType()));
        messageTerser.set(base + "-8", xad.getOtherGeographicDesignation());

        messageTerser.set(base + "-10", xad.getCensusTract());
        messageTerser.set(base + "-11", getRawCode(xad.getAddressRepresentationCode()));
    }

    private void fillXTN(String base, Terser messageTerser, XTNPhoneNumber xtn) throws HL7Exception {
        if (xtn == null) {
            return;
        }
        messageTerser.set(base + "-1", xtn.getTelephoneNumber());

        messageTerser.set(base + "-4", xtn.getEmail());
        messageTerser.set(base + "-5", xtn.getCountryCode());
        messageTerser.set(base + "-6", xtn.getAreaCode());
        messageTerser.set(base + "-7", xtn.getPhoneNumber());
        messageTerser.set(base + "-8", xtn.getExtension());
        messageTerser.set(base + "-9", xtn.getAnyText());
    }

    private void fillDLN(String base, Terser messageTerser, DLNDriverSLicenseNumber dln) throws HL7Exception {
        if (dln == null) {
            return;
        }
        messageTerser.set(base + "-1", dln.getLicenseNumber());
        messageTerser.set(base + "-2", dln.getIssuingStateProvinceCountry());
        messageTerser.set(base + "-3", toHl7DTFormat(dln.getExpirationDate()));
    }

    private void fillPL(String base, Terser messageTerser, PLPatientLocation pl) throws HL7Exception {
        if (pl == null) {
            return;
        }
        messageTerser.set(base + "-1", pl.getPointOfCare());
        messageTerser.set(base + "-2", pl.getRoom());
        messageTerser.set(base + "-3", pl.getBed());
        fillHD(base + "-4", messageTerser, pl.getFacility());
        messageTerser.set(base + "-5", pl.getLocationStatus());
        messageTerser.set(base + "-6", pl.getPersonLocationType());
        messageTerser.set(base + "-7", pl.getBuilding());
        messageTerser.set(base + "-8", pl.getFloor());
        messageTerser.set(base + "-9", pl.getLocationDescription());
    }

    private void fillXCN(String base, Terser messageTerser, XCNExtendedCompositeIdNumberAndNameForPersons xcn) throws HL7Exception {
        if (xcn == null) {
            return;
        }
        messageTerser.set(base + "-2", xcn.getLastName());
        messageTerser.set(base + "-3", xcn.getFirstName());
        messageTerser.set(base + "-4", xcn.getMiddleName());
        messageTerser.set(base + "-5", xcn.getSuffix());
        messageTerser.set(base + "-6", xcn.getPrefix());
        messageTerser.set(base + "-7", xcn.getDegree());
        messageTerser.set(base + "-8", xcn.getSourceTable());
        fillHD(base + "-9", messageTerser, xcn.getAssigningAuthority());
        messageTerser.set(base + "-10", xcn.getNameTypeCode());

        messageTerser.set(base + "-13", xcn.getIdentifierTypeCode());
        fillHD(base + "-14", messageTerser, xcn.getAssigningFacility());
        messageTerser.set(base + "-15", xcn.getNameRepresentationCode());
    }
}
