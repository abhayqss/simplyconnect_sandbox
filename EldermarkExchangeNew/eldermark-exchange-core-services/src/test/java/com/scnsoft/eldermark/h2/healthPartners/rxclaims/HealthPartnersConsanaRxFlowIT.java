package com.scnsoft.eldermark.h2.healthPartners.rxclaims;

import com.scnsoft.eldermark.beans.projection.ActiveAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.MedicationDao;
import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersRxClaimDao;
import com.scnsoft.eldermark.dto.healthpartners.RxClaimCSVDto;
import com.scnsoft.eldermark.entity.*;
import com.scnsoft.eldermark.entity.community.Community;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim_;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.entity.medication.Medication;
import com.scnsoft.eldermark.entity.medication.MedicationInformation;
import com.scnsoft.eldermark.h2.healthPartners.BaseHealthPartnersIT;
import com.scnsoft.eldermark.service.document.cda.generator.CcdGeneratorService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersUtils;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.dbunit.DatabaseUnitException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport.atStartOfDayCentralTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
//DirtiesContext because processing occurs in new transaction so data is not rolled back after test execution
//todo investigate approach to delete data
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class HealthPartnersConsanaRxFlowIT extends BaseHealthPartnersIT {

    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersConsanaRxFlowIT.class);

    @Autowired
    private MedicationDao medicationDao;

    @Autowired
    private HealthPartnersRxClaimDao healthPartnersRxClaimDao;

    @Autowired
    private CommunityDao communityDao;

    @Autowired
    @Qualifier("ndcApiRestTemplate")
    private RestTemplate ndcApiRestTemplate;

    @Value("${rxnorm.ndc.status.url}")
    private String ndcStatusUrl;

    @Value("${rxnorm.version.url}")
    private String rxNormVersionUrl;

    private static final Map<String, Pair<String, String>> ndcRxNormMapping = Map.of(
            "00071015723", new Pair<>("617320", "atorvastatin 40 MG Oral Tablet [Lipitor]"),
            "00045027026", new Pair<>("1110988", "acetaminophen 325 MG / dextromethorphan hydrobromide 10 MG / guaifenesin 200 MG / phenylephrine hydrochloride 5 MG Oral Tablet")
    );

    private static final Map<String, String> ndcApiResponse = Map.of(
            "00071015723", "{\"ndcStatus\":{\"ndc11\":\"00071015723\",\"status\":\"ACTIVE\",\"active\":\"YES\",\"rxnormNdc\":\"YES\",\"rxcui\":\"617320\",\"conceptName\":\"atorvastatin 40 MG Oral Tablet [Lipitor]\",\"conceptStatus\":\"ACTIVE\",\"sourceList\":{\"sourceName\":[\"GS\",\"MMSL\",\"MMX\",\"MTHSPL\",\"RXNORM\",\"VANDF\"]},\"altNdc\":\"N\",\"comment\":\"\",\"ndcHistory\":[{\"activeRxcui\":\"617320\",\"originalRxcui\":\"617320\",\"startDate\":\"200706\",\"endDate\":\"202109\"}]}}",
            "00045027026", "{\"ndcStatus\":{\"ndc11\":\"00045027026\",\"status\":\"ACTIVE\",\"active\":\"YES\",\"rxnormNdc\":\"YES\",\"rxcui\":\"1110988\",\"conceptName\":\"acetaminophen 325 MG / dextromethorphan hydrobromide 10 MG / guaifenesin 200 MG / phenylephrine hydrochloride 5 MG Oral Tablet\",\"conceptStatus\":\"ACTIVE\",\"sourceList\":{\"sourceName\":[\"GS\",\"RXNORM\"]},\"altNdc\":\"N\",\"comment\":\"\",\"ndcHistory\":[{\"activeRxcui\":\"1110988\",\"originalRxcui\":\"1110988\",\"startDate\":\"201503\",\"endDate\":\"202109\"}]}}"
    );

    private static final String UNKNOWN_NDC_CODE_RESPONSE_TEMPLATE = "{\"ndcStatus\":{\"ndc11\":\"%s\",\"status\":\"UNKNOWN\",\"active\":\"\",\"rxnormNdc\":\"\",\"rxcui\":\"\",\"conceptName\":\"\",\"conceptStatus\":\"\",\"sourceList\":null,\"altNdc\":\"N\",\"comment\":\"NDC not found in All NDC data.\"}}";

    private static final String RXNORM_VERSION_RESPONSE = "{\"version\":\"03-Jan-2022\",\"apiVersion\":\"3.1.151\"}";

    interface PIN {
        Boolean getIsSignaturePinEnabled();
    }

    /**
     * Due to the fact that processing occurs in new transaction data is not rolled back after test execution.
     * Therefore to perform clean testing data deletion should be implemented or context should be marked as dirty.
     * This needs to be investigated
     *
     * <p>
     *
     * @throws IOException
     */
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    abstract class BaseRxFileImportTestCase extends BaseHealthPartnersIT {
        Long fileLogId;
        Long targetCommunityId;
        List<RxClaimExpectedDataDefinition> expectedData;

        abstract String getFileNamePostfix();

        abstract List<RxClaimExpectedDataDefinition> getExpectedData();

        abstract String getCsvFileName();

        abstract void additionalActions(List<RxClaimExpectedDataDefinition> expectedData) throws URISyntaxException;

        abstract ProcessingSummary.ProcessingStatus getExpectedFileStatus();

        @BeforeAll
        void processValidSftpFile() throws IOException, DatabaseUnitException, SQLException, URISyntaxException {
            importHpOrgsAndCommunities();

            var filename = HpFileType.CONSANA_RX.name() + getFileNamePostfix();
            targetCommunityId = communityDao.findByOrganization_AlternativeIdAndOid(hpOrgAlternativeId, hpCommunityOid, IdAware.class)
                    .orElseThrow().getId();

            expectedData = getExpectedData();
            final int[] i = {1};
            expectedData.forEach(ed -> ed.getCsvData().setLineNumber(i[0]++));

            expectedData.sort(
                    Comparator
                            .comparing((Function<RxClaimExpectedDataDefinition, String>) ed -> StringUtils.defaultString(ed.getCsvData().getMemberIdentifier()))
                            .thenComparing(ed -> Optional.ofNullable(ed.getCsvData().getClaimNo()).map(String::length).orElse(0))
                            .thenComparing(ed -> StringUtils.defaultString(ed.getCsvData().getClaimNo()))
            );

//            expectedData.forEach(ed -> System.out.printf("line %d, member %s, claimno %s, description %s\r\n",
//                            ed.getCsvData().getLineNumber(), ed.getCsvData().getMemberIdentifier(),
//                            ed.getCsvData().getClaimNo(), ed.getDescription()));

            additionalActions(expectedData);
            fileLogId = putFileToLocalCacheAndWaitUntilProcessed(loadTestCsv(getCsvFileName()), filename);
        }

        @Test
        @Transactional
        void fileLogShouldHaveExpectedProcessingStatus() {
            var fileLog = healthPartnersFileLogDao.findById(fileLogId);

            assertFileProcessedStatus(fileLog, getExpectedFileStatus());
        }

        @Test
        @Transactional
        void validateClientIsCreatedForUniqueMemberIdentifier() {
            var uniqueMemberIdentifiers = extractMemberIdentifiers(expectedData);
            var clients = loadOrderedClientsByExpectedData(expectedData, targetCommunityId);

            assertThat(clients).hasSize(uniqueMemberIdentifiers.size());
        }

        @Test
        @Transactional
        void validateThatRxClaimEntityCreatedForEveryRecord() {
            var claims = healthPartnersRxClaimDao.findAllByHpFileLogId(fileLogId, Sort.by(HealthPartnersRxClaim_.ID));

            assertThat(claims)
                    .describedAs("HealthPartnersRxClaim should be created for each claim in input file")
                    .hasSameSizeAs(expectedData);

            for (int i = 0; i < expectedData.size(); i++) {
                var exp = expectedData.get(i);
                var csvData = exp.getCsvData();
                var claim = claims.get(i);

                if (exp.isSuccess()) {
                    assertThat(claim.isSuccess())
                            .describedAs("Excepted claim at index %d to be processed successfully, " +
                                    "however it failed with error %s", i, claim.getErrorMessage())
                            .isTrue();
                } else {
                    assertThat(claim.isSuccess())
                            .describedAs("Excepted claim at index %d to be processed with fail, and error %s" +
                                    "however it was processed successfully", i, claim.getErrorMessage())
                            .isFalse();
                    assertThat(claim.getErrorMessage())
                            .isEqualTo(exp.getErrorMessage());
                }

                assertThat(claim.getMemberIdentifier()).isEqualTo(csvData.getMemberIdentifier());
                assertThat(claim.getMemberFirstName()).isEqualTo(csvData.getMemberFirstName());
                assertThat(claim.getMemberMiddleName()).isEqualTo(csvData.getMemberMiddleName());
                assertThat(claim.getMemberLastName()).isEqualTo(csvData.getMemberLastName());
                assertThat(claim.getBirthDate()).isEqualTo(csvData.getDateOfBirth());
                assertThat(claim.getDaysSupply()).isEqualTo(csvData.getDaysSupply());
                assertThat(claim.getPrescriberFirstName()).isEqualTo(csvData.getPrescriberFirstName());
                assertThat(claim.getPrescriberMiddleName()).isEqualTo(csvData.getPrescriberMiddleName());
                assertThat(claim.getPrescriberLastName()).isEqualTo(csvData.getPrescriberLastName());
                assertThat(claim.getPrescribingPhysicianNPI()).isEqualTo(csvData.getPrescribingPhysicianNPI());
                assertThat(claim.getCompoundCode()).isEqualTo(csvData.getCompoundCode());
                assertThat(claim.getDAWProductSelectionCode()).isEqualTo(csvData.getDAWProductSelectionCode());
                assertThat(claim.getRefillNumber()).isEqualTo(csvData.getRefillNumber());
                assertThat(claim.getPrescriptionOriginCode()).isEqualTo(csvData.getPrescriptionOriginCode());
                assertThat(claim.getDrugName()).isEqualTo(csvData.getDrugName());
                assertThat(claim.getPlanReportedBrandGenericCode()).isEqualTo(csvData.getPlanReportedBrandGenericCode());
                assertThat(claim.getNationalDrugCode()).isEqualTo(csvData.getNationalDrugCode());
                assertThat(claim.getServiceDate()).isEqualTo(atStartOfDayCentralTime(csvData.getServiceDate()));
                assertThat(claim.getClaimNo()).isEqualTo(csvData.getClaimNo());
                assertThat(claim.getRXNumber()).isEqualTo(csvData.getRXNumber());
                assertThat(claim.getClaimAdjustedFromIdentifier()).isEqualTo(csvData.getClaimAdjustedFromIdentifier());
                assertThat(claim.getRelatedClaimRelationship()).isEqualTo(csvData.getRelatedClaimRelationship());
                assertThat(claim.getQuantityDispensed()).isEqualTo(csvData.getQuantityDispensed());
                assertThat(claim.getQuantityQualifierCode()).isEqualTo(csvData.getQuantityQualifierCode());
                assertThat(claim.getPharmacyName()).isEqualTo(csvData.getPharmacyName());
                assertThat(claim.getClaimBillingProvider()).isEqualTo(csvData.getClaimBillingProvider());
                assertThat(claim.getPharmacyNPI()).isEqualTo(csvData.getPharmacyNPI());


                if (exp.isDuplicate()) {
                    assertThat(claim.getDuplicate()).isTrue();
                } else {
                    assertThat(claim.getDuplicate()).isIn(null, false);
                }

                boolean dispenseIsNull = false;
                String medicationDeleteType = null;

                if (exp.isSuccess()) {
                    if (exp.isDuplicate()) {
                        dispenseIsNull = true;
                    } else {
                        if (exp.getExpectedMedicationOrder() == RxClaimExpectedDataDefinition.MEDICATION_DELETED ||
                                exp.getExpectedDispenseOrder() == RxClaimExpectedDataDefinition.DISPENSE_DELETED) {
                            dispenseIsNull = true;

                            if (exp.getExpectedMedicationOrder() == RxClaimExpectedDataDefinition.MEDICATION_DELETED) {
                                medicationDeleteType = exp.isAdjustment() ?
                                        HealthPartnersUtils.ADJUSTMENT_CAUSED_MEDICATION_DELETE :
                                        HealthPartnersUtils.MEDICATION_DELETED_BY_ADJUSTMENT;
                            } else if (exp.getExpectedDispenseOrder() == RxClaimExpectedDataDefinition.DISPENSE_DELETED) {
                                medicationDeleteType = exp.isAdjustment() ?
                                        HealthPartnersUtils.ADJUSTMENT_CAUSED_DISPENSE_DELETE :
                                        HealthPartnersUtils.DISPENSE_DELETED_BY_ADJUSTMENT;
                            }
                        }
                    }
                } else {
                    dispenseIsNull = true;
                }

                if (dispenseIsNull) {
                    assertThat(claim.getMedicationDispense()).isNull();
                    assertThat(claim.getMedicationDispenseId()).isNull();
                } else {
                    assertThat(claim.getMedicationDispense()).isNotNull();
                    assertThat(claim.getMedicationDispenseId()).isNotNull();
                }

                if (medicationDeleteType == null) {
                    assertThat(claim.getMedicationDeletedType()).isNull();
                } else {
                    assertThat(claim.getMedicationDeletedType()).isEqualTo(medicationDeleteType);
                }

                if (exp.isAdjustment()) {
                    assertThat(claim.getAdjustment()).isTrue();
                } else {
                    assertThat(claim.getAdjustment()).isIn(null, false);
                }
            }
        }

        @Test
        @Transactional
        void validateClientsData() {
            var clients = loadOrderedClientsByExpectedData(expectedData, targetCommunityId);
            var firstClientCsvEntries = expectedData.stream()
                    .filter(RxClaimExpectedDataDefinition::isSuccess)
                    .filter(c -> !c.isDuplicate())
                    .map(RxClaimExpectedDataDefinition::getCsvData)
                    .collect(Collectors.toMap(
                            RxClaimCSVDto::getMemberIdentifier,
                            Function.identity(),
                            (firstEntry, secondEntry) -> firstEntry,
                            HashMap::new
                    ));

            var softly = new SoftAssertions();
            for (var client : clients) {
                verifyClientData(softly, firstClientCsvEntries.get(client.getHealthPartnersMemberIdentifier()), client, true);
            }

            softly.assertAll();
        }

        @Test
        @Transactional
        void validateExistingMedicationsUsedAccordingToTestDataDefinitionCounts() {
            var clients = loadOrderedClientsByExpectedData(expectedData, targetCommunityId);
            var actualMedicationsOrdered = loadOrderedMedications(clients);

            var expectedMedicationCountsByClient = expectedData.stream()
                    .filter(RxClaimExpectedDataDefinition::isSuccess)
                    .filter(c -> !c.isDuplicate())
                    .filter(c -> c.getExpectedMedicationOrder() != RxClaimExpectedDataDefinition.MEDICATION_DELETED)
                    .collect(Collectors.groupingBy(
                            RxClaimExpectedDataDefinition::getExpectedClientOrder,
                            Collectors.mapping(
                                    RxClaimExpectedDataDefinition::getExpectedMedicationOrder,
                                    Collectors.reducing(0, Math::max)
                            )
                    ));


            expectedMedicationCountsByClient.forEach((clientOrder, medsCount) -> {
                var client = clients.get(clientOrder - 1);

                assertThat(actualMedicationsOrdered.get(client.getId()))
                        .describedAs("Test client with order %d, identifier %s medications count",
                                clientOrder,
                                client.getHealthPartnersMemberIdentifier())
                        .hasSize(medsCount);
            });

            var expectedDispenseCountsByMedication = expectedData.stream()
                    .filter(RxClaimExpectedDataDefinition::isSuccess)
                    .filter(c -> !c.isDuplicate())
                    .filter(c -> c.getExpectedDispenseOrder() != RxClaimExpectedDataDefinition.DISPENSE_DELETED)
                    .filter(c -> c.getExpectedMedicationOrder() != RxClaimExpectedDataDefinition.MEDICATION_DELETED)
                    .collect(Collectors.groupingBy(
                            exp -> new Pair<>(exp.getExpectedClientOrder(), exp.getExpectedMedicationOrder()),
                            Collectors.collectingAndThen(
                                    Collectors.mapping(
                                            RxClaimExpectedDataDefinition::getExpectedDispenseOrder,
                                            Collectors.toSet()
                                    ),
                                    Collection::size
                            )
                    ));

            expectedDispenseCountsByMedication.forEach((clientMedOrderPair, dispenseCount) -> {
                var client = clients.get(clientMedOrderPair.getFirst() - 1);
                var medication = actualMedicationsOrdered.get(client.getId()).get(clientMedOrderPair.getSecond() - 1);

                assertThat(medication.getMedicationDispenses())
                        .describedAs("Test client with order %d, identifier %s, medication order %d dispenses count",
                                clientMedOrderPair.getFirst(),
                                client.getHealthPartnersMemberIdentifier(),
                                clientMedOrderPair.getSecond())
                        .hasSize(dispenseCount);
            });

        }

        @Test
        @Transactional
        void validateMedicationsData() {
            var clients = loadOrderedClientsByExpectedData(expectedData, targetCommunityId);
            var actualMedicationsOrdered = loadOrderedMedications(clients);
            var actualDispensesOrdered = orderedDispenses(actualMedicationsOrdered);

            var claims = healthPartnersRxClaimDao.findAllByHpFileLogId(fileLogId, Sort.by(HealthPartnersRxClaim_.ID));

            //medication is created based on first sorted csv entry and not updated even in case of changes
            //it can only be deleted due to adjustments
            var checkedMedications = new HashSet<Long>();

            for (int i = 0; i < expectedData.size(); i++) {
                var exp = expectedData.get(i);
                if (!exp.isSuccess() || exp.isDuplicate() || exp.isAdjustment() ||
                        exp.getExpectedMedicationOrder() == RxClaimExpectedDataDefinition.MEDICATION_DELETED) {
                    continue;
                }
                assertThat(clients.size())
                        .describedAs(
                                "Expected client to be present at order %d for test data definition %s",
                                exp.getExpectedClientOrder(),
                                exp)
                        .isGreaterThanOrEqualTo(exp.getExpectedClientOrder());
                var client = clients.get(exp.getExpectedClientOrder() - 1);

                assertThat(client.getHealthPartnersMemberIdentifier())
//                        .describedAs("Client member identifi")
                        .isEqualTo(exp.getCsvData().getMemberIdentifier());

                var actualClientMedications = actualMedicationsOrdered.get(client.getId());

                assertThat(actualClientMedications.size())
                        .describedAs("Expected client to have at least %d medications for test data definition %s",
                                exp.getExpectedMedicationOrder(), exp)
                        .isGreaterThanOrEqualTo(exp.getExpectedMedicationOrder());

                var medication = actualClientMedications.get(exp.getExpectedMedicationOrder() - 1);

                var softly = new SoftAssertions();
                if (!checkedMedications.contains(medication.getId())) {
                    verifyMedicationData(softly, medication, client.getOrganization(), exp.getCsvData());
                    checkedMedications.add(medication.getId());
                }
                verifyContainsNdcCode(softly, medication.getMedicationInformation().getTranslationProductCodes(),
                        exp.getCsvData().getNationalDrugCode(), exp.getCsvData().getDrugName());

                softly.assertAll();

                if (exp.getExpectedDispenseOrder() != RxClaimExpectedDataDefinition.DISPENSE_DELETED) {
                    var actualMedicationDispenses = actualDispensesOrdered.get(medication.getId());

                    assertThat(actualMedicationDispenses.size())
                            .describedAs("Expected medication to have at least %d dispenses for test data definition %s",
                                    exp.getExpectedDispenseOrder(), exp)
                            .isGreaterThanOrEqualTo(exp.getExpectedDispenseOrder());

                    var dispense = actualMedicationDispenses.get(exp.getExpectedDispenseOrder() - 1);

                    assertThat(dispense.getId())
                            .describedAs("Dispense ids should match at index %d for test data definition %s. " +
                                    "Looks like existing medication wasn't picked correctly", i, exp)
                            .isEqualTo(claims.get(i).getMedicationDispenseId());

                    softly = new SoftAssertions();
                    verifyMedicationDispenseData(softly, dispense, client.getOrganization(), exp);
                    softly.assertAll();
                }
            }
        }

        @Test
        @Transactional
        void validateNdcCodesNotDuplicatedInMedicationInformation() {
            var clients = loadOrderedClientsByExpectedData(expectedData, targetCommunityId);
            medicationDao.findByClient_IdIn(CareCoordinationUtils.toIds(clients, Collectors.toList()))
                    .forEach(medication -> {
                        var translations = medication.getMedicationInformation().getTranslationProductCodes();


                        var deduplicatedTranslationsCount = (int) translations.stream()
                                .map(code -> new Pair<>(code.getCode(), code.getDisplayName()))
                                .distinct()
                                .count();

                        assertThat(translations).hasSize(deduplicatedTranslationsCount);
                    });
        }

        @Test
        void communitiesSignaturePinEnabled() {
            var communities = communityDao.findAll((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.and(),
                    PIN.class);

            communities.forEach(c ->
                    assertThat(c.getIsSignaturePinEnabled()).isTrue()
            );

        }
    }

    @Nested
    @Order(1)
    public class FirstWarnFileImport extends BaseRxFileImportTestCase {
        static final String CSV_FILE_NAME = "rx-test-warn.txt";

        @Override
        String getFileNamePostfix() {
            return "_20210910_121314.txt";
        }

        @Override
        List<RxClaimExpectedDataDefinition> getExpectedData() {
            return HealthPartnersConsanaRxFlowTestSupport.createFirstWarnFileExpectedDefinition();
        }

        @Override
        String getCsvFileName() {
            return CSV_FILE_NAME;
        }

        @Override
        ProcessingSummary.ProcessingStatus getExpectedFileStatus() {
            return ProcessingSummary.ProcessingStatus.WARN;
        }

        @Override
        void additionalActions(List<RxClaimExpectedDataDefinition> expectedData) throws URISyntaxException {
            mockNdcApiRequests(expectedData);
        }

        void mockNdcApiRequests(List<RxClaimExpectedDataDefinition> expectedData) throws URISyntaxException {
            var mockServer = MockRestServiceServer.createServer(ndcApiRestTemplate);
            mockServer.expect(ExpectedCount.once(), requestTo(new URI(rxNormVersionUrl)))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(withStatus(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(RXNORM_VERSION_RESPONSE)
                    );

            expectedData.stream()
                    .filter(RxClaimExpectedDataDefinition::isSuccess)
                    .map(RxClaimExpectedDataDefinition::getCsvData)
                    .map(RxClaimCSVDto::getNationalDrugCode)
                    .distinct()
                    .forEach(ndc -> {
                        try {
                            String response;
                            if (ndcApiResponse.containsKey(ndc)) {
                                response = ndcApiResponse.get(ndc);
                            } else {
                                response = String.format(UNKNOWN_NDC_CODE_RESPONSE_TEMPLATE, ndc);
                            }
                            mockServer.expect(ExpectedCount.once(), requestTo(new URI(ndcStatusUrl + "?ndc=" + ndc + "&history=1")))
                                    .andExpect(method(HttpMethod.GET))
                                    .andRespond(withStatus(HttpStatus.OK)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(response)
                                    );
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }

                    });
        }

        @Autowired
        private CcdGeneratorService ccdGeneratorService;

        //@Test
        @Transactional
        void generateCcd() throws IOException {
            var client = loadOrderedClientsByExpectedData(expectedData, targetCommunityId).get(1);
            var rep = ccdGeneratorService.generate(client.getId(), false);

            var file = new File("ccd-document-rx.xml");
            if (file.exists()) {
                file.delete();
            }
            FileUtils.writeByteArrayToFile(file, rep.getInputStream().readAllBytes(), false);
        }
    }

    @Nested
    @Order(2)
    class SecondOkFileImportWithDuplicates extends BaseRxFileImportTestCase {
        static final String CSV_FILE_NAME = "rx-test-ok-dups.txt";

        @Override
        String getFileNamePostfix() {
            return "_20210910_131415.txt";
        }

        @Override
        List<RxClaimExpectedDataDefinition> getExpectedData() {
            return HealthPartnersConsanaRxFlowTestSupport.createSecondFileWithDuplicatesExpectedDefinition();
        }

        @Override
        String getCsvFileName() {
            return CSV_FILE_NAME;
        }

        @Override
        ProcessingSummary.ProcessingStatus getExpectedFileStatus() {
            return ProcessingSummary.ProcessingStatus.OK;
        }

        @Override
        void additionalActions(List<RxClaimExpectedDataDefinition> expectedData) throws URISyntaxException {
            deactivateClientFromDuplicate(expectedData);

            //test that previously failed claim is not detected as duplicate.
            failClaim(expectedData.get(0));
        }

        @Autowired
        TransactionTemplate transactionTemplate;

        private void deactivateClientFromDuplicate(List<RxClaimExpectedDataDefinition> expectedData) {
            //deactivate one of clients present in duplicates to test that client is activated while duplicate processing
            var duplicateIdentifier = extractDuplicatesMemberIdentifiers(expectedData)
                    .filter(StringUtils::isNotEmpty)
                    .findFirst()
                    .orElseThrow();

            var clientId = clientDao.findFirst(healthPartnersClient(
                            List.of(duplicateIdentifier),
                            targetCommunityId),
                    IdAware.class).orElseThrow().getId();

            transactionTemplate.execute(status -> {
                clientDao.deactivateClient(clientId, Instant.now());
                return null;
            });
        }

        private void failClaim(RxClaimExpectedDataDefinition rxClaimExpectedDataDefinition) {
            var claimToFailExpected = expectedData.get(0);

            var foundClaims = healthPartnersRxClaimDao.findAll((root, query, cb) ->
                    cb.and(
                            cb.equal(root.get(HealthPartnersRxClaim_.memberIdentifier), claimToFailExpected.getCsvData().getMemberIdentifier()),
                            cb.equal(root.get(HealthPartnersRxClaim_.claimNo), claimToFailExpected.getCsvData().getClaimNo())
                    ));

            if (foundClaims.size() != 1) {
                throw new RuntimeException("Didn't find single claim to fail");
            }
            var claimEntityToFail = foundClaims.get(0);
            claimEntityToFail.setSuccess(false);
            healthPartnersRxClaimDao.save(claimEntityToFail);
        }

        @Test
        @Transactional
        void validateClientsFromDuplicateRecordsAreActivated() {
            var duplicateIdentifiers = extractDuplicatesMemberIdentifiers(expectedData);

            var clients = clientDao.findAll(
                    healthPartnersClient(duplicateIdentifiers.collect(Collectors.toSet()), targetCommunityId),
                    ActiveAware.class
            );

            var softly = new SoftAssertions();
            for (var client : clients) {
                softly.assertThat(client.getActive()).isTrue();
            }

            softly.assertAll();
        }


        private Stream<String> extractDuplicatesMemberIdentifiers(List<RxClaimExpectedDataDefinition> expectedData) {
            return expectedData.stream()
                    .filter(RxClaimExpectedDataDefinition::isSuccess)
                    .filter(RxClaimExpectedDataDefinition::isDuplicate)
                    .map(RxClaimExpectedDataDefinition::getCsvData)
                    .map(RxClaimCSVDto::getMemberIdentifier);
        }
    }


    private Set<String> extractMemberIdentifiers(List<RxClaimExpectedDataDefinition> expectedData) {
        return expectedData.stream()
                .filter(RxClaimExpectedDataDefinition::isSuccess)
                .filter(c -> !c.isDuplicate())
                .map(RxClaimExpectedDataDefinition::getCsvData)
                .map(RxClaimCSVDto::getMemberIdentifier)
                .collect(Collectors.toSet());
    }

    private Map<Long, List<MedicationDispense>> orderedDispenses(Map<Long, List<Medication>> medications) {
        return medications.values()
                .stream()
                .flatMap(Collection::stream)
                .collect(Collectors.groupingBy(Medication::getId, Collectors.flatMapping(
                        med -> med.getMedicationDispenses().stream().sorted(Comparator.comparing(MedicationDispense::getId)),
                        Collectors.toList())
                ));
    }

    private Map<Long, List<Medication>> loadOrderedMedications(List<Client> clients) {
        var map = medicationDao.findByClient_IdIn(CareCoordinationUtils.toIds(clients, Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(Medication::getClientId));
        map.forEach((clientId, meds) -> meds.sort(Comparator.comparing(Medication::getId)));
        return map;
    }

    private List<Client> loadOrderedClientsByExpectedData(List<RxClaimExpectedDataDefinition> expectedData, Long
            targetCommunityId) {
        return loadOrderedClients(extractMemberIdentifiers(expectedData), targetCommunityId);
    }

    private InputStream loadTestCsv(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "h2\\healthpartners\\rx-claims\\" + name);
    }

    private void verifyMedicationData(SoftAssertions softly, Medication medication, Organization organization, RxClaimCSVDto csvData) {
        softly.assertThat(medication.getOrganization()).isEqualTo(organization);

        softly.assertThat(
                        Optional.ofNullable(medication.getMedicationStarted())
                                .map(Date::toInstant)
                                .orElse(null))
                .isEqualTo(atStartOfDayCentralTime(csvData.getServiceDate()));

        verifyMedicationInformation(softly, medication.getMedicationInformation(), organization, csvData);
        verifyMedicationSupplyOrder(softly, medication.getMedicationSupplyOrder(), organization, csvData);
    }

    private void verifyMedicationInformation(SoftAssertions softly, MedicationInformation medicationInformation,
                                             Organization organization, RxClaimCSVDto rxClaimCSVDto) {
        assertThat(medicationInformation).isNotNull();

        softly.assertThat(medicationInformation.getOrganization()).isEqualTo(organization);

        if (ndcRxNormMapping.containsKey(rxClaimCSVDto.getNationalDrugCode())) {
            var code = ndcRxNormMapping.get(rxClaimCSVDto.getNationalDrugCode());
            softly.assertThat(medicationInformation.getProductNameCode()).isNotNull();

            softly.assertThat(Optional.ofNullable(medicationInformation.getProductNameCode()))
                    .map(CcdCode::getCode)
                    .contains(code.getFirst());

            softly.assertThat(Optional.ofNullable(medicationInformation.getProductNameCode()))
                    .map(CcdCode::getDisplayName)
                    .contains(code.getSecond());

            softly.assertThat(Optional.ofNullable(medicationInformation.getProductNameCode()))
                    .map(CcdCode::getCodeSystem)
                    .contains(CodeSystem.RX_NORM.getOid());

            softly.assertThat(medicationInformation.getProductNameText()).isNullOrEmpty();
        } else {
            softly.assertThat(medicationInformation.getProductNameCode()).isNull();
            softly.assertThat(medicationInformation.getProductNameText()).isEqualTo(rxClaimCSVDto.getDrugName());
        }
    }

    private void verifyMedicationSupplyOrder(SoftAssertions softly, MedicationSupplyOrder medicationSupplyOrder,
                                             Organization organization, RxClaimCSVDto rxClaimCSVDto) {
        softly.assertThat(medicationSupplyOrder.getOrganization()).isEqualTo(organization);


        softly.assertThat(medicationSupplyOrder.getDAWProductSelectionCode()).isEqualTo(rxClaimCSVDto.getDAWProductSelectionCode());
        softly.assertThat(medicationSupplyOrder.getPrescriptionOriginCode()).isEqualTo(rxClaimCSVDto.getPrescriptionOriginCode());
        softly.assertThat(medicationSupplyOrder.getPrescriptionNumber()).isEqualTo(rxClaimCSVDto.getRXNumber());

        verifyAuthor(softly, medicationSupplyOrder.getAuthor(), organization, rxClaimCSVDto);
        verifyMedicalProfessional(softly, medicationSupplyOrder.getMedicalProfessional(), organization, rxClaimCSVDto);
    }

    private void verifyMedicalProfessional(SoftAssertions softly, MedicalProfessional medicalProfessional,
                                           Organization organization, RxClaimCSVDto rxClaimCSVDto) {

        softly.assertThat(medicalProfessional.getOrganization()).isEqualTo(organization);
        softly.assertThat(medicalProfessional.getNpi()).isEqualTo(rxClaimCSVDto.getPrescribingPhysicianNPI());

        verifyPerson(softly, medicalProfessional.getPerson(), "medical professional", organization,
                rxClaimCSVDto.getPrescriberFirstName(),
                rxClaimCSVDto.getPrescriberMiddleName(),
                rxClaimCSVDto.getPrescriberLastName()
        );
    }


    private void verifyAuthor(SoftAssertions softly, Author author, Organization organization,
                              RxClaimCSVDto rxClaimCSVDto) {
        softly.assertThat(author.getOrganization()).isEqualTo(organization);
        softly.assertThat(author.getCommunity()).isNull();

        verifyPerson(softly, author.getPerson(), "author", organization,
                rxClaimCSVDto.getPrescriberFirstName(),
                rxClaimCSVDto.getPrescriberMiddleName(),
                rxClaimCSVDto.getPrescriberLastName());
    }


    private void verifyMedicationDispenseData(SoftAssertions softly, MedicationDispense dispense, Organization organization, RxClaimExpectedDataDefinition exp) {
        var csv = exp.getCsvData();
        softly.assertThat(dispense.getOrganization()).isEqualTo(organization);

        var start = atStartOfDayCentralTime(csv.getServiceDate());
        var daysSupply = csv.getDaysSupply();
        var quantity = csv.getQuantityDispensed();
        for (var adjustment : exp.getAdjustedBy()) {
            daysSupply += adjustment.getCsvData().getDaysSupply();
            quantity = quantity.add(adjustment.getCsvData().getQuantityDispensed());
        }

        Integer finalDaysSupply = daysSupply;
        var end = Optional.ofNullable(start).map(d -> d.plus(finalDaysSupply, ChronoUnit.DAYS)).orElse(null);

        softly.assertThat(
                        Optional.ofNullable(dispense.getDispenseDateLow())
                                .map(Date::toInstant)
                                .orElse(null))
                .isEqualTo(start);

        softly.assertThat(
                        Optional.ofNullable(dispense.getDispenseDateHigh())
                                .map(Date::toInstant)
                                .orElse(null)
                )
                .isEqualTo(end);

        softly.assertThat(dispense.getFillNumber()).isEqualTo(csv.getRefillNumber());
        softly.assertThat(dispense.getPrescriptionNumber()).isEqualTo(csv.getRXNumber());
        softly.assertThat(dispense.getQuantity()).isEqualTo(quantity);
        softly.assertThat(dispense.getQuantityQualifierCode()).isEqualTo(csv.getQuantityQualifierCode());

        verifyProvider(softly, dispense.getProvider(), organization, csv);
    }


    private void verifyContainsNdcCode(SoftAssertions softly, List<CcdCode> translationCodes, String nationalDrugCode, String drugName) {
        softly.assertThat(translationCodes).isNotEmpty();
        softly.assertThat(translationCodes).anyMatch(
                translationCode -> CodeSystem.NDC.getOid().equals(translationCode.getCodeSystem()) &&
                        StringUtils.equals(nationalDrugCode, translationCode.getCode()) &&
                        StringUtils.equals(drugName, translationCode.getDisplayName())
        );
    }

    private void verifyProvider(SoftAssertions softly, Community provider, Organization organization, RxClaimCSVDto rxClaimCSVDto) {
        softly.assertThat(provider.getOrganization()).isEqualTo(organization);
        softly.assertThat(provider.getName()).isEqualTo(rxClaimCSVDto.getPharmacyName());
        softly.assertThat(provider.getProviderNpi()).isEqualTo(rxClaimCSVDto.getPharmacyNPI());
        softly.assertThat(provider.getModuleHie()).isTrue();
        softly.assertThat(provider.getHealthPartnersBillingProviderRef()).isEqualTo(rxClaimCSVDto.getClaimBillingProvider());
    }
}