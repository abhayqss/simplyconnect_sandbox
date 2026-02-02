package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientDetails;
import com.scnsoft.eldermark.dto.pointclickcare.projection.PccClientMatchProjectionAdapter;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PccClientMatchProjectionToPatientMatchCriteriaConverterTest {

    private final PccClientMatchProjectionToPatientMatchCriteriaConverter instance = new PccClientMatchProjectionToPatientMatchCriteriaConverter();

    private static Stream<Arguments> genders() {
        return Stream.of(
                Arguments.of(prepareGenderCcd("F", CodeSystem.ADMINISTRATIVE_GENDER.getOid()), PCCPatientDetails.Gender.FEMALE),
                Arguments.of(prepareGenderCcd("M", CodeSystem.ADMINISTRATIVE_GENDER.getOid()), PCCPatientDetails.Gender.MALE),
                Arguments.of(prepareGenderCcd("UN", CodeSystem.ADMINISTRATIVE_GENDER.getOid()), PCCPatientDetails.Gender.UNKNOWN),
                Arguments.of(prepareGenderCcd("M", "1.2.3.4"), PCCPatientDetails.Gender.UNKNOWN),
                Arguments.of(null, PCCPatientDetails.Gender.UNKNOWN)
        );
    }

    private static CcdCode prepareGenderCcd(String code, String codeSystem) {
        var genderCcd = new CcdCode();
        genderCcd.setCode(code);
        genderCcd.setCodeSystem(codeSystem);

        return genderCcd;
    }

    @ParameterizedTest
    @MethodSource("genders")
    void convert_hasValidGender(CcdCode genderCcd, PCCPatientDetails.Gender gender) {
        var birthDate = LocalDate.of(2021, 1, 21);
        var facId = 1L;
        var firstName = "firstName";
        var lastName = "lastName";
        var medicaidNumber = "medicaid";
        var medicareNumber = "medicare";

        var comm = new Community();
        comm.setPccFacilityId(facId);

        var client = new Client();
        client.setCommunity(comm);
        client.setBirthDate(birthDate);
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setMedicareNumber(medicareNumber);
        client.setMedicaidNumber(medicaidNumber);
        client.setGender(genderCcd);


        var criteria = instance.convert(new PccClientMatchProjectionAdapter(client));


        assertThat(criteria).isNotNull();
        assertThat(criteria.getBirthDate()).isEqualTo(birthDate);
        assertThat(criteria.getFacId()).isEqualTo(facId);
        assertThat(criteria.getFirstName()).isEqualTo(firstName);
        assertThat(criteria.getGender()).isEqualTo(gender);
        assertThat(criteria.getHealthCardNumber()).isNull();
        assertThat(criteria.getLastName()).isEqualTo(lastName);
        assertThat(criteria.getMedicaidNumber()).isEqualTo(medicaidNumber);
        assertThat(criteria.getMedicareNumber()).isEqualTo(medicareNumber);
        assertThat(criteria.getSocialBeneficiaryIdentifier()).isNull();
    }


}