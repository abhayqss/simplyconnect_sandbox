package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v231.segment.IN1;
import org.openhealthtools.openxds.entity.datatype.XONExtendedCompositeNameAndIdForOrganizations;
import org.openhealthtools.openxds.entity.hl7table.HL7CodeTable0063Relationship;
import org.openhealthtools.openxds.entity.segment.IN1InsuranceSegment;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.In1SegmentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class In1SegmentParserImpl extends AbstractAdtSegmentParser<IN1InsuranceSegment, IN1> implements In1SegmentParser {

    @Autowired
    private DataTypeService dataTypeService;

    @Autowired
    private EmptyHL7Field231Service emptyHL7Field231Service;

    @Override
    public IN1InsuranceSegment doParse(IN1 segment) throws HL7Exception {
        final IN1InsuranceSegment in1 = new IN1InsuranceSegment();
        in1.setSetId(segment.getSetIDIN1().getValue());
        in1.setInsurancePlanId(dataTypeService.createCE(segment.getInsurancePlanID()));
        if (segment.getInsuranceCompanyID() != null && segment.getInsuranceCompanyID().length > 0)
            in1.setInsuranceCompanyId(dataTypeService.createCX(segment.getInsuranceCompanyID()[0]));

//      todo cardinality
        final List<XONExtendedCompositeNameAndIdForOrganizations> names = dataTypeService.createXONList(segment.getInsuranceCompanyName());
        if (!CollectionUtils.isEmpty(names)) {
            in1.setInsuranceCompanyName(names.get(0));
        }

        in1.setInsuranceCoPhoneNumbers(dataTypeService.createXTNList(segment.getInsuranceCoPhoneNumber()));
        in1.setGroupNumber(dataTypeService.getValue(segment.getGroupNumber()));
        in1.setGroupNames(dataTypeService.createXONList(segment.getGroupName()));

        in1.setPlanEffectiveDate(dataTypeService.convertDtToDate(segment.getPlanEffectiveDate()));
        in1.setPlanExpirationDate(dataTypeService.convertDtToDate(segment.getPlanExpirationDate()));
        in1.setPlanType(dataTypeService.getValue(segment.getPlanType()));

        in1.setNamesOfInsured(dataTypeService.createXPNList(segment.getNameOfInsured()));
        in1.setInsuredsRelationshipToPatient(dataTypeService.createCEWithCodedValues(segment.getInsuredSRelationshipToPatient(),
                    HL7CodeTable0063Relationship.class));

        return in1;
    }

    @Override
    public boolean isHl7SegmentEmpty(final IN1 hl7Segment) {
        return hl7Segment == null || emptyHL7Field231Service.isAbstractPrimitiveEmpty(hl7Segment.getSetIDIN1());
    }

}
