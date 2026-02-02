package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v251.segment.GT1;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0001AdministrativeSex;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0063Relationship;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0066EmploymentStatus;
import com.scnsoft.eldermark.entity.xds.segment.AdtGT1GuarantorSegment;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.Gt1GuarantorSegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class Gt1GuarantorSegmentParserImpl extends AbstractAdtSegmentParser<AdtGT1GuarantorSegment, GT1> implements Gt1GuarantorSegmentParser {

    @Override
    public AdtGT1GuarantorSegment doParse(final GT1 segment, MessageSource messageSource) throws HL7Exception, ApplicationException {
        final AdtGT1GuarantorSegment result = new AdtGT1GuarantorSegment();
        result.setSetId(segment.getSetIDGT1().getValue());

        result.setGuarantorNumbers(HapiUtils.convertArray(
                segment.getGt12_GuarantorNumber(),
                dataTypeService::createCX
        ));
        result.setGuarantorNameList(HapiUtils.convertArray(
                segment.getGt13_GuarantorName(),
                dataTypeService::createXPN)
        );

        result.setGuarantorAddressList(HapiUtils.convertArray(
                segment.getGt15_GuarantorAddress(),
                dataTypeService::createXAD)
        );

        result.setGuarantorPhNumHomeList(HapiUtils.convertArray(
                segment.getGt16_GuarantorPhNumHome(),
                dataTypeService::createXTN)
        );

        result.setGuarantorDatetimeOfBirth(dataTypeService.convertTS(segment.getGt18_GuarantorDateTimeOfBirth()));

        result.setGuarantorAdministrativeSex(dataTypeService.createIS(
                segment.getGt19_GuarantorAdministrativeSex(),
                HL7CodeTable0001AdministrativeSex.class)
        );

        result.setGuarantorType(dataTypeService.getValue(segment.getGt110_GuarantorType()));

        result.setGuarantorRelationship(dataTypeService.createCEWithCodedValues(
                segment.getGt111_GuarantorRelationship(),
                HL7CodeTable0063Relationship.class
        ));

        result.setGuarantorEmploymentStatus(dataTypeService.createIS(
                segment.getGt120_GuarantorEmploymentStatus(),
                HL7CodeTable0066EmploymentStatus.class));

        result.setPrimaryLanguage(dataTypeService.createCE(segment.getGt136_PrimaryLanguage()));
        return result;
    }
}
