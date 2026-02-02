package com.scnsoft.eldermark.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationResidentDao;
import com.scnsoft.eldermark.duke.MatchResult;
import com.scnsoft.eldermark.duke.matchers.ResidentMatchListener;
import com.scnsoft.eldermark.entity.CareCoordinationResident;
import com.scnsoft.eldermark.entity.PersonAddress;
import com.scnsoft.eldermark.entity.State;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import no.priv.garshol.duke.*;
import no.priv.garshol.duke.comparators.Levenshtein;
import no.priv.garshol.duke.datasources.InMemoryDataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by knetkachou on 1/16/2017.
 */
@Service
public class ResidentMatcherServiceImpl implements ResidentMatcherService {

    @Autowired
    CareCoordinationResidentDao careCoordinationResidentDao;
    @Autowired
    StateService stateService;

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");


//    public boolean findMatchedPatients(PatientDto patient, boolean verbose) {
//        Record residentRecord = createRecord(patient);
//        Configuration config = null;
//        try {
//            config = ConfigLoader.load("classpath:duke-config.xml");
//
//            Processor proc = new Processor(config);//,false);
//            proc.setThreads(2);
////            proc.setPerformanceProfiling(true);
//            AbstractResidentMatchListener matchListener = new ResidentMatchListener(true, verbose, verbose, false,
//                    config.getProperties(),
//                    true);
//            proc.addMatchListener(matchListener);
//
//            InMemoryDataSource dataSource = new InMemoryDataSource();
//            dataSource.add(residentRecord);
//
//            List<DataSource> dataSources = new ArrayList<DataSource>();
//            dataSources.add(dataSource);
//
//            proc.link(dataSources, config.getDataSources(1), 40000);
//
//            proc.close();
//
//            return matchListener.getMatchCount() > 0;
//        } catch (IOException e) {
////            logger.error(e.getMessage(), e);
//            throw new RuntimeException(e);
//        } catch (SAXException e) {
////            logger.error(e.getMessage(), e);
//            throw new RuntimeException(e);
//        }
//    }

//    public MatchResult findMatchedPatients(PatientDto patient,  boolean verbose) {
//        findMatchedPatients(patient,null,verbose);
//    }

    public MatchResult findMatchedPatients(PatientDto patient, boolean verbose) {
        Record residentRecord = createRecord(patient);
        Configuration config;
        try {
            config = ConfigLoader.load("classpath:duke-config.xml");

            Processor proc = new Processor(config);//,false);
            proc.setThreads(2);
//        proc.setPerformanceProfiling(true);
            ResidentMatchListener matchListener = new ResidentMatchListener(true, verbose, verbose, false,
                    config.getProperties(),
                    true);
            proc.addMatchListener(matchListener);

            InMemoryDataSource newDataSource = new InMemoryDataSource();
            newDataSource.add(residentRecord);

            List<DataSource> newDataSources = new ArrayList<DataSource>();
            newDataSources.add(newDataSource);
//
            List<CareCoordinationResident> patients = careCoordinationResidentDao.search(patient);
//
            InMemoryDataSource actualDataSource = new InMemoryDataSource();
            for (CareCoordinationResident foundPatient : patients) {
//                if (organizationId!=null && foundPatient.getFacility().getId().equals(organizationId)) {
                actualDataSource.add(createRecord(foundPatient));
//                }
            }
            List<DataSource> actualDataSources = new ArrayList<DataSource>();
            actualDataSources.add(actualDataSource);

            proc.link(newDataSources, actualDataSources, 40000);


//            List <CareCoordinationResident>patientDtoList = new ArrayList<CareCoordinationResident>(); //todo remove from list instead
//            for (CareCoordinationResident foundPatient: patients) {
//                ResidentRecord foundRecord = new ResidentRecord(foundPatient);
//                double prob = proc.compare(residentRecord,foundRecord);
//                System.out.println(prob);
//                if (prob > 0.96) {
//                    patientDtoList.add(foundPatient);
//                }
//            }

            proc.close();

//            return matchListener.getMatchCount() > 0;
//            return patientDtoList.size()>0;
//            return new int[]{matchListener.getMatchCount(),matchListener.getMaybeMatchCount()};
//            MatchResult.MatchResultType matchResultType;
//            if (matchListener.getMatchCount() > 0) matchResultType =  MatchResult.MatchResultType.MATCH;
//            else if (matchListener.getMaybeMatchCount() > 0) matchResultType =   MatchResult.MatchResultType.MAYBE;
//            else matchResultType =   MatchResult.MatchResultType.NO_MATCH;
//
//            return new MatchResult(convert(matchListener.getMatchedRecords()),convert(matchListener.getProbablyMatchedRecords()),matchResultType);
            return createMatchResult(matchListener, patients);
        } catch (IOException | SAXException e) {
            throw new RuntimeException(e);
        }

    }

