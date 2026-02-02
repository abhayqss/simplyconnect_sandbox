package com.scnsoft.eldermark.h2.healthPartners.termedmembers;

import com.scnsoft.eldermark.dto.healthpartners.RxClaimCSVDto;
import com.scnsoft.eldermark.dto.healthpartners.TermedMembersCSVDto;
import com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport;
import com.scnsoft.eldermark.h2.healthPartners.rxclaims.HealthPartnersConsanaRxFlowTestSupport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport.dateToString;

public class HealthPartnersTermedMembersTestSupport {

    private static final String client1MemberIdentifier = "client-to-term";
    private static final String client2MemberIdentifier = "client-to-term-2";

    public static List<TermedMemberExpectedDataDefinition> termedMembersExpectedDefinition() {
        List<TermedMemberExpectedDataDefinition> result = new ArrayList<>();
        var member = new TermedMembersCSVDto();
        member.setMemberIdentifier(client1MemberIdentifier);
        member.setMemberFirstName("Alex");
        member.setMemberMiddleName("John");
        member.setMemberLastName("Smith");
        member.setDateOfBirth(LocalDate.of(1996, 5, 26));
        result.add(TermedMemberExpectedDataDefinition.successful(
                "Inactive client should be created", 1, member
        ));

        var memberCopy = copy(member);
        result.add(TermedMemberExpectedDataDefinition.successful(
                "Another client should be not be created", 1, memberCopy
        ));

        var member2 = new TermedMembersCSVDto();
        member2.setMemberIdentifier(client2MemberIdentifier);
        member2.setMemberFirstName("John");
        member2.setMemberMiddleName("Robert");
        member2.setMemberLastName("Potter");
        member2.setDateOfBirth(LocalDate.of(2010, 5, 10));
        result.add(TermedMemberExpectedDataDefinition.successful(
                "Another inactive client should be created", 2, member2
        ));

        return result;
    }

    private static TermedMembersCSVDto copy(TermedMembersCSVDto source) {
        var result = new TermedMembersCSVDto();
        HealthPartnersITSupport.copy(source, result);
        return result;
    }

    public static List<RxClaimCSVDto> rxClaims() {

        var client1Claim = new RxClaimCSVDto();
        client1Claim.setMemberIdentifier(client1MemberIdentifier);
        client1Claim.setMemberFirstName("Alex");
        client1Claim.setMemberMiddleName("John");
        client1Claim.setMemberLastName("Smith");
        client1Claim.setDateOfBirth(LocalDate.of(1996, 5, 26));
        client1Claim.setDaysSupply(2);
        client1Claim.setPrescriberFirstName("James");
        client1Claim.setPrescriberMiddleName("Tomas");
        client1Claim.setPrescriberLastName("Wilson");
        client1Claim.setPrescribingPhysicianNPI("443423412");
        client1Claim.setCompoundCode("cc1");
        client1Claim.setDAWProductSelectionCode("2");
        client1Claim.setRefillNumber(1);
        client1Claim.setPrescriptionOriginCode("5");
        client1Claim.setDrugName("atorvastatin 40 MG Oral Tablet [Lipitor]");
        client1Claim.setPlanReportedBrandGenericCode("44342");
        client1Claim.setNationalDrugCode("00071015723");
        client1Claim.setServiceDate(LocalDate.of(2021, 4, 25));
        client1Claim.setClaimNo(String.valueOf(1));
        client1Claim.setRXNumber("11223");
        client1Claim.setQuantityDispensed(null); //test that null values are also parsed to null
        client1Claim.setQuantityQualifierCode("a");
        client1Claim.setPharmacyName("My favourite pharmacy");
        client1Claim.setClaimBillingProvider("provref");
        client1Claim.setPharmacyNPI("443421234");

        return Collections.singletonList(client1Claim);
    }

    private static String toCSV(List<TermedMembersCSVDto> claims) {
        var builder = new StringBuilder(
                "MEMBER_IDENTIFIER|MEMBER_FIRST_NM|MEMBER_MIDDLE_NM|MEMBER_LAST_NM|MEMBER_BIRTH_DT");
        for (var claim : claims) {
            builder.append("\r\n")
                    .append(StringUtils.defaultString(claim.getMemberIdentifier())).append("|")
                    .append(StringUtils.defaultString(claim.getMemberFirstName())).append("|")
                    .append(StringUtils.defaultString(claim.getMemberMiddleName())).append("|")
                    .append(StringUtils.defaultString(claim.getMemberLastName())).append("|")
                    .append(dateToString(claim.getDateOfBirth()));
        }
        return builder.toString();
    }

    public static void main(String[] args) throws IOException {
        var basePath = Path.of("eldermark-exchange-core-services\\src\\test\\resources\\h2\\healthpartners\\termed-members");

        writeTermedFile(HealthPartnersTermedMembersFlowIT.ValidTermedMembersImport.CSV_FILE_NAME, basePath, termedMembersExpectedDefinition());
        writeRxFile(HealthPartnersTermedMembersFlowIT.ClaimsImportToActivateTermedMembers.CSV_FILE_NAME, basePath, rxClaims());
    }

    private static void writeTermedFile(String csvFileName, Path basePath, List<TermedMemberExpectedDataDefinition> data) throws IOException {
        var csvData = data.stream().map(TermedMemberExpectedDataDefinition::getCsvData).collect(Collectors.toList());

        Files.writeString(basePath.resolve(Path.of(csvFileName)), toCSV(csvData));
    }

    private static void writeRxFile(String csvFileName, Path basePath, List<RxClaimCSVDto> csvData) throws IOException {
        Files.writeString(basePath.resolve(Path.of(csvFileName)), HealthPartnersConsanaRxFlowTestSupport.toCSV(csvData));
    }
}
