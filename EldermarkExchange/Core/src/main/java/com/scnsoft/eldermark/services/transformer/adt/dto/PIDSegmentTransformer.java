package com.scnsoft.eldermark.services.transformer.adt.dto;

import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0136YesNoIndicator;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7UserDefinedCodeTable;
import com.scnsoft.eldermark.entity.xds.segment.PIDPatientIdentificationSegment;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.PIDPatientIdentificationSegmentDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.DLNDriverSLicenseNumberDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XADPatientAddressDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XTNPhoneNumberDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class PIDSegmentTransformer implements Converter<PIDPatientIdentificationSegment, PIDPatientIdentificationSegmentDto> {

    @Autowired
    private ListAndItemTransformer<XADPatientAddress, XADPatientAddressDto> xadPatientAddressTransformer;

    @Autowired
    private Converter<ISCodedValueForUserDefinedTables<? extends HL7UserDefinedCodeTable>, String> isCodedValueForUserDefinedTablesStringConverter;

    @Autowired
    private ListAndItemTransformer<CECodedElement, String> ceCodedElementStringConverter;

    @Autowired
    private Converter<IDCodedValueForHL7Tables<HL7CodeTable0136YesNoIndicator>, Boolean> idCodedValueForHL7TablesBooleanConverter;

    @Autowired
    private ListAndItemTransformer<CXExtendedCompositeId, String> cxExtendedCompositeIdStringTransformer;

    @Autowired
    private ListAndItemTransformer<XPNPersonName, String> xpnPersonNameStringTransformer;

    @Autowired
    private ListAndItemTransformer<XTNPhoneNumber, XTNPhoneNumberDto> xtnPhoneNumberTransformer;

    @Autowired
    private Converter<DLNDriverSLicenseNumber, DLNDriverSLicenseNumberDto> dlnDriverSLicenseNumberTransformer;

    @Override
    public PIDPatientIdentificationSegmentDto convert(PIDPatientIdentificationSegment pid) {
        if (pid == null) {
            return null;
        }
        final PIDPatientIdentificationSegmentDto target = new PIDPatientIdentificationSegmentDto();
        target.setPatientIdentifiers(cxExtendedCompositeIdStringTransformer.convertList(pid.getPatientIdentifiers()));
        target.setPatientNames(xpnPersonNameStringTransformer.convertList(pid.getPatientNames()));
        target.setMothersMaidenNames(xpnPersonNameStringTransformer.convertList(pid.getMothersMaidenNames()));
        target.setDateTimeOfBirth(pid.getDateTimeOfBirth());
        target.setSex(isCodedValueForUserDefinedTablesStringConverter.convert(pid.getAdministrativeSex()));
        target.setPatientAliases(xpnPersonNameStringTransformer.convertList(pid.getPatientAliases()));
        target.setRaces(ceCodedElementStringConverter.convertList(pid.getRaces()));
        target.setPatientAddresses(xadPatientAddressTransformer.convertList(pid.getPatientAddresses()));
        target.setPhoneNumbersHome(xtnPhoneNumberTransformer.convertList(pid.getPhoneNumbersHome()));
        target.setPhoneNumbersBusiness(xtnPhoneNumberTransformer.convertList(pid.getPhoneNumbersBusiness()));
        target.setPrimaryLanguage(ceCodedElementStringConverter.convert(pid.getPrimaryLanguage()));
        target.setMaritalStatus(ceCodedElementStringConverter.convert(pid.getMaritalStatus()));
        target.setReligion(ceCodedElementStringConverter.convert(pid.getReligion()));
        target.setPatientAccountNumber(cxExtendedCompositeIdStringTransformer.convert(pid.getPatientAccountNumber()));
        target.setSsnNumberPatient(pid.getSsnNumberPatient());
        target.setDriverLicenseNumber(dlnDriverSLicenseNumberTransformer.convert(pid.getDriversLicenseNumber()));
        target.setMotherIdentifiers(cxExtendedCompositeIdStringTransformer.convertList(pid.getMothersIdentifiers()));
        target.setEtnicGroups(ceCodedElementStringConverter.convertList(pid.getEthnicGroups()));
        target.setBirthPlace(pid.getBirthPlace());
        if (pid.getBirthOrder() != null && pid.getBirthOrder() != 0)
            target.setBirthOrder(pid.getBirthOrder());
        target.setCitizenships(ceCodedElementStringConverter.convertList(pid.getCitizenships()));
        target.setVeteransMilitaryStatus(ceCodedElementStringConverter.convert(pid.getVeteransMilitaryStatus()));
        target.setNationality(ceCodedElementStringConverter.convert(pid.getNationality()));
        target.setDeathDateTime(pid.getPatientDeathDateAndTime());
        target.setDeathIndicator(idCodedValueForHL7TablesBooleanConverter.convert(pid.getPatientDeathIndicator()));
        return target;
    }

}
