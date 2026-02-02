package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v251.segment.PD1;
import com.scnsoft.eldermark.entity.xds.segment.AdtPD1AdditionalDemographicSegment;
import com.scnsoft.eldermark.hl7v2.HapiUtils;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.Pd1AdditionalDemographicSegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class Pd1AdditionalDemographicSegmentParserImpl extends AbstractAdtSegmentParser<AdtPD1AdditionalDemographicSegment, PD1>
        implements Pd1AdditionalDemographicSegmentParser {

    @Override
    public AdtPD1AdditionalDemographicSegment doParse(final PD1 segment, MessageSource messageSource) throws HL7Exception, ApplicationException {
        final AdtPD1AdditionalDemographicSegment result = new AdtPD1AdditionalDemographicSegment();

        //todo test living dependency
        result.setLivingDependencyList(HapiUtils.convertArray(segment.getPd11_LivingDependency(), dataTypeService::getValue));

        result.setLivingArrangement(segment.getPd12_LivingArrangement().getValue());

        result.setPrimaryFacilityList(HapiUtils.convertArray(segment.getPd13_PatientPrimaryFacility(), dataTypeService::createXON));
        result.setPrimaryCareProviderList(HapiUtils.convertArray(segment.getPd14_PatientPrimaryCareProviderNameIDNo(), dataTypeService::createXCN));
        result.setStudentIndicator(segment.getPd15_StudentIndicator().getValue());
        result.setHandicap(segment.getPd16_Handicap().getValue());
        result.setLivingWill(segment.getPd17_LivingWillCode().getValue());
        result.setOrganDonor(segment.getPd18_OrganDonorCode().getValue());
        result.setSeparateBill(segment.getPd19_SeparateBill().getValue());
        result.setDuplicatePatientList(HapiUtils.convertArray(segment.getPd110_DuplicatePatient(), dataTypeService::createCX));
        result.setPublicityCode(dataTypeService.createCE(segment.getPd111_PublicityCode()));
        result.setProtectionIndicator(segment.getPd112_ProtectionIndicator().getValue());
        return result;
    }
}
