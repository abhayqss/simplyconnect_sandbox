package com.scnsoft.eldermark.services.transformer.adt.dto;

import com.scnsoft.eldermark.entity.xds.datatype.*;
import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;
import com.scnsoft.eldermark.services.transformer.ListAndItemTransformer;
import com.scnsoft.eldermark.shared.carecoordination.adt.IN1InsuranceSegmentDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CECodedElementDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.CXExtendedCompositeIdDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XONExtendedCompositeNameAndIdForOrganizationsDto;
import com.scnsoft.eldermark.shared.carecoordination.adt.datatype.XTNPhoneNumberDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IN1SegmentTransformer extends ListAndItemTransformer<IN1InsuranceSegment, IN1InsuranceSegmentDto> {

    @Autowired
    private Converter<CECodedElement, CECodedElementDto> ceCodedElementTransformer;

    @Autowired
    private ListAndItemTransformer<CXExtendedCompositeId, CXExtendedCompositeIdDto> cxExtendedCompositeIdTransformer;

    @Autowired
    private ListAndItemTransformer<XTNPhoneNumber, XTNPhoneNumberDto> xtnPhoneNumberTransformer;

    @Autowired
    private ListAndItemTransformer<XONExtendedCompositeNameAndIdForOrganizations, XONExtendedCompositeNameAndIdForOrganizationsDto> xonExtendedCompositeNameAndIdForOrganizationsDtoTransformer;

    @Override
    public IN1InsuranceSegmentDto convert(IN1InsuranceSegment in1InsuranceSegment) {
        if (in1InsuranceSegment == null) {
            return null;
        }
        IN1InsuranceSegmentDto target = new IN1InsuranceSegmentDto();
        target.setSetId(in1InsuranceSegment.getSetId());
        target.setInsurancePlanId(ceCodedElementTransformer.convert(in1InsuranceSegment.getInsurancePlanId()));
        target.setInsuranceCompanyId(cxExtendedCompositeIdTransformer.convert(in1InsuranceSegment.getInsuranceCompanyId()));
        target.setInsuranceCompanyName(xonExtendedCompositeNameAndIdForOrganizationsDtoTransformer.convert(in1InsuranceSegment.getInsuranceCompanyName()));
        if (CollectionUtils.isNotEmpty(in1InsuranceSegment.getInsuranceCoPhoneNumbers())) {
            List<XTNPhoneNumberDto> insuranceCoPhoneNumberDtos = new ArrayList<>();
            xtnPhoneNumberTransformer.convertList(in1InsuranceSegment.getInsuranceCoPhoneNumbers(), insuranceCoPhoneNumberDtos);
            target.setInsuranceCoPhoneNumbers(insuranceCoPhoneNumberDtos);
        }
        target.setGroupNumber(in1InsuranceSegment.getGroupNumber());
        if (CollectionUtils.isNotEmpty(in1InsuranceSegment.getGroupNames())) {
            List<XONExtendedCompositeNameAndIdForOrganizationsDto> groupNameDtos = new ArrayList<>();
            xonExtendedCompositeNameAndIdForOrganizationsDtoTransformer.convertList(in1InsuranceSegment.getGroupNames(), groupNameDtos);
            target.setGroupNames(groupNameDtos);
        }
        target.setPlanEffectiveDate(in1InsuranceSegment.getPlanEffectiveDate());
        target.setPlanExpirationDate(in1InsuranceSegment.getPlanExpirationDate());
        target.setPlanType(in1InsuranceSegment.getPlanType());
        if (CollectionUtils.isNotEmpty(in1InsuranceSegment.getNamesOfInsured())) {
            List<String> namesOfInsured = new ArrayList<>();
            for (XPNPersonName nameOfInsured : in1InsuranceSegment.getNamesOfInsured()) {
                namesOfInsured.add(CareCoordinationUtils.getFullName(nameOfInsured.getFirstName(), nameOfInsured.getLastName()));
            }
            target.setNamesOfInsured(namesOfInsured);
        }
        target.setInsuredsRelationshipToPatient(ceCodedElementTransformer.convert(in1InsuranceSegment.getInsuredsRelationshipToPatient()));
        return target;
    }
}
