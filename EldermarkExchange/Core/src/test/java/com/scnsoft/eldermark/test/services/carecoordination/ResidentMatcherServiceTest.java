package com.scnsoft.eldermark.test.services.carecoordination;

import com.scnsoft.eldermark.dao.carecoordination.CareCoordinationResidentDao;
import com.scnsoft.eldermark.duke.MatchResult;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.services.StateService;
import com.scnsoft.eldermark.services.carecoordination.*;
import com.scnsoft.eldermark.shared.carecoordination.AddressDto;
import com.scnsoft.eldermark.shared.carecoordination.PatientDto;
import com.scnsoft.eldermark.shared.carecoordination.utils.CareCoordinationUtils;
import com.scnsoft.eldermark.test.mock.StateServiceMock;
import org.apache.commons.lang3.StringUtils;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by knetkachou on 12/28/2016.
 */
public class ResidentMatcherServiceTest {


    ResidentMatcherServiceImpl residentMatcherService = new ResidentMatcherServiceImpl();
    CareCoordinationResidentDao careCoordinationResidentDao;
    StateService stateService;

    List<CareCoordinationResident> residents = new ArrayList<CareCoordinationResident>();
    List<PatientDto> matchedResidents = new ArrayList<PatientDto>();
    List<PatientDto> unMatchedResidents = new ArrayList<PatientDto>();
    List<PatientDto> maybeMatchedResidents = new ArrayList<PatientDto>();


    private SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    private static final int ID_IND = 0;
    private static final int SSN_IND = 1;
    private static final int FIRST_NAME_IND = 2;
    private static final int LAST_NAME_IND = 3;
    private static final int DOB_IND = 4;
    private static final int GENDER_IND = 5;
    private static final int STREET_IND = 6;
    private static final int CITY_IND = 7;
    private static final int STATE_IND = 8;
    private static final int ZIP_IND = 9;
    private static final int MATCH_IND = 10;

    private static final String FILE_NAME = "/matching/patientmatch%d.csv";
    private static final int NUMBER_OF_FILES = 2;

    @Before
    public void initObjects() {
        stateService = new StateServiceMock();//EasyMock.createMock(StateService.class);
        careCoordinationResidentDao = EasyMock.createMock(CareCoordinationResidentDao.class);
        residentMatcherService.setCareCoordinationResidentDao(careCoordinationResidentDao);
        residentMatcherService.setStateService(stateService);
        EasyMock.expect(careCoordinationResidentDao.search(EasyMock.isA(PatientDto.class))).andReturn(residents).atLeastOnce();
        EasyMock.replay(careCoordinationResidentDao);
//        initStateService(stateService);
    }

    @Test
    public void testFindMatchedPatients() throws Exception {
        for (int i = 1; i <= NUMBER_OF_FILES; i++) {
            parseFile(String.format(FILE_NAME, i));
        }
        for (PatientDto patientDto : matchedResidents) {
            assertEquals("Records should match " + printData(patientDto),
                    MatchResult.MatchResultType.MATCH, residentMatcherService.findMatchedPatients(patientDto, true).getMatchResultType());
        }
        for (PatientDto patientDto : unMatchedResidents) {
            assertEquals("Records should NOT match " + printData(patientDto),
                    MatchResult.MatchResultType.NO_MATCH, residentMatcherService.findMatchedPatients(patientDto, true).getMatchResultType());
        }
        for (PatientDto patientDto : maybeMatchedResidents) {
            assertEquals("Records should MAY BE match " + printData(patientDto),
                    MatchResult.MatchResultType.MAYBE, residentMatcherService.findMatchedPatients(patientDto, true).getMatchResultType());
        }
    }


