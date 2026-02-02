package com.scnsoft.eldermark.adaptermpicomponent;

import com.scnsoft.eldermark.ws.api.residents.*;
import gov.hhs.fha.nhinc.mpi.adapter.component.PatientDbChecker;
import gov.hhs.fha.nhinc.mpi.adapter.component.hl7parsers.HL7DbParser201305;
import gov.hhs.fha.nhinc.mpi.adapter.component.hl7parsers.HL7Parser201306;
import gov.hhs.fha.nhinc.mpilib.*;
import gov.hhs.fha.nhinc.patientdb.model.Address;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hl7.v3.PRPAIN201305UV02;
import org.hl7.v3.PRPAIN201306UV02;
import org.hl7.v3.PRPAMT201306UV02ParameterList;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

public class EldermparkPatientCheckerImpl extends PatientDbChecker {
    private Logger logger = Logger.getLogger(EldermparkPatientCheckerImpl.class);

    @Override
    public PRPAIN201306UV02 findPatient(PRPAIN201305UV02 query) {
        logger.trace("Entering PatientDbChecker.FindPatient method...");

        PRPAMT201306UV02ParameterList queryParams = HL7DbParser201305.extractHL7QueryParamsFromMessage(query);

        Patients patients = null;

        if (queryParams == null) {
            logger.error("no query parameters were supplied");
        } else {
            //Patient sourcePatient = HL7Parser201305.extractMpiPatientFromQueryParams(queryParams);

            //Using HL7DbParser201305 instead of HL7Parser201305 because:
            //1. HL7Parser201305 don't parse middlename
            //2. There are some questions about parsing date, HL7Parser201305 parse to String
            gov.hhs.fha.nhinc.patientdb.model.Patient sourcePatient = HL7DbParser201305.extractMpiPatientFromQueryParams(queryParams);

            ResidentsEndpointImplService residentsEndpointImplService = new ResidentsEndpointImplService();
            ResidentsEndpoint port = residentsEndpointImplService.getResidentsEndpointImplPort();

            ResidentFilter residentFilter = createResidentFilter(sourcePatient);

            List<Resident> residents = null;

            try {
                residents = port.searchResidents(residentFilter);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getMessage(), e);
            }

            patients = createPatients(residents);

            logger.trace("Custom patient MPI invoked");
        }
        PRPAIN201306UV02 result = HL7Parser201306.buildMessageFromMpiPatient(patients, query);
        logger.trace("Exiting EldermparkPatientCheckerImpl.findPatient method...");

        return result;
    }

    private Patients createPatients(List<Resident> residentList) {

        Patients patients = new Patients();

        for (Resident resident : residentList) {
            Patient patient = new Patient();

            PersonNames personNames = new PersonNames();
            PersonName personname = new PersonName();
            personname.setFirstName(resident.getFirstName());
            personname.setLastName(resident.getLastName());
            personname.setMiddleName(resident.getMiddleName());
            personNames.add(personname);

            Addresses addresses = new Addresses();
            gov.hhs.fha.nhinc.mpilib.Address address = new gov.hhs.fha.nhinc.mpilib.Address();
            address.setCity(resident.getCity());
            address.setState(resident.getState());
            address.setStreet1(resident.getStreetAddress());
            address.setZip(resident.getPostalCode());
            addresses.add(address);

            Identifiers identifiers = new Identifiers();
            gov.hhs.fha.nhinc.mpilib.Identifier identifier = new gov.hhs.fha.nhinc.mpilib.Identifier();
            identifier.setId("" + resident.getId());
            //identifier.setOrganizationId(resident.getSourceOrganizationId());
            identifier.setOrganizationId("2.16.840.1.113883.3.6492");
            identifiers.add(identifier);

            gov.hhs.fha.nhinc.mpilib.Identifier hashKey = new gov.hhs.fha.nhinc.mpilib.Identifier();
            hashKey.setId(resident.getHashKey());
            identifiers.add(hashKey);

            PhoneNumbers phonenumbers = new PhoneNumbers();
            PhoneNumber phonenumber = new PhoneNumber();
            phonenumber.setPhoneNumber(resident.getPhone());
            phonenumbers.add(phonenumber);

            patient.setGender(null);
            if(resident.getGender() != null) {
                switch(resident.getGender()) {
                    case FEMALE: patient.setGender("F"); break;
                    case MALE: patient.setGender("M"); break;
                    case UNDIFFERENTIATED: patient.setGender("UN"); break;
                }
            }

            if (resident.getDateOfBirth() != null) {
                DateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");
                String dobStr = dateFormatter.format(resident.getDateOfBirth().toGregorianCalendar().getTime());
                patient.setDateOfBirth(dobStr);
            }

            patient.setNames(personNames);

            patient.setAddresses(addresses);
            patient.setIdentifiers(identifiers);
            patient.setSSN(resident.getSsn());
            patient.setPhoneNumbers(phonenumbers);

            patients.add(patient);
        }
        return patients;
    }

    private ResidentFilter createResidentFilter(gov.hhs.fha.nhinc.patientdb.model.Patient patient) {
        ResidentFilter filter = new ResidentFilter();

        filter.setFirstName(patient.getPersonnames().get(0).getFirstName());
        filter.setLastName(patient.getPersonnames().get(0).getLastName());
        filter.setMiddleName(patient.getPersonnames().get(0).getMiddleName());

        Gender gender = null;
        if ("M".equalsIgnoreCase(patient.getGender())) {
            gender = Gender.MALE;
        } else if ("F".equalsIgnoreCase(patient.getGender())) {
            gender = Gender.FEMALE;
        } else if ("UN".equalsIgnoreCase(patient.getGender())) {
            gender = Gender.UNDIFFERENTIATED;
        }
        filter.setGender(gender);

        if (patient.getDateOfBirth() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(patient.getDateOfBirth());
            XMLGregorianCalendar dateOfBirth = null;
            try {
                dateOfBirth = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } catch (DatatypeConfigurationException e) {
                logger.error(e.getMessage(), e);
            }

            filter.setDateOfBirth(dateOfBirth);
        }


        if (patient.getPhonenumbers() != null && patient.getPhonenumbers().size() > 0) {
            filter.setPhone(patient.getPhonenumbers().get(0).getValue());
        }

        if (patient.getAddresses() != null && patient.getAddresses().size() > 0) {
            Address address = patient.getAddresses().get(0);
            filter.setCity(address.getCity());
            filter.setStreet(address.getStreet1());
            filter.setState(address.getState());
            filter.setPostalCode(address.getPostal());
        }

        String ssn = patient.getSsn();
        if (StringUtils.isNotBlank(ssn) && ssn.length() >= 4) {
            String lastFourDigitsOfSsn = ssn.substring(ssn.length() - 4, ssn.length());
            filter.setLastFourDigitsOfSsn(lastFourDigitsOfSsn);
        }
        return filter;
    }
}
