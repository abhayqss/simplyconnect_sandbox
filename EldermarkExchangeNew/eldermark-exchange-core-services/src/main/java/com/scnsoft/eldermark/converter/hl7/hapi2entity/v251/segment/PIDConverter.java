package com.scnsoft.eldermark.converter.hl7.hapi2entity.v251.segment;

import ca.uhn.hl7v2.model.v251.segment.PID;
import com.scnsoft.eldermark.entity.xds.hl7table.*;
import com.scnsoft.eldermark.entity.xds.segment.PIDPatientIdentificationSegment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class PIDConverter extends HL7SegmentConverter<PID, PIDPatientIdentificationSegment> {

    @Override
    protected PIDPatientIdentificationSegment doConvert(PID source) {
        var pid = new PIDPatientIdentificationSegment();
        pid.setPatientID(dataTypeService.createCX(source.getPid2_PatientID()));
        pid.setPatientIdentifiers(dataTypeService.createCXList(source.getPid3_PatientIdentifierList()));
        pid.setPatientNames(dataTypeService.createXPNList(source.getPid5_PatientName()));
        pid.setMothersMaidenNames(dataTypeService.createXPNList(source.getPid6_MotherSMaidenName()));
        pid.setDateTimeOfBirth(LocalDate.ofInstant(dataTypeService.convertTS(source.getPid7_DateTimeOfBirth()), ZoneId.of("UTC")));
        pid.setAdministrativeSex(dataTypeService.createIS(source.getPid8_AdministrativeSex(), HL7CodeTable0001AdministrativeSex.class));
        pid.setPatientAliases(dataTypeService.createXPNList(source.getPid9_PatientAlias()));
        pid.setRaces(dataTypeService.createCEListWithCodedValues(source.getPid10_Race(), HL7CodeTable0005Race.class));
        pid.setPatientAddresses(dataTypeService.createXADList(source.getPid11_PatientAddress()));
        pid.setPhoneNumbersHome(dataTypeService.createXTNList(source.getPid13_PhoneNumberHome()));
        pid.setPhoneNumbersBusiness(dataTypeService.createXTNList(source.getPid14_PhoneNumberBusiness()));
        pid.setPrimaryLanguage(dataTypeService.createCE(source.getPid15_PrimaryLanguage()));
        pid.setMaritalStatus(dataTypeService.createCEWithCodedValues(source.getPid16_MaritalStatus(), HL7CodeTable0002MaritalStatus.class));
        pid.setReligion(dataTypeService.createCEWithCodedValues(source.getPid17_Religion(), HL7CodeTable0006Religion.class));
        pid.setPatientAccountNumber(dataTypeService.createCX(source.getPid18_PatientAccountNumber()));
        pid.setSsnNumberPatient(dataTypeService.getValue(source.getPid19_SSNNumberPatient()));
        pid.setDriversLicenseNumber(dataTypeService.createDLN(source.getPid20_DriverSLicenseNumberPatient()));
        pid.setMotherIdentifiers(dataTypeService.createCXList(source.getPid21_MotherSIdentifier()));
        pid.setEthnicGroups(dataTypeService.createCEListWithCodedValues(source.getPid22_EthnicGroup(), HL7CodeTable0189EthnicGroup.class));
        pid.setBirthPlace(dataTypeService.getValue(source.getPid23_BirthPlace()));
        pid.setBirthOrder(dataTypeService.convertNM(source.getPid25_BirthOrder()));
        pid.setCitizenships(dataTypeService.createCEList(source.getPid26_Citizenship()));
        pid.setVeteransMilitaryStatus(dataTypeService.createCE(source.getPid27_VeteransMilitaryStatus()));
        pid.setNationality(dataTypeService.createCE(source.getPid28_Nationality()));
        pid.setPatientDeathDateAndTime(dataTypeService.convertTS(source.getPid29_PatientDeathDateAndTime()));
        pid.setPatientDeathIndicator(dataTypeService.createID(source.getPid30_PatientDeathIndicator(), HL7CodeTable0136YesNoIndicator.class));
        pid.setSpeciesCode(dataTypeService.createCE(source.getPid35_SpeciesCode()));
        return pid;
    }
}
