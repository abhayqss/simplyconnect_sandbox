package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v251.segment.IN1;
import com.scnsoft.eldermark.entity.xds.datatype.XONExtendedCompositeNameAndIdForOrganizations;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0063Relationship;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0098TypeOfAgreement;
import com.scnsoft.eldermark.entity.xds.segment.IN1InsuranceSegment;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.In1SegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class In1SegmentParserImpl extends AbstractAdtSegmentParser<IN1InsuranceSegment, IN1> implements In1SegmentParser {

    @Override
    public IN1InsuranceSegment doParse(IN1 segment, MessageSource messageSource) throws HL7Exception {
        final IN1InsuranceSegment in1 = new IN1InsuranceSegment();
        in1.setSetId(segment.getIn11_SetIDIN1().getValue());
        in1.setInsurancePlanId(dataTypeService.createCE(segment.getIn12_InsurancePlanID()));

        var insuranceCompanyId = segment.getIn13_InsuranceCompanyID();
        if (insuranceCompanyId != null && insuranceCompanyId.length > 0)
            in1.setInsuranceCompanyId(dataTypeService.createCX(insuranceCompanyId[0]));

//      todo cardinality
        final List<XONExtendedCompositeNameAndIdForOrganizations> names = dataTypeService.createXONList(segment.getIn14_InsuranceCompanyName());
        if (!CollectionUtils.isEmpty(names)) {
            in1.setInsuranceCompanyName(names.get(0));
        }

        in1.setInsuranceCompanyAddresses(dataTypeService.createXADList(segment.getIn15_InsuranceCompanyAddress()));
        in1.setInsuranceCoPhoneNumbers(dataTypeService.createXTNList(segment.getIn17_InsuranceCoPhoneNumber()));
        in1.setGroupNumber(dataTypeService.getValue(segment.getIn18_GroupNumber()));
        in1.setGroupNames(dataTypeService.createXONList(segment.getIn19_GroupName()));

        in1.setPlanEffectiveDate(dataTypeService.convertDtToInstant(segment.getIn112_PlanEffectiveDate()));
        in1.setPlanExpirationDate(dataTypeService.convertDtToInstant(segment.getIn113_PlanExpirationDate()));
        in1.setPlanType(dataTypeService.getValue(segment.getIn115_PlanType()));

        in1.setNamesOfInsured(dataTypeService.createXPNList(segment.getIn116_NameOfInsured()));
        in1.setInsuredsRelationshipToPatient(dataTypeService.createCEWithCodedValues(segment.getIn117_InsuredSRelationshipToPatient(),
                HL7CodeTable0063Relationship.class));
        in1.setInsuredsDateOfBirth(dataTypeService.convertHL7Date(segment.getIn118_InsuredSDateOfBirth().getTs1_Time().getValue()));
        in1.setInsuredsAddresses(dataTypeService.createXADList(segment.getIn119_InsuredSAddress()));
        in1.setPreAdmitCert(dataTypeService.getValue(segment.getIn128_PreAdmitCert()));
        in1.setTypeOfAgreementCode(dataTypeService.createIS(segment.getIn131_TypeOfAgreementCode(), HL7CodeTable0098TypeOfAgreement.class));
        in1.setPolicyNumber(dataTypeService.getValue(segment.getIn136_PolicyNumber()));

        return in1;
    }
}
