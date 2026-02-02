package com.scnsoft.eldermark.converter.hl7.entity2dto.segment;

import com.scnsoft.eldermark.converter.base.ListAndItemConverter;
import com.scnsoft.eldermark.dto.AddressDto;
import com.scnsoft.eldermark.dto.adt.datatype.*;
import com.scnsoft.eldermark.dto.adt.segment.AdtInsuranceDto;
import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class InsuranceDtoConverter implements ListAndItemConverter<IN1InsuranceSegment, AdtInsuranceDto> {

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementDtoConverter;

    @Autowired
    private ListAndItemConverter<CXExtendedCompositeId, CXExtendedCompositeIdDto> cxExtendedCompositeIdConverter;

    @Autowired
    private ListAndItemConverter<XONExtendedCompositeNameAndIdForOrganizations, XONExtendedCompositeNameAndIdForOrganizationsDto> xonExtendedCompositeNameAndIdForOrganizationsConverter;

    @Autowired
    private ListAndItemConverter<XTNPhoneNumber, XTNPhoneNumberDto> xtnPhoneNumberDtoConverter;

    @Autowired
    private ListAndItemConverter<XPNPersonName, XPNDto> xpnConverter;

    @Autowired
    private ListAndItemConverter<XADPatientAddress, AddressDto> xadConverter;

    @Autowired
    private Converter<CodedValueForHL7Table, String> codedValueConverter;

    @Override
    public AdtInsuranceDto convert(IN1InsuranceSegment source) {
        if (source == null) {
            return null;
        }
        var target = new AdtInsuranceDto();
        target.setSetId(source.getSetId());
        target.setInsurancePlanId(ceCodedElementDtoConverter.convert(source.getInsurancePlanId()));
        target.setInsuranceCompanyIds(CareCoordinationUtils.nullOrSingletoneList(cxExtendedCompositeIdConverter.convert(source.getInsuranceCompanyId())));
        target.setInsuranceCompanyNames(CareCoordinationUtils.nullOrSingletoneList(
                xonExtendedCompositeNameAndIdForOrganizationsConverter.convert(source.getInsuranceCompanyName())));
        target.setInsuranceCompanyAddresses(xadConverter.convertList(source.getInsuranceCompanyAddresses()));
        if (CollectionUtils.isNotEmpty(source.getInsuranceCoPhoneNumbers())) {
            List<XTNPhoneNumberDto> insuranceCoPhoneNumberDtos = xtnPhoneNumberDtoConverter
                    .convertList(source.getInsuranceCoPhoneNumbers());
            target.setInsuranceCoPhoneNumbers(insuranceCoPhoneNumberDtos);
        }
        target.setGroupNumber(source.getGroupNumber());
        if (CollectionUtils.isNotEmpty(source.getGroupNames())) {
            List<XONExtendedCompositeNameAndIdForOrganizationsDto> groupNameDtos = xonExtendedCompositeNameAndIdForOrganizationsConverter
                    .convertList(source.getGroupNames());
            target.setGroupNames(groupNameDtos);
        }
        target.setPlanEffectiveDate(DateTimeUtils.toEpochMilli(source.getPlanEffectiveDate()));
        target.setPlanExpirationDate(DateTimeUtils.toEpochMilli(source.getPlanExpirationDate()));
        target.setPlanType(source.getPlanType());

        if (CollectionUtils.isNotEmpty(source.getNamesOfInsured())) {
            target.setNamesOfInsured(xpnConverter.convertList(source.getNamesOfInsured()));
        }

        target.setInsuredsRelationshipToPatient(ceCodedElementDtoConverter.convert(source.getInsuredsRelationshipToPatient()));
        target.setInsuredsDateOfBirth(DateTimeUtils.toEpochMilli(source.getInsuredsDateOfBirth()));
        target.setInsuredsAddresses(xadConverter.convertList(source.getInsuredsAddresses()));
        target.setPreAdmitCert(source.getPreAdmitCert());
        target.setTypeOfAgreementCode(codedValueConverter.convert(source.getTypeOfAgreementCode()));
        target.setPolicyNumber(source.getPolicyNumber());
        return target;
    }

}