    private void createPatient(List<String> data) throws Exception {
//        boolean log = true;
//        CareCoordinationResidentServiceImpl service = new CareCoordinationResidentServiceImpl();
        PatientDto patientDto = new PatientDto();
        patientDto.setId(Long.parseLong(data.get(ID_IND)));
        patientDto.setFirstName(data.get(FIRST_NAME_IND));
        patientDto.setLastName(data.get(LAST_NAME_IND));
        String birthString = data.get(DOB_IND);
        if (StringUtils.isNotBlank(birthString)) {
            patientDto.setBirthDate(sdf.parse(birthString));
        }
//                patientDto.setEmail("asdfasf@adsfas.se");
//                patientDto.setPhone("23423423");
        patientDto.setSsn(data.get(SSN_IND));
        patientDto.setGender(data.get(GENDER_IND));

        AddressDto addressDto = new AddressDto();
        addressDto.setStreet(data.get(STREET_IND));
        addressDto.setCity(data.get(CITY_IND));
        State state = stateService.findByAbbr(data.get(STATE_IND));
        if (state != null) {
            addressDto.setState(CareCoordinationUtils.createKeyValueDto(state));
        }
        addressDto.setZip(data.get(ZIP_IND));
        patientDto.setAddress(addressDto);
//        long startTime = System.currentTimeMillis();

        if (data.get(MATCH_IND).equalsIgnoreCase("Yes")) {
//                    assertTrue("Records should match " + printData(data),
//                            residentMatcherService.findMatchedPatients(patientDto, true));
            matchedResidents.add(patientDto);
        } else if (data.get(MATCH_IND).equalsIgnoreCase("No")) {
//                    assertFalse("Records should NOT match " + printData(data),
//                            residentMatcherService.findMatchedPatients(patientDto, true));
            unMatchedResidents.add(patientDto);
        }
        else {
            maybeMatchedResidents.add(patientDto);
        }

//                long stopTime1 = System.currentTimeMillis();
//                long elapsedTime1 = stopTime1 - startTime;
//                System.out.println(elapsedTime1 + " ms");


//                assertFalse("Wrong match found", residentMatcherService.findMatchedPatients(patientDto, log));
//                long stopTime = System.currentTimeMillis();
//                long elapsedTime = stopTime - stopTime1;
//                System.out.println(elapsedTime + " ms");
    }

