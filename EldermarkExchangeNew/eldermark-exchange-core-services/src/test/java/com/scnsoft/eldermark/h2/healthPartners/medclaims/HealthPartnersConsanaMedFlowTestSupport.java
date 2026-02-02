package com.scnsoft.eldermark.h2.healthPartners.medclaims;

import com.scnsoft.eldermark.dto.healthpartners.MedClaimCSVDto;
import com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport.dateToString;
import static com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport.numberToString;

class HealthPartnersConsanaMedFlowTestSupport {

    static MedClaimCSVDto createFirstClientClaim(int claimNo) {
        var client1Claim = new MedClaimCSVDto();
        client1Claim.setMemberIdentifier("med-client-1");
        client1Claim.setMemberFirstName("Alex");
        client1Claim.setMemberMiddleName("John");
        client1Claim.setMemberLastName("Smith");
        client1Claim.setDateOfBirth(LocalDate.of(1996, 5, 26));
        client1Claim.setClaimNo(String.valueOf(claimNo));
        client1Claim.setServiceDate(LocalDate.of(2021, 4, 25));
        client1Claim.setIcdVersion(10);
        client1Claim.setDiagnosisCode("J43.9");//Emphysema, unspecified
        client1Claim.setDiagnosisTxt("unused text");
        client1Claim.setPhysicianFirstName("Greg");
        client1Claim.setPhysicianMiddleName("Brian");
        client1Claim.setPhysicianLastName("House");
        return client1Claim;
    }

    static List<MedClaimExpectedDataDefinition> createFirstWarnFileExpectedDefinition() {
        List<MedClaimExpectedDataDefinition> result = new ArrayList<>();
        int claimNo = 1;

        result.add(MedClaimExpectedDataDefinition.successful(
                "Client should be created, existing diagnosis code with existing name should be used",
                1, 1, false,
                createFirstClientClaim(claimNo++)
        ));

        var invalidClaim = new MedClaimCSVDto();
        result.add(MedClaimExpectedDataDefinition.failed(
                "Invalid claim",
                "Member identifier is empty, Member first name is empty, Member last name is empty, Birth date is null, Diagnosis code is empty",
                invalidClaim
        ));

        var client2_problem1 = new MedClaimCSVDto();
        client2_problem1.setMemberIdentifier("med-client-2");
        client2_problem1.setMemberFirstName("John");
        client2_problem1.setMemberMiddleName("Robert");
        client2_problem1.setMemberLastName("Potter");
        client2_problem1.setDateOfBirth(LocalDate.of(2010, 5, 10));
        client2_problem1.setClaimNo(String.valueOf(claimNo++));
        client2_problem1.setServiceDate(LocalDate.of(2021, 8, 15));
        client2_problem1.setIcdVersion(10);
        client2_problem1.setDiagnosisCode("unknown");
        client2_problem1.setDiagnosisTxt("unknown code name");
        client2_problem1.setPhysicianFirstName("James");
        client2_problem1.setPhysicianMiddleName(null);
        client2_problem1.setPhysicianLastName("Wilson");
        result.add(MedClaimExpectedDataDefinition.successful(
                "Another client is created, unknown ICD-10 code is created",
                2, 1, true,
                client2_problem1));

        var client2_problem2 = copy(client2_problem1);
        client2_problem2.setClaimNo(String.valueOf(claimNo++));
        result.add(MedClaimExpectedDataDefinition.successful(
                "Unknown code is reused",
                2, 2, false,
                client2_problem2));

        var client2_problem3 = copy(client2_problem1);
        client2_problem3.setClaimNo(String.valueOf(claimNo));
        client2_problem3.setDiagnosisCode("J43.9");
        client2_problem3.setIcdVersion(null);
        result.add(MedClaimExpectedDataDefinition.successful(
                "ICD-10 is used by default, same claim number doesn't cause any errors, sorting by diagnosis code applied",
                2, 4, false,
                client2_problem3));

        var client2_problem4 = copy(client2_problem1);
        client2_problem4.setClaimNo(String.valueOf(claimNo++));
        client2_problem4.setDiagnosisCode("805.6"); //Coccyx Fracture
        client2_problem4.setIcdVersion(9);
        result.add(MedClaimExpectedDataDefinition.successful(
                "Existing ICD-9 code is used, sorting by diagnosis code applied",
                2, 3, false,
                client2_problem4));

        var client2_problem5 = copy(client2_problem4);
        client2_problem5.setClaimNo(String.valueOf(claimNo++));
        client2_problem5.setIcdVersion(9);
        client2_problem5.setDiagnosisCode("unknown-icd-9");
        client2_problem5.setDiagnosisTxt("Unknown icd-9 code name");
        result.add(MedClaimExpectedDataDefinition.successful(
                "Unknown ICD-9 code is created",
                2, 5, true,
                client2_problem5));

        var client2_problem6 = copy(client2_problem5);
        client2_problem6.setClaimNo(String.valueOf(claimNo++));
        client2_problem6.setPhysicianFirstName(null);
        client2_problem6.setPhysicianMiddleName(null);
        client2_problem6.setPhysicianLastName(null);
        result.add(MedClaimExpectedDataDefinition.successful(
                "Unknown ICD-9 code is reused, problem author is null",
                2, 6, false,
                client2_problem6));

        return result;
    }