    private MatchResult createMatchResult(ResidentMatchListener matchListener, List<CareCoordinationResident> patients) {
        MatchResult matchResult = new MatchResult();
        MatchResult.MatchResultType matchResultType;
        if (matchListener.getMatchCount() > 0) matchResultType = MatchResult.MatchResultType.MATCH;
        else if (matchListener.getMaybeMatchCount() > 0) matchResultType = MatchResult.MatchResultType.MAYBE;
        else matchResultType = MatchResult.MatchResultType.NO_MATCH;
        matchResult.setMatchResultType(matchResultType);

        for (Record record : matchListener.getMatchedRecords()) {
            matchResult.getMatchedRecords().add(findResident(record, patients));
        }
        for (Record record : matchListener.getProbablyMatchedRecords()) {
            matchResult.getProbablyMatchedRecords().add(findResident(record, patients));
        }

        return matchResult;
    }

    private CareCoordinationResident findResident(Record record, List<CareCoordinationResident> patients) {
        for (CareCoordinationResident patient : patients) {
            Long id = Long.parseLong(record.getValue("id"));
            if (patient.getId().equals(id)) {
                return patient;
            }
        }
        return null;
    }

//    private List<PatientDto> convert(List<Record> matchedRecords) {
//        List<PatientDto> patientDtoList = new ArrayList<PatientDto>();
//        for (Record record:matchedRecords){
//            final PatientDto patient = new PatientDto();
//
//            patient.setId(Long.parseLong(record.getValue("id")));
//            patient.setFirstName(record.getValue("id"));
//            patient.setLastName(record.getValue("id"));
//            try {
//                patient.setBirthDate(df.parse(record.getValue("id")));
//            } catch (ParseException e) {
//                //logger.e //TODO
//            }
//            patient.setGender(record.getValue("gender"));
//
//            patient.setSsn(record.getValue("ssn"));
//
//            final AddressDto addressDto = new AddressDto();
//            addressDto.setState(CareCoordinationUtils.createKeyValueDto(stateService.findByAbbr(record.getValue("addrState").toUpperCase())));
//            addressDto.setCity(record.getValue("addrCity"));
//            addressDto.setStreet(record.getValue("addrStreet"));
//            addressDto.setZip(record.getValue("addrZip"));
//
//            patient.setAddress(addressDto);
//
//            patientDtoList.add(patient);
//        }
//        return patientDtoList;
//    }

//    private List<DataSource> createDataSource () {
//
//    }

//    private PatientDto convert(CareCoordinationResident resident) {
//        final PatientDto result = new PatientDto();
//
//        result.setId(resident.getId());
//        result.setFirstName(resident.getFirstName());
//        result.setLastName(resident.getLastName());
//        result.setBirthDate(resident.getBirthDate());
//
//        if (resident.getGender() != null) {
//                result.setGender(resident.getGender().getDisplayName());
//        }
//            result.setSsn(resident.getSocialSecurity());
//
//        if (!CollectionUtils.isEmpty(resident.getPerson().getAddresses())) {
//            result.setAddress(toDto(resident.getPerson().getAddresses().get(0)));
//        }
//        return result;
//    }
//
//    private AddressDto toDto(PersonAddress address) {
//        final AddressDto result = new AddressDto();
//        if (!StringUtils.isEmpty(address.getState())) {
//            result.setState(CareCoordinationUtils.createKeyValueDto(stateService.findByAbbr(address.getState())));
//        }
//        result.setCity(address.getCity());
//        result.setStreet(address.getStreetAddress());
//        result.setZip(address.getPostalCode());
//
//        return result;
//
//    }


    private RecordImpl createRecord(PatientDto resident) {
        RecordImpl record = new RecordImpl();
        if (resident.getId() != null) {
            record.addValue("id", resident.getId().toString());
        } else {
            record.addValue("id", UUID.randomUUID().toString());
        }
        record.addValue("firstName", resident.getFirstName().toLowerCase().trim());
        record.addValue("middleName", resident.getMiddleName() != null ? resident.getMiddleName().toLowerCase().trim(): null);
        record.addValue("lastName", resident.getLastName().toLowerCase().trim());
        if (resident.getBirthDate() != null) {
            record.addValue("birthDate", df.format(resident.getBirthDate()));
        }
        record.addValue("ssn", resident.getSsn().replace("-", "").replace(" ", "").trim());
        if (StringUtils.isNotBlank(resident.getGender())) {
            record.addValue("gender", resident.getGender().trim());
        }
        AddressDto address = resident.getAddress();
        if (address != null) {
            if (StringUtils.isNotBlank(address.getStreet())) {
                record.addValue("addrStreet", address.getStreet().replaceAll("[^a-z A-Z0-9]", "").toLowerCase().trim());
            }
            if (StringUtils.isNotBlank(address.getCity())) {
                record.addValue("addrCity", address.getCity().toLowerCase().trim());
            }

            if (address.getState() != null) {
                //            State state = stateService.get(address.getState().getId());
                //addValue("addrState", ((StateDto)address.getState()).getAbbr().toLowerCase());
                State state = stateService.get(address.getState().getId());
                record.addValue("addrState", state.getAbbr().toLowerCase().trim());
            }
            //        else {
            //            record.addValue("addrState","");
            //        }
            if (StringUtils.isNotBlank(address.getZip())) {
                record.addValue("addrZip", address.getZip().trim());
            }
        }
//        record.addValue("email", resident.getEmail());
//        record.addValue("phone", resident.getPhone());
        return record;
    }


    private RecordImpl createRecord(CareCoordinationResident resident) {
        RecordImpl record = new RecordImpl();
        record.addValue("id", resident.getId().toString());
        record.addValue("firstName", resident.getFirstName().toLowerCase().trim());
        record.addValue("middleName", resident.getMiddleName() != null ? resident.getMiddleName().toLowerCase().trim(): null);
        record.addValue("lastName", resident.getLastName().toLowerCase().trim());

        if (resident.getBirthDate() != null) {
            record.addValue("birthDate", df.format(resident.getBirthDate()));
        }
        record.addValue("ssn", resident.getSocialSecurity().trim());
        if (resident.getGender() != null) {
            record.addValue("gender", resident.getGender().getCode());
        }

        if (!CollectionUtils.isEmpty(resident.getPerson().getAddresses())) {
            PersonAddress address = resident.getPerson().getAddresses().get(0);
//            final AddressDto result = new AddressDto();
//            if (!StringUtils.isEmpty(address.getState())) {
            if (StringUtils.isNotBlank(address.getState())) {
                record.addValue("addrState", address.getState().toLowerCase().trim());
            }
//            }
            if (StringUtils.isNotBlank(address.getStreetAddress())) {
                record.addValue("addrStreet", address.getStreetAddress().replaceAll("[^a-z A-Z0-9]", "").toLowerCase().trim());
            }
            if (StringUtils.isNotBlank(address.getCity())) {
                record.addValue("addrCity", address.getCity().toLowerCase().trim());
            }
            if (StringUtils.isNotBlank(address.getPostalCode())) {
                record.addValue("addrZip", address.getPostalCode().trim());
            }
        }

//        record.addValue("email", PersonService.getPersonTelecomValue(resident.getPerson(), PersonTelecomCode.EMAIL));
//        record.addValue("phone", PersonService.getPersonTelecomValue(resident.getPerson(), PersonTelecomCode.HP));
        return record;
    }

    public List<CareCoordinationResident> findFullMatchedResidents(PatientDto patient, Long organizationId, String dbOid) {
        List<CareCoordinationResident> matchedResidents = new ArrayList<CareCoordinationResident>();
        MatchResult result = findMatchedPatients(patient, false);
        if (!result.getMatchResultType().equals(MatchResult.MatchResultType.NO_MATCH)) {
            List<CareCoordinationResident> matchedPatientList = result.getMatchedRecords();
            matchedPatientList.addAll(result.getProbablyMatchedRecords());

            for (CareCoordinationResident foundPatient : matchedPatientList) {
                if (patient.getSsn().equals(foundPatient.getSocialSecurity()) && matchStrings(patient.getFirstName(), foundPatient.getFirstName(), 1) &&
                        matchStrings(patient.getLastName(), foundPatient.getLastName(), 1) &&
                        (organizationId == null || foundPatient.getFacility().getId().equals(organizationId)) &&
                        (dbOid == null || dbOid.equals(foundPatient.getDatabase().getOid()))
                        ) {
                    matchedResidents.add(foundPatient);
                }
            }
        }
        return matchedResidents;
    }

//    private CareCoordinationResident findFullMatchedResident (PatientDto patient, List<CareCoordinationResident> patientDtoList,Long organizationId, String dbOid) {
//        for (CareCoordinationResident foundPatient : patientDtoList) {
//            if (patient.getSsn().equals(foundPatient.getSocialSecurity()) && matchStrings(patient.getFirstName(), foundPatient.getFirstName(),1) &&
//                    matchStrings(patient.getLastName(), foundPatient.getLastName(),1) &&
//                    (organizationId== null || foundPatient.getFacility().getId().equals(organizationId)) &&
//                    (dbOid== null || foundPatient.getDatabase().getOid().equals(dbOid))
//                    ) {
//                return foundPatient;
//            }
//        }
//        return null;
//    }

    public boolean matchStrings(String str1, String str2, int dist) {
        return Levenshtein.distance(str1, str2) <= dist;
    }

    public void setStateService(StateService stateService) {
        this.stateService = stateService;
    }


    public void setCareCoordinationResidentDao(CareCoordinationResidentDao careCoordinationResidentDao) {
        this.careCoordinationResidentDao = careCoordinationResidentDao;
    }
}
