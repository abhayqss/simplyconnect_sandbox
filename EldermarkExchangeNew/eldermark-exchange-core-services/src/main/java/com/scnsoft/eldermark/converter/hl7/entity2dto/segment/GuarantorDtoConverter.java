package com.scnsoft.eldermark.converter.hl7.entity2dto.segment;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.dto.adt.datatype.CXExtendedCompositeIdDto;
import com.scnsoft.eldermark.dto.adt.datatype.XPNDto;
import com.scnsoft.eldermark.dto.adt.datatype.XTNPhoneNumberDto;
import com.scnsoft.eldermark.dto.adt.segment.AdtGuarantorDto;
import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.segment.AdtGT1GuarantorSegment;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GuarantorDtoConverter implements ListAndItemConverter<AdtGT1GuarantorSegment, AdtGuarantorDto> {

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementDtoConverter;

    @Autowired
    private ListAndItemConverter<XADPatientAddress, AddressDto> xadPatientAddressConverter;

    @Autowired
    private ListAndItemConverter<XTNPhoneNumber, XTNPhoneNumberDto> xtnConverter;

    @Autowired
    private ListAndItemConverter<CXExtendedCompositeId, CXExtendedCompositeIdDto> cxConverter;

    @Autowired
    private Converter<CodedValueForHL7Table, String> codedValueConverter;

    @Autowired
    private ListAndItemConverter<XPNPersonName, XPNDto> xpnConverter;

    @Override
    public AdtGuarantorDto convert(AdtGT1GuarantorSegment source) {
        if (source == null) {
            return null;
        }
        var target = new AdtGuarantorDto();
        target.setSetId(source.getSetId());
        target.setGuarantorNumbers(cxConverter.convertList(source.getGuarantorNumbers()));

        if (CollectionUtils.isNotEmpty(source.getGuarantorNameList())) {
            target.setGuarantorNames(xpnConverter.convertList(source.getGuarantorNameList()));
        }
        if (CollectionUtils.isNotEmpty(source.getGuarantorAddressList())) {
            target.setGuarantorAddresses(xadPatientAddressConverter.convertList(source.getGuarantorAddressList()));
        }
        if (CollectionUtils.isNotEmpty(source.getGuarantorPhNumHomeList())) {
            target.setGuarantorHomePhones(xtnConverter.convertList(source.getGuarantorPhNumHomeList()));
        }
        target.setGuarantorDatetimeOfBirth(DateTimeUtils.toEpochMilli(source.getGuarantorDatetimeOfBirth()));
        target.setGuarantorAdministrativeSex(codedValueConverter.convert(source.getGuarantorAdministrativeSex()));
        target.setGuarantorType(source.getGuarantorType());
        target.setGuarantorRelationship(ceCodedElementDtoConverter.convert(source.getGuarantorRelationship()));
        target.setGuarantorEmploymentStatus(codedValueConverter.convert(source.getGuarantorEmploymentStatus()));
        target.setPrimaryLanguage(ceCodedElementDtoConverter.convert(source.getPrimaryLanguage()));
        return target;
    }

}