    private String printData(List<String> data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String dt : data) {
            stringBuilder.append(dt).append(",");
        }
        return stringBuilder.toString();
    }

    private String printData(PatientDto patientDto) {
        StringBuilder stringBuilder = new StringBuilder(patientDto.getId() + ",");
        stringBuilder.append(patientDto.getSsn()).append(",");
        stringBuilder.append(patientDto.getFirstName()).append(",");
        stringBuilder.append(patientDto.getLastName()).append(",");
        if (patientDto.getBirthDate() != null) {
            stringBuilder.append(sdf.format(patientDto.getBirthDate())).append(",");
        }
        stringBuilder.append(patientDto.getGender()).append(",");
        stringBuilder.append(patientDto.getAddress().getStreet()).append(",");
        stringBuilder.append(patientDto.getAddress().getCity()).append(",");
        if (patientDto.getAddress().getState() != null)
            stringBuilder.append(patientDto.getAddress().getState().getLabel()).append(",");
        stringBuilder.append(patientDto.getAddress().getZip()).append(",");

        return stringBuilder.toString();
    }

    private void createDefaultPatient(List<String> data) throws ParseException {
//        List<CareCoordinationResident> patients = new ArrayList<CareCoordinationResident>();
        CareCoordinationResident resident = new CareCoordinationResident();
        resident.setId(Long.parseLong(data.get(ID_IND)));
        resident.setFirstName(data.get(FIRST_NAME_IND));
        resident.setLastName(data.get(LAST_NAME_IND));
        CcdCode genderCode = new CcdCode();
        genderCode.setCode(data.get(GENDER_IND));
        resident.setGender(genderCode);
        resident.setSocialSecurity(data.get(SSN_IND));

        resident.setBirthDate(sdf.parse(data.get(DOB_IND)));


        final Person person = new Person();
//        CareCoordinationConstants.setLegacyId(person);
        resident.setPerson(person);


        //Fill In Name table Data

//        Name name = new Name();
//        name.setPerson(person);
//
//        if (person.getNames() == null) person.setNames(new ArrayList<Name>());
//        person.getNames().add(name);
//
//        name.setFamily("Jonen");
//        name.setGiven("Tommy");

        final PersonAddress personAddress = new PersonAddress();
        personAddress.setPerson(person);
        personAddress.setState(data.get(STATE_IND));
        personAddress.setStreetAddress(data.get(STREET_IND));
        personAddress.setCity(data.get(CITY_IND));
        personAddress.setPostalCode(data.get(ZIP_IND));

        person.setAddresses(new ArrayList<PersonAddress>());
        person.getAddresses().add(personAddress);


//        final PersonAddress personAddress = addressService.createPersonAddress(organization.getDatabase(), person, address);
//        CareCoordinationConstants.setLegacyIdFromParent(personAddress, resident.getPerson());
//        person.getAddresses().add(personAddress);
//        addressCreated = true;
        residents.add(resident);
    }


    private void parseFile(String filename) throws Exception {
        //String csvFile = "classpath:matching\\patientmatch.csv";
//        String csvFile = "patientmatch.csv";
        String csvFile = getClass().getResource(filename).getFile();
        BufferedReader br = null;
        String line;
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {

                String[] data = line.split(cvsSplitBy);

//                List<String> dataList = new ArrayList<String>();
//                for (String dt : data) {
//                    if (dt.equals("")) {
//                        dt = null;
//                    }
//                    dataList.add(dt);
//                }

                if (firstLine) {
                    createDefaultPatient(new ArrayList<String>(Arrays.asList(data)));
                    firstLine = false;
                } else {
                    createPatient(new ArrayList<String>(Arrays.asList(data)));
                }
//                    for (String word : data) {
//                        System.out.print(word + ";");
//                    }
//                    System.out.println("");
//                }
//                titleLine = false;
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }


//    private void initStateService(StateService stateService) {
//        EasyMock.expect(stateService.getAbbr(1L)).andReturn("AL");
//        EasyMock.expect(stateService.getAbbr(2L)).andReturn("AZ");
//        EasyMock.expect(stateService.getAbbr(3L)).andReturn("CA");
//        EasyMock.expect(stateService.getAbbr(4L)).andReturn("CT");
//        EasyMock.expect(stateService.getAbbr(5L)).andReturn("FL");
//        EasyMock.expect(stateService.getAbbr(6L)).andReturn("GA");
//        EasyMock.expect(stateService.getAbbr(7L)).andReturn("ID");
//        EasyMock.expect(stateService.getAbbr(8L)).andReturn("IN");
//        EasyMock.expect(stateService.getAbbr(9L)).andReturn("KS");
//        EasyMock.expect(stateService.getAbbr(10L)).andReturn("NH");
//        EasyMock.expect(stateService.getAbbr(11L)).andReturn("NM");
//        EasyMock.expect(stateService.getAbbr(12L)).andReturn("ND");
//        EasyMock.expect(stateService.getAbbr(13L)).andReturn("OK");
//        EasyMock.expect(stateService.getAbbr(14L)).andReturn("OR");
//        EasyMock.expect(stateService.getAbbr(15L)).andReturn("RI");
//        EasyMock.expect(stateService.getAbbr(16L)).andReturn("SD");
//        EasyMock.expect(stateService.getAbbr(17L)).andReturn("TN");
//        EasyMock.expect(stateService.getAbbr(18L)).andReturn("UT");
//        EasyMock.expect(stateService.getAbbr(19L)).andReturn("VA");
//        EasyMock.expect(stateService.getAbbr(20L)).andReturn("WV");
//        EasyMock.expect(stateService.getAbbr(21L)).andReturn("WY");
//        EasyMock.expect(stateService.getAbbr(22L)).andReturn("ME");
//        EasyMock.expect(stateService.getAbbr(23L)).andReturn("MA");
//        EasyMock.expect(stateService.getAbbr(24L)).andReturn("MN");
//        EasyMock.expect(stateService.getAbbr(25L)).andReturn("MO");
//        EasyMock.expect(stateService.getAbbr(26L)).andReturn("NE");
//        EasyMock.expect(stateService.getAbbr(27L)).andReturn("NV");
//        EasyMock.expect(stateService.getAbbr(28L)).andReturn("NJ");
//        EasyMock.expect(stateService.getAbbr(29L)).andReturn("NY");
//        EasyMock.expect(stateService.getAbbr(30L)).andReturn("NC");
//        EasyMock.expect(stateService.getAbbr(31L)).andReturn("OH");
//        EasyMock.expect(stateService.getAbbr(32L)).andReturn("PA");
//        EasyMock.expect(stateService.getAbbr(33L)).andReturn("SC");
//        EasyMock.expect(stateService.getAbbr(34L)).andReturn("VT");
//        EasyMock.expect(stateService.getAbbr(35L)).andReturn("WA");
//        EasyMock.expect(stateService.getAbbr(36L)).andReturn("WI");
//        EasyMock.expect(stateService.getAbbr(37L)).andReturn("AK");
//        EasyMock.expect(stateService.getAbbr(38L)).andReturn("AR");
//        EasyMock.expect(stateService.getAbbr(39L)).andReturn("CO");
//        EasyMock.expect(stateService.getAbbr(40L)).andReturn("DE");
//        EasyMock.expect(stateService.getAbbr(41L)).andReturn("HI");
//        EasyMock.expect(stateService.getAbbr(42L)).andReturn("IL");
//        EasyMock.expect(stateService.getAbbr(43L)).andReturn("IA");
//        EasyMock.expect(stateService.getAbbr(44L)).andReturn("KY");
//        EasyMock.expect(stateService.getAbbr(45L)).andReturn("LA");
//        EasyMock.expect(stateService.getAbbr(46L)).andReturn("MD");
//        EasyMock.expect(stateService.getAbbr(47L)).andReturn("MI");
//        EasyMock.expect(stateService.getAbbr(48L)).andReturn("MS");
//        EasyMock.expect(stateService.getAbbr(49L)).andReturn("MT");
//        EasyMock.expect(stateService.getAbbr(50L)).andReturn("TX");
//
//
//    }




}
