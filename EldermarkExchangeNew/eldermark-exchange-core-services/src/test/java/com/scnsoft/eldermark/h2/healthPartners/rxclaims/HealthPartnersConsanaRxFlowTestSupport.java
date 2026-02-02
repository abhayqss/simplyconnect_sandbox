package com.scnsoft.eldermark.h2.healthPartners.rxclaims;

import com.scnsoft.eldermark.dto.healthpartners.RxClaimCSVDto;
import com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport.dateToString;
import static com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport.numberToString;

public class HealthPartnersConsanaRxFlowTestSupport {

    private static RxClaimCSVDto firstClientClaim(int claimNo) {
        var client1Claim = new RxClaimCSVDto();
        client1Claim.setMemberIdentifier("rx-client-1");
        client1Claim.setMemberFirstName("Alex");
        client1Claim.setMemberMiddleName("John");
        client1Claim.setMemberLastName("Smith");
        client1Claim.setDateOfBirth(LocalDate.of(1996, 5, 26));
        client1Claim.setClaimNo(String.valueOf(claimNo));
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
        client1Claim.setRXNumber("11223");
        client1Claim.setQuantityDispensed(null); //test that null values are also parsed to null
        client1Claim.setQuantityQualifierCode("a");
        client1Claim.setPharmacyName("My favourite pharmacy");
        client1Claim.setClaimBillingProvider("provref");
        client1Claim.setPharmacyNPI("443421234");
        return client1Claim;
    }

    static List<RxClaimExpectedDataDefinition> createFirstWarnFileExpectedDefinition() {
        List<RxClaimExpectedDataDefinition> result = new ArrayList<>();

        int claimNoIdx = 1;

        result.add(RxClaimExpectedDataDefinition.successful(
                "Client 1 test", 1, 1, 1,
                firstClientClaim(claimNoIdx++)
        ));

        var invalidClaim = new RxClaimCSVDto();
        result.add(RxClaimExpectedDataDefinition.failed(
                "invalid claim test",
                "Member identifier is empty, Member first name is empty, Member last name is empty, Birth date is null",
                invalidClaim
        ));

        var client2refillNumber = 1;
        var client2_medication1_dispense1_claim = new RxClaimCSVDto();
        client2_medication1_dispense1_claim.setMemberIdentifier("rx-client-2");
        client2_medication1_dispense1_claim.setMemberFirstName("John");
        client2_medication1_dispense1_claim.setMemberMiddleName("Robert");
        client2_medication1_dispense1_claim.setMemberLastName("Potter");
        client2_medication1_dispense1_claim.setDateOfBirth(LocalDate.of(2010, 5, 10));
        client2_medication1_dispense1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication1_dispense1_claim.setDaysSupply(5);
        client2_medication1_dispense1_claim.setPrescriberFirstName("Gregory");
        client2_medication1_dispense1_claim.setPrescriberMiddleName("Brian");
        client2_medication1_dispense1_claim.setPrescriberLastName("House");
        client2_medication1_dispense1_claim.setPrescribingPhysicianNPI("111020");
        client2_medication1_dispense1_claim.setCompoundCode("cc2");
        client2_medication1_dispense1_claim.setDAWProductSelectionCode("1");
        client2_medication1_dispense1_claim.setRefillNumber(client2refillNumber++);
        client2_medication1_dispense1_claim.setPrescriptionOriginCode("4");
        client2_medication1_dispense1_claim.setDrugName("atorvastatin 40 MG Oral Tablet [Lipitor]");
        client2_medication1_dispense1_claim.setPlanReportedBrandGenericCode("44342");
        client2_medication1_dispense1_claim.setNationalDrugCode("00071015723");
        client2_medication1_dispense1_claim.setServiceDate(LocalDate.of(2021, 8, 15));
        client2_medication1_dispense1_claim.setRXNumber("33312");
        client2_medication1_dispense1_claim.setQuantityDispensed(BigDecimal.valueOf(0.12));
        client2_medication1_dispense1_claim.setQuantityQualifierCode("a");
        client2_medication1_dispense1_claim.setPharmacyName("My favourite pharmacy");
        client2_medication1_dispense1_claim.setClaimBillingProvider("provref");
        client2_medication1_dispense1_claim.setPharmacyNPI("443421234");
        result.add(RxClaimExpectedDataDefinition.successful(
                "Test that another client is created",
                2, 1, 1, client2_medication1_dispense1_claim
        ));


        var client2_medication1_dispense2_claim = copy(client2_medication1_dispense1_claim);
        client2_medication1_dispense2_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication1_dispense2_claim.setRefillNumber(client2refillNumber++);
        client2_medication1_dispense2_claim.setQuantityDispensed(BigDecimal.valueOf(14L));
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that dispense with different refill number is added to exiting medication if medication data is the same",
                2, 1, 2, client2_medication1_dispense2_claim
        ));


        var client2_medication2_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication2_dispense1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication2_dispense1_claim.setRefillNumber(client2refillNumber++);
        client2_medication2_dispense1_claim.setPrescriberFirstName(client2_medication1_dispense1_claim.getPrescriberFirstName() + "Changed");
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that new medication is created if prescriber was present and first name changed",
                2, 2, 1, client2_medication2_dispense1_claim
        ));


        var client2_medication3_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication3_dispense1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication3_dispense1_claim.setRefillNumber(client2refillNumber++);
        client2_medication3_dispense1_claim.setPrescriberFirstName(null);
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that new medication is created if prescriber was present and null first name came",
                2, 3, 1, client2_medication3_dispense1_claim
        ));


        var client2_medication3_dispense2_claim = copy(client2_medication3_dispense1_claim);
        client2_medication3_dispense2_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication3_dispense2_claim.setRefillNumber(client2refillNumber++);
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that existing medication is used when prescriber first name null comparison",
                2, 3, 2, client2_medication3_dispense2_claim
        ));


        var client2_medication4_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication4_dispense1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication4_dispense1_claim.setRefillNumber(client2refillNumber++);
        client2_medication4_dispense1_claim.setPrescriberLastName(
                client2_medication1_dispense1_claim.getPrescriberLastName() + "Changed"
        );
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that new medication is created if prescriber was present and last name changed",
                2, 4, 1, client2_medication4_dispense1_claim
        ));


        var client2_medication5_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication5_dispense1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication5_dispense1_claim.setRefillNumber(client2refillNumber++);
        client2_medication5_dispense1_claim.setPrescriberLastName(null);
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that new medication is created if prescriber was present and null last name came",
                2, 5, 1, client2_medication5_dispense1_claim
        ));


        var client2_medication5_dispense2_claim = copy(client2_medication5_dispense1_claim);
        client2_medication5_dispense2_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication5_dispense2_claim.setRefillNumber(client2refillNumber++);
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that existing medication is used when prescriber last name null comparison",
                2, 5, 2, client2_medication5_dispense2_claim
        ));


        var client2_medication6_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication6_dispense1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication6_dispense1_claim.setRefillNumber(client2refillNumber++);
        client2_medication6_dispense1_claim.setPrescribingPhysicianNPI(
                client2_medication6_dispense1_claim.getPrescribingPhysicianNPI() + "1"
        );
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that new medication is created if prescriber was present and npi changed",
                2, 6, 1, client2_medication6_dispense1_claim
        ));


        var client2_medication7_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication7_dispense1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication7_dispense1_claim.setRefillNumber(client2refillNumber++);
        client2_medication7_dispense1_claim.setPrescribingPhysicianNPI(null);
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that new medication is created if prescriber was present and null npi came",
                2, 7, 1, client2_medication7_dispense1_claim
        ));


        var client2_medication7_dispense2_claim = copy(client2_medication7_dispense1_claim);
        client2_medication7_dispense2_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication7_dispense2_claim.setRefillNumber(client2refillNumber++);
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that existing medication is used when prescriber npi null comparison",
                2, 7, 2, client2_medication7_dispense2_claim
        ));


        var client2_medication8_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication8_dispense1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication8_dispense1_claim.setRefillNumber(client2refillNumber++);
        client2_medication8_dispense1_claim.setNationalDrugCode("00045027026");
        client2_medication8_dispense1_claim.setDrugName("Some name which should not be used");
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that new medication is created if drug changed",
                2, 8, 1, client2_medication8_dispense1_claim
        ));


        var client2_medication9_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication9_dispense1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication9_dispense1_claim.setRefillNumber(client2refillNumber++);
        client2_medication9_dispense1_claim.setNationalDrugCode("12344321432");
        client2_medication9_dispense1_claim.setDrugName("unknown drug name");
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that drug name is used for unresolved drugs",
                2, 9, 1, client2_medication9_dispense1_claim
        ));


        var client2_medication9_dispense2_claim = copy(client2_medication9_dispense1_claim);
        client2_medication9_dispense2_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication9_dispense2_claim.setRefillNumber(client2refillNumber++);
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that existing medication used for unresolved drugs",
                2, 9, 2, client2_medication9_dispense2_claim
        ));


        var client2_medication9_dispense3_claim = copy(client2_medication9_dispense1_claim);
        client2_medication9_dispense3_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication9_dispense3_claim.setRefillNumber(client2refillNumber++);
        client2_medication9_dispense3_claim.setServiceDate(client2_medication9_dispense1_claim.getServiceDate().plusDays(5));
        result.add(RxClaimExpectedDataDefinition.successful(
                "test that service date is updated for dispense only",
                2, 9, 3, client2_medication9_dispense3_claim
        ));

        //test dispense updated by adjustment
        var originalClaimNo = String.valueOf(claimNoIdx++);
        var client2_medication_10_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication_10_dispense1_claim.setClaimNo(originalClaimNo);
        client2_medication_10_dispense1_claim.setPrescriberFirstName("disp_adj");
        var originalExpData = RxClaimExpectedDataDefinition.successful(
                "Medication dispense which will be adjusted, sorted before adjustment by claim number",
                2, 10, 1, client2_medication_10_dispense1_claim
        );

        var client2_medication_10_dispense1_adjustment_claim = copy(client2_medication_10_dispense1_claim);
        client2_medication_10_dispense1_adjustment_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication_10_dispense1_adjustment_claim.setQuantityDispensed(BigDecimal.valueOf(-0.01));
        client2_medication_10_dispense1_adjustment_claim.setDaysSupply(-1);
        client2_medication_10_dispense1_adjustment_claim.setClaimAdjustedFromIdentifier(originalClaimNo);
        result.add(RxClaimExpectedDataDefinition.adjusts(
                "Dispense updated", originalExpData, 10, 1, client2_medication_10_dispense1_adjustment_claim
        ));
        result.add(originalExpData);

        //test dispense updated by adjustment chain
        originalClaimNo = String.valueOf(claimNoIdx++);
        var client2_medication_11_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication_11_dispense1_claim.setClaimNo(originalClaimNo);
        client2_medication_11_dispense1_claim.setPrescriberFirstName("disp_adj_chain");
        originalExpData = RxClaimExpectedDataDefinition.successful(
                "Medication dispense which will be adjusted by chain of adjustments",
                2, 11, 1, client2_medication_11_dispense1_claim
        );
        result.add(originalExpData);

        var client2_medication_11_dispense1_adjustment1_claim = copy(client2_medication_11_dispense1_claim);
        client2_medication_11_dispense1_adjustment1_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication_11_dispense1_adjustment1_claim.setQuantityDispensed(BigDecimal.valueOf(-0.01));
        client2_medication_11_dispense1_adjustment1_claim.setDaysSupply(-1);
        client2_medication_11_dispense1_adjustment1_claim.setClaimAdjustedFromIdentifier(originalClaimNo);
        result.add(RxClaimExpectedDataDefinition.adjusts(
                "Dispense updated by adjustment chain 1", originalExpData, 11, 1, client2_medication_11_dispense1_adjustment1_claim
        ));

        var client2_medication_11_dispense1_adjustment2_claim = copy(client2_medication_11_dispense1_adjustment1_claim);
        client2_medication_11_dispense1_adjustment2_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication_11_dispense1_adjustment2_claim.setQuantityDispensed(BigDecimal.valueOf(-0.02));
        client2_medication_11_dispense1_adjustment2_claim.setDaysSupply(-2);

        result.add(RxClaimExpectedDataDefinition.adjusts(
                "Dispense updated by adjustment chain 2", originalExpData, 11, 1, client2_medication_11_dispense1_adjustment2_claim
        ));

        //test dispense deleted
        originalClaimNo = String.valueOf(claimNoIdx++);
        var client2_medication_12_dispense1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication_12_dispense1_claim.setClaimNo(originalClaimNo);
        client2_medication_12_dispense1_claim.setPrescriberFirstName("disp_del");
        originalExpData = RxClaimExpectedDataDefinition.successful(
                "Medication dispense which will be deleted by adjustment",
                2, 12,
                RxClaimExpectedDataDefinition.DISPENSE_DELETED, client2_medication_12_dispense1_claim
        );
        result.add(originalExpData);

        var client2_medication_12_dispense2_claim = copy(client2_medication_12_dispense1_claim);
        client2_medication_12_dispense2_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication_12_dispense2_claim.setRefillNumber(client2_medication_12_dispense1_claim.getRefillNumber() + 1);
        result.add(RxClaimExpectedDataDefinition.successful(
                "This dispense will prevent medication from being deleted",
                2, 12, 1, client2_medication_12_dispense2_claim
        ));

        var client2_medication_12_dispense1_adjustment_claim = copy(client2_medication_12_dispense1_claim);
        client2_medication_12_dispense1_adjustment_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication_12_dispense1_adjustment_claim.setQuantityDispensed(client2_medication_12_dispense1_adjustment_claim.getQuantityDispensed().negate());
        client2_medication_12_dispense1_adjustment_claim.setDaysSupply(-client2_medication_12_dispense1_adjustment_claim.getDaysSupply());
        client2_medication_12_dispense1_adjustment_claim.setClaimAdjustedFromIdentifier(originalClaimNo);
        result.add(RxClaimExpectedDataDefinition.adjusts(
                "Adjustment which causes dispense delete", originalExpData, 12,
                RxClaimExpectedDataDefinition.DISPENSE_DELETED, client2_medication_12_dispense1_adjustment_claim
        ));

        //test medication deleted
        originalClaimNo = String.valueOf(claimNoIdx++);
        var client2_medication_deleted_1_claim = copy(client2_medication1_dispense1_claim);
        client2_medication_deleted_1_claim.setClaimNo(originalClaimNo);
        client2_medication_deleted_1_claim.setPrescriberFirstName("med_del");
        originalExpData = RxClaimExpectedDataDefinition.successful(
                "Medication which will be deleted by adjustment",
                2, RxClaimExpectedDataDefinition.MEDICATION_DELETED,
                RxClaimExpectedDataDefinition.DISPENSE_DELETED, client2_medication_deleted_1_claim
        );
        result.add(originalExpData);

        var client2_medication_deleted_1_adjustment_claim = copy(client2_medication_deleted_1_claim);
        client2_medication_deleted_1_adjustment_claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication_deleted_1_adjustment_claim.setQuantityDispensed(client2_medication_deleted_1_claim.getQuantityDispensed().negate());
        client2_medication_deleted_1_adjustment_claim.setDaysSupply(-client2_medication_deleted_1_claim.getDaysSupply());
        client2_medication_deleted_1_adjustment_claim.setClaimAdjustedFromIdentifier(originalClaimNo);
        result.add(RxClaimExpectedDataDefinition.adjusts(
                "Adjustment which causes medication delete", originalExpData,
                RxClaimExpectedDataDefinition.MEDICATION_DELETED,
                RxClaimExpectedDataDefinition.DISPENSE_DELETED, client2_medication_deleted_1_adjustment_claim
        ));


        return result;
    }

    static List<RxClaimExpectedDataDefinition> createSecondFileWithDuplicatesExpectedDefinition() {
        List<RxClaimExpectedDataDefinition> result = new ArrayList<>();

        int claimNoIdx = 1;
        var client1Claim = firstClientClaim(claimNoIdx++);
        result.add(RxClaimExpectedDataDefinition.successful(
                "Will not be recognized as duplicate because artificially will fail previous processing",
                1, 2, 1,
                client1Claim
        ));

        var client2refillNumber = 1;
        var client2_medication1_dispense1_Claim = new RxClaimCSVDto();
        client2_medication1_dispense1_Claim.setMemberIdentifier("rx-client-2");
        client2_medication1_dispense1_Claim.setMemberFirstName("John");
        client2_medication1_dispense1_Claim.setMemberMiddleName("Robert");
        client2_medication1_dispense1_Claim.setMemberLastName("Potter");
        client2_medication1_dispense1_Claim.setDateOfBirth(LocalDate.of(2010, 5, 10));
        client2_medication1_dispense1_Claim.setClaimNo(String.valueOf(claimNoIdx++));
        client2_medication1_dispense1_Claim.setDaysSupply(5);
        client2_medication1_dispense1_Claim.setPrescriberFirstName("Gregory");
        client2_medication1_dispense1_Claim.setPrescriberMiddleName("Brian");
        client2_medication1_dispense1_Claim.setPrescriberLastName("House");
        client2_medication1_dispense1_Claim.setPrescribingPhysicianNPI("111020");
        client2_medication1_dispense1_Claim.setCompoundCode("cc2");
        client2_medication1_dispense1_Claim.setDAWProductSelectionCode("1");
        client2_medication1_dispense1_Claim.setRefillNumber(client2refillNumber++);
        client2_medication1_dispense1_Claim.setPrescriptionOriginCode("4");
        client2_medication1_dispense1_Claim.setDrugName("atorvastatin 40 MG Oral Tablet [Lipitor]");
        client2_medication1_dispense1_Claim.setPlanReportedBrandGenericCode("44342");
        client2_medication1_dispense1_Claim.setNationalDrugCode("00071015723");
        client2_medication1_dispense1_Claim.setServiceDate(LocalDate.of(2021, 8, 15));
        client2_medication1_dispense1_Claim.setRXNumber("33312");
        client2_medication1_dispense1_Claim.setQuantityDispensed(BigDecimal.valueOf(0.12));
        client2_medication1_dispense1_Claim.setQuantityQualifierCode("a");
        client2_medication1_dispense1_Claim.setPharmacyName("My favourite pharmacy");
        client2_medication1_dispense1_Claim.setClaimBillingProvider("provref");
        client2_medication1_dispense1_Claim.setPharmacyNPI("443421234");
        result.add(RxClaimExpectedDataDefinition.duplicate(
                "Duplicate not processed again",
                client2_medication1_dispense1_Claim
        ));

        var client3refillNumber = 1;
        var client3_medication1_dispense1_Claim = new RxClaimCSVDto();
        client3_medication1_dispense1_Claim.setMemberIdentifier("rx-client-3");
        client3_medication1_dispense1_Claim.setMemberFirstName("Hugo");
        client3_medication1_dispense1_Claim.setMemberMiddleName("John");
        client3_medication1_dispense1_Claim.setMemberLastName("Boss");
        client3_medication1_dispense1_Claim.setDateOfBirth(LocalDate.of(2013, 1, 17));
        client3_medication1_dispense1_Claim.setClaimNo(String.valueOf(100));
        client3_medication1_dispense1_Claim.setDaysSupply(5);
        client3_medication1_dispense1_Claim.setPrescriberFirstName("Gregory");
        client3_medication1_dispense1_Claim.setPrescriberMiddleName("Brian");
        client3_medication1_dispense1_Claim.setPrescriberLastName("House");
        client3_medication1_dispense1_Claim.setPrescribingPhysicianNPI("111020");
        client3_medication1_dispense1_Claim.setCompoundCode("cc2");
        client3_medication1_dispense1_Claim.setDAWProductSelectionCode("1");
        client3_medication1_dispense1_Claim.setRefillNumber(client3refillNumber++);
        client3_medication1_dispense1_Claim.setPrescriptionOriginCode("4");
        client3_medication1_dispense1_Claim.setDrugName("atorvastatin 40 MG Oral Tablet [Lipitor]");
        client3_medication1_dispense1_Claim.setPlanReportedBrandGenericCode("44342");
        client3_medication1_dispense1_Claim.setNationalDrugCode("00071015723");
        client3_medication1_dispense1_Claim.setServiceDate(LocalDate.of(2021, 8, 15));
        client3_medication1_dispense1_Claim.setRXNumber("33312");
        client3_medication1_dispense1_Claim.setQuantityDispensed(BigDecimal.valueOf(0.12));
        client3_medication1_dispense1_Claim.setQuantityQualifierCode("a");
        client3_medication1_dispense1_Claim.setPharmacyName("My favourite pharmacy");
        client3_medication1_dispense1_Claim.setClaimBillingProvider("provref");
        client3_medication1_dispense1_Claim.setPharmacyNPI("443421234");
        result.add(RxClaimExpectedDataDefinition.successful(
                "Test that another client is created",
                2, 1, 1, client3_medication1_dispense1_Claim
        ));

        return result;
    }

    private static RxClaimCSVDto copy(RxClaimCSVDto source) {
        var result = new RxClaimCSVDto();
        HealthPartnersITSupport.copy(source, result);
        return result;
    }

    public static String toCSV(List<RxClaimCSVDto> claims) {
        var builder = new StringBuilder(
                "MEMBER_IDENTIFIER|MEMBER_FIRST_NM|MEMBER_MIDDLE_NM|MEMBER_LAST_NM|MEMBER_BIRTH_DT|" +
                        "CLAIM_NO|DAYS_SUPPLY|" +
                        "PRESCRIBER_FIRST_NM|PRESCRIBER_MIDDLE_NM|PRESCRIBER_LAST_NM|PRESCRIBER_NPI|" +
                        "COMPOUND_CD|DAW_CD|REFILL_NO|PRESCRIPTION_ORIGIN_CD|DRUG_NM|BRAND_GENERIC_CD|" +
                        "NATIONAL_DRUG_CODE|FILLED_DT|PRESCRIPTION_NO|CLAIM_ADJUSTED_FROM_IDENTIFIER|" +
                        "RELATED_CLAIM_RELATIONSHIP|QUANTITY_DISPENSED|UNIT_OF_MEASURE|PHARMACY_NM|PHARMACY_NABP|PHARMACY_NPI");
        for (var claim : claims) {
            builder.append("\r\n")
                    .append(StringUtils.defaultString(claim.getMemberIdentifier())).append("|")
                    .append(StringUtils.defaultString(claim.getMemberFirstName())).append("|")
                    .append(StringUtils.defaultString(claim.getMemberMiddleName())).append("|")
                    .append(StringUtils.defaultString(claim.getMemberLastName())).append("|")
                    .append(dateToString(claim.getDateOfBirth())).append("|")

                    .append(StringUtils.defaultString(claim.getClaimNo())).append("|")
                    .append(numberToString(claim.getDaysSupply())).append("|")

                    .append(StringUtils.defaultString(claim.getPrescriberFirstName())).append("|")
                    .append(StringUtils.defaultString(claim.getPrescriberMiddleName())).append("|")
                    .append(StringUtils.defaultString(claim.getPrescriberLastName())).append("|")
                    .append(StringUtils.defaultString(claim.getPrescribingPhysicianNPI())).append("|")

                    .append(StringUtils.defaultString(claim.getCompoundCode())).append("|")
                    .append(StringUtils.defaultString(claim.getDAWProductSelectionCode())).append("|")
                    .append(numberToString(claim.getRefillNumber())).append("|")
                    .append(StringUtils.defaultString(claim.getPrescriptionOriginCode())).append("|")
                    .append(StringUtils.defaultString(claim.getDrugName())).append("|")
                    .append(StringUtils.defaultString(claim.getPlanReportedBrandGenericCode())).append("|")

                    .append(StringUtils.defaultString(claim.getNationalDrugCode())).append("|")
                    .append(dateToString(claim.getServiceDate())).append("|")
                    .append(StringUtils.defaultString(claim.getRXNumber())).append("|")
                    .append(StringUtils.defaultString(claim.getClaimAdjustedFromIdentifier())).append("|")

                    .append(StringUtils.defaultString(claim.getRelatedClaimRelationship())).append("|")
                    .append(numberToString(claim.getQuantityDispensed())).append("|")
                    .append(StringUtils.defaultString(claim.getQuantityQualifierCode())).append("|")
                    .append(StringUtils.defaultString(claim.getPharmacyName())).append("|")
                    .append(StringUtils.defaultString(claim.getClaimBillingProvider())).append("|")
                    .append(StringUtils.defaultString(claim.getPharmacyNPI()));
        }
        return builder.toString();
    }

    public static void main(String[] args) throws IOException {
        var basePath = Path.of("eldermark-exchange-core-services\\src\\test\\resources\\h2\\healthpartners\\rx-claims");

        writeFile(HealthPartnersConsanaRxFlowIT.FirstWarnFileImport.CSV_FILE_NAME, basePath, createFirstWarnFileExpectedDefinition());
        writeFile(HealthPartnersConsanaRxFlowIT.SecondOkFileImportWithDuplicates.CSV_FILE_NAME, basePath, createSecondFileWithDuplicatesExpectedDefinition());
    }

    private static void writeFile(String csvFileName, Path basePath, List<RxClaimExpectedDataDefinition> data) throws IOException {
        var csvData = data.stream().map(RxClaimExpectedDataDefinition::getCsvData).collect(Collectors.toList());

        Files.writeString(basePath.resolve(Path.of(csvFileName)), toCSV(csvData));
    }
}
