package com.scnsoft.eldermark.hl7v2.parse.v251.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v251.segment.PID;
import com.scnsoft.eldermark.entity.xds.hl7table.*;
import com.scnsoft.eldermark.entity.xds.segment.PIDPatientIdentificationSegment;
import com.scnsoft.eldermark.hl7v2.parse.v251.segment.PidSegmentParser;
import com.scnsoft.eldermark.hl7v2.source.MessageSource;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class PidSegmentParserImpl extends AbstractAdtSegmentParser<PIDPatientIdentificationSegment, PID>
        implements PidSegmentParser {

    @Override
    protected PIDPatientIdentificationSegment doParse(PID segment, MessageSource messageSource) throws HL7Exception, ApplicationException {
        final PIDPatientIdentificationSegment pid = new PIDPatientIdentificationSegment();
        pid.setPatientID(dataTypeService.createCX(segment.getPid2_PatientID()));
        pid.setPatientIdentifiers(dataTypeService.createCXList(segment.getPid3_PatientIdentifierList()));
        pid.setPatientNames(dataTypeService.createXPNList(segment.getPid5_PatientName()));
        pid.setMothersMaidenNames(dataTypeService.createXPNList(segment.getPid6_MotherSMaidenName()));
        pid.setDateTimeOfBirth(LocalDate.ofInstant(dataTypeService.convertTS(segment.getPid7_DateTimeOfBirth()), ZoneId.of("UTC")));
        pid.setAdministrativeSex(dataTypeService.createIS(segment.getPid8_AdministrativeSex(), HL7CodeTable0001AdministrativeSex.class));
        pid.setPatientAliases(dataTypeService.createXPNList(segment.getPid9_PatientAlias()));
        pid.setRaces(dataTypeService.createCEListWithCodedValues(segment.getPid10_Race(), HL7CodeTable0005Race.class));
        pid.setPatientAddresses(dataTypeService.createXADList(segment.getPid11_PatientAddress()));
        pid.setPhoneNumbersHome(dataTypeService.createXTNList(segment.getPid13_PhoneNumberHome()));
        pid.setPhoneNumbersBusiness(dataTypeService.createXTNList(segment.getPid14_PhoneNumberBusiness()));
        pid.setPrimaryLanguage(dataTypeService.createCE(segment.getPid15_PrimaryLanguage()));
        pid.setMaritalStatus(dataTypeService.createCEWithCodedValues(segment.getPid16_MaritalStatus(), HL7CodeTable0002MaritalStatus.class));
        pid.setReligion(dataTypeService.createCEWithCodedValues(segment.getPid17_Religion(), HL7CodeTable0006Religion.class));
        pid.setPatientAccountNumber(dataTypeService.createCX(segment.getPid18_PatientAccountNumber()));
        pid.setSsnNumberPatient(dataTypeService.getValue(segment.getPid19_SSNNumberPatient()));
        pid.setDriversLicenseNumber(dataTypeService.createDLN(segment.getPid20_DriverSLicenseNumberPatient()));
        pid.setMotherIdentifiers(dataTypeService.createCXList(segment.getPid21_MotherSIdentifier()));
        pid.setEthnicGroups(dataTypeService.createCEListWithCodedValues(segment.getPid22_EthnicGroup(), HL7CodeTable0189EthnicGroup.class));
        pid.setBirthPlace(dataTypeService.getValue(segment.getPid23_BirthPlace()));
        pid.setBirthOrder(dataTypeService.convertNM(segment.getPid25_BirthOrder()));
        pid.setCitizenships(dataTypeService.createCEList(segment.getPid26_Citizenship()));
        pid.setVeteransMilitaryStatus(dataTypeService.createCE(segment.getPid27_VeteransMilitaryStatus()));
        pid.setNationality(dataTypeService.createCE(segment.getPid28_Nationality()));
        pid.setPatientDeathDateAndTime(dataTypeService.convertTS(segment.getPid29_PatientDeathDateAndTime()));
        pid.setPatientDeathIndicator(dataTypeService.createID(segment.getPid30_PatientDeathIndicator(), HL7CodeTable0136YesNoIndicator.class));
        pid.setSpeciesCode(dataTypeService.createCE(segment.getPid35_SpeciesCode()));
        return pid;
    }
}
