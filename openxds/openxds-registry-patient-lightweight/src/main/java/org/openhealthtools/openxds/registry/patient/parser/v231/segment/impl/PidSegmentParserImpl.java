package org.openhealthtools.openxds.registry.patient.parser.v231.segment.impl;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.app.ApplicationException;
import ca.uhn.hl7v2.model.v231.segment.PID;
import org.openhealthtools.openxds.entity.hl7table.*;
import org.openhealthtools.openxds.entity.segment.PIDPatientIdentificationSegment;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;
import org.openhealthtools.openxds.registry.patient.parser.datatype.EmptyHL7Field231Service;
import org.openhealthtools.openxds.registry.patient.parser.v231.segment.PidSegmentParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PidSegmentParserImpl extends AbstractAdtSegmentParser<PIDPatientIdentificationSegment, PID>
        implements PidSegmentParser {

    @Autowired
    private DataTypeService dataTypeService;

    @Autowired
    private EmptyHL7Field231Service emptyHL7Field231Service;


    @Override
    public boolean isHl7SegmentEmpty(final PID hl7Segment) {
        return hl7Segment == null || getEmptyHL7Field231Service().isCXArrayEmpty(hl7Segment.getPatientIdentifierList())
                || getEmptyHL7Field231Service().isXPNArrayEmpty(hl7Segment.getPatientName());
    }

    @Override
    protected PIDPatientIdentificationSegment doParse(PID segment) throws HL7Exception, ApplicationException {
        if (isHl7SegmentEmpty(segment)) {
            return null;
        }
        final PIDPatientIdentificationSegment pid = new PIDPatientIdentificationSegment();
        pid.setPatientIdentifiers(getDataTypeService().createCXList(segment.getPatientIdentifierList()));
        pid.setPatientNames(getDataTypeService().createXPNList(segment.getPatientName()));
        pid.setMothersMaidenNames(getDataTypeService().createXPNList(segment.getMotherSMaidenName()));
        pid.setAdministrativeSex(getDataTypeService().createIS(segment.getSex(), HL7CodeTable0001AdministrativeSex.class));
        pid.setPatientAliases(getDataTypeService().createXPNList(segment.getPatientAlias()));
        pid.setRaces(getDataTypeService().createCEListWithCodedValues(segment.getRace(), HL7CodeTable0005Race.class));
        pid.setPatientAddresses(getDataTypeService().createXADList(segment.getPatientAddress()));
        pid.setPhoneNumbersHome(getDataTypeService().createXTNList(segment.getPhoneNumberHome()));
        pid.setPhoneNumbersBusiness(getDataTypeService().createXTNList(segment.getPhoneNumberBusiness()));
        pid.setPrimaryLanguage(getDataTypeService().createCE(segment.getPrimaryLanguage()));
        pid.setMaritalStatus(getDataTypeService().createCEWithCodedValues(segment.getMaritalStatus(), HL7CodeTable0002MaritalStatus.class));
        pid.setReligion(getDataTypeService().createCEWithCodedValues(segment.getReligion(), HL7CodeTable0006Religion.class));
        pid.setPatientAccountNumber(getDataTypeService().createCX(segment.getPatientAccountNumber()));
        pid.setSsnNumberPatient(getDataTypeService().getValue(segment.getSSNNumberPatient()));
        pid.setDriversLicenseNumber(getDataTypeService().createDLN(segment.getDriverSLicenseNumberPatient()));
        pid.setMothersIdentifiers(getDataTypeService().createCXList(segment.getMotherSIdentifier()));
        pid.setEthnicGroups(getDataTypeService().createCEListWithCodedValues(segment.getEthnicGroup(), HL7CodeTable0189EthnicGroup.class));
        pid.setBirthPlace(getDataTypeService().getValue(segment.getBirthPlace()));
        pid.setDateTimeOfBirth(getDataTypeService().convertTsToDate(segment.getDateTimeOfBirth()));
        pid.setBirthOrder(getDataTypeService().convertNM(segment.getBirthOrder()));
        pid.setCitizenships(getDataTypeService().createCEList(segment.getCitizenship()));
        pid.setVeteransMilitaryStatus(getDataTypeService().createCE(segment.getVeteransMilitaryStatus()));
        pid.setNationality(getDataTypeService().createCE(segment.getNationality()));
        pid.setPatientDeathDateAndTime(getDataTypeService().convertTsToDate(segment.getPatientDeathDateAndTime()));
        pid.setPatientDeathIndicator(getDataTypeService().createID(segment.getPatientDeathIndicator(), HL7CodeTable0136YesNoIndicator.class));
        return pid;
    }

    public DataTypeService getDataTypeService() {
        return dataTypeService;
    }

    public void setDataTypeService(final DataTypeService dataTypeService) {
        this.dataTypeService = dataTypeService;
    }

    public EmptyHL7Field231Service getEmptyHL7Field231Service() {
        return emptyHL7Field231Service;
    }

    public void setEmptyHL7Field231Service(EmptyHL7Field231Service emptyHL7Field231Service) {
        this.emptyHL7Field231Service = emptyHL7Field231Service;
    }
}