    static List<MedClaimExpectedDataDefinition> createOkWithDuplicatesFileExpectedDefinition() {
        List<MedClaimExpectedDataDefinition> result = new ArrayList<>();
        int claimNo = 1;

        result.add(MedClaimExpectedDataDefinition.successful("Will not be recognized as duplicate because artificially will fail previous processing",
                1, 2, false,
                createFirstClientClaim(claimNo++)));

        var client2_problem1 = new MedClaimCSVDto();
        client2_problem1.setMemberIdentifier("med-client-2");
        client2_problem1.setMemberFirstName("John");
        client2_problem1.setMemberMiddleName("Robert");
        client2_problem1.setMemberLastName("Potter");
        client2_problem1.setDateOfBirth(LocalDate.of(2010, 5, 10));
        client2_problem1.setClaimNo(String.valueOf(claimNo++));
        client2_problem1.setServiceDate(LocalDate.of(2021, 8, 15));
        client2_problem1.setIcdVersion(10);
        client2_problem1.setDiagnosisCode("unknown");
        client2_problem1.setDiagnosisTxt("unknown code name");
        client2_problem1.setPhysicianFirstName("James");
        client2_problem1.setPhysicianMiddleName(null);
        client2_problem1.setPhysicianLastName("Wilson");
        result.add(MedClaimExpectedDataDefinition.duplicate(
                "duplicate record", client2_problem1));

        var client3_problem1 = new MedClaimCSVDto();
        client3_problem1.setMemberIdentifier("med-client-3");
        client3_problem1.setMemberFirstName("Hugo");
        client3_problem1.setMemberMiddleName("John");
        client3_problem1.setMemberLastName("Boss");
        client3_problem1.setDateOfBirth(LocalDate.of(2010, 5, 10));
        client3_problem1.setClaimNo(String.valueOf(100));
        client3_problem1.setServiceDate(LocalDate.of(2021, 8, 15));
        client3_problem1.setIcdVersion(10);
        client3_problem1.setDiagnosisCode("unknown");
        client3_problem1.setDiagnosisTxt("unknown code name");
        client3_problem1.setPhysicianFirstName("James");
        client3_problem1.setPhysicianMiddleName(null);
        client3_problem1.setPhysicianLastName("Wilson");
        result.add(MedClaimExpectedDataDefinition.successful(
                "Client is created, unknown ICD-10 code is reused",
                2, 1, true,
                client3_problem1));

        return result;
    }

    private static MedClaimCSVDto copy(MedClaimCSVDto source) {
        var result = new MedClaimCSVDto();
        HealthPartnersITSupport.copy(source, result);
        return result;
    }

    private static String toCSV(List<MedClaimCSVDto> claims) {
        var builder = new StringBuilder(
                "MEMBER_IDENTIFIER|MEMBER_FIRST_NM|MEMBER_MIDDLE_NM|MEMBER_LAST_NM|MEMBER_BIRTH_DT|CLAIM_NO|SERVICE_START_DT|" +
                        "ICD_CODE_SET|DIAGNOSIS_CD|DIAGNOSIS_TXT|PHYSICIAN_FIRST_NM|PHYSICIAN_MIDDLE_NM|PHYSICIAN_LAST_NM");
        for (var claim : claims) {
            builder.append("\r\n")
                    .append(StringUtils.defaultString(claim.getMemberIdentifier())).append("|")
                    .append(StringUtils.defaultString(claim.getMemberFirstName())).append("|")
                    .append(StringUtils.defaultString(claim.getMemberMiddleName())).append("|")
                    .append(StringUtils.defaultString(claim.getMemberLastName())).append("|")
                    .append(dateToString(claim.getDateOfBirth())).append("|")
                    .append(StringUtils.defaultString(claim.getClaimNo())).append("|")
                    .append(dateToString(claim.getServiceDate())).append("|")
                    .append(numberToString(claim.getIcdVersion())).append("|")
                    .append(StringUtils.defaultString(claim.getDiagnosisCode())).append("|")
                    .append(StringUtils.defaultString(claim.getDiagnosisTxt())).append("|")
                    .append(StringUtils.defaultString(claim.getPhysicianFirstName())).append("|")
                    .append(StringUtils.defaultString(claim.getPhysicianMiddleName())).append("|")
                    .append(StringUtils.defaultString(claim.getPhysicianLastName()));
        }
        return builder.toString();
    }


    public static void main(String[] args) throws IOException {
        var basePath = Path.of("eldermark-exchange-core-services\\src\\test\\resources\\h2\\healthpartners\\med-claims");

        writeFile(HealthPartnersConsanaMedFlowIT.FirstWarnFileImport.CSV_FILE_NAME, basePath, createFirstWarnFileExpectedDefinition());
        writeFile(HealthPartnersConsanaMedFlowIT.SecondOkFileWithDuplicatesImport.CSV_FILE_NAME, basePath, createOkWithDuplicatesFileExpectedDefinition());
    }

    private static void writeFile(String csvFileName, Path basePath, List<MedClaimExpectedDataDefinition> data) throws IOException {
        var csvData = data.stream().map(MedClaimExpectedDataDefinition::getCsvData).collect(Collectors.toList());

        Files.writeString(basePath.resolve(Path.of(csvFileName)), toCSV(csvData));
    }
}
