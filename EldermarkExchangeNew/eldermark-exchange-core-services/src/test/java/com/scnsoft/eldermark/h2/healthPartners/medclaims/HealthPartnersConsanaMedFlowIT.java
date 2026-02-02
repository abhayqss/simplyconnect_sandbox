package com.scnsoft.eldermark.h2.healthPartners.medclaims;

import com.scnsoft.eldermark.beans.ValueSetEnum;
import com.scnsoft.eldermark.beans.projection.ActiveAware;
import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.CcdCodeDao;
import com.scnsoft.eldermark.dao.ProblemDao;
import com.scnsoft.eldermark.dao.UnknownCcdCodeDao;
import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersMedClaimDao;
import com.scnsoft.eldermark.dto.healthpartners.HpCsvRecord;
import com.scnsoft.eldermark.dto.healthpartners.MedClaimCSVDto;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.document.CcdCode;
import com.scnsoft.eldermark.entity.document.UnknownCcdCode_;
import com.scnsoft.eldermark.entity.document.ccd.Author;
import com.scnsoft.eldermark.entity.document.ccd.CodeSystem;
import com.scnsoft.eldermark.entity.document.ccd.Problem;
import com.scnsoft.eldermark.entity.document.ccd.ProblemObservation;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersMedClaim_;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.h2.healthPartners.BaseHealthPartnersIT;
import com.scnsoft.eldermark.service.document.cda.generator.CcdGeneratorService;
import com.scnsoft.eldermark.shared.carecoordination.utils.Pair;
import com.scnsoft.eldermark.util.CareCoordinationUtils;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.dbunit.DatabaseUnitException;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.scnsoft.eldermark.h2.healthPartners.HealthPartnersITSupport.atStartOfDayCentralTime;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class HealthPartnersConsanaMedFlowIT extends BaseHealthPartnersIT {

    private static final Logger logger = LoggerFactory.getLogger(HealthPartnersConsanaMedFlowIT.class);

    @Autowired
    private ProblemDao problemDao;

    @Autowired
    private HealthPartnersMedClaimDao healthPartnersMedClaimDao;

    @Autowired
    private CcdCodeDao ccdCodeDao;

    @Autowired
    private UnknownCcdCodeDao unknownCcdCodeDao;

    @BeforeAll
    void importCcdCodes() throws DatabaseUnitException, SQLException {
        importDataset("import-ccd-codes.xml");
    }

    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    abstract class BaseMedClaimImport extends BaseHealthPartnersIT {
        Long fileLogId;
        Long targetCommunityId;
        List<MedClaimExpectedDataDefinition> expectedData;

        abstract String getFileNamePostfix();

        abstract List<MedClaimExpectedDataDefinition> getExpectedData();

        abstract String getCsvFileName();

        abstract ProcessingSummary.ProcessingStatus getExpectedFileStatus();

        abstract void additionalActions(List<MedClaimExpectedDataDefinition> expectedData);

        @BeforeAll
        void processValidSftpFile() throws IOException, DatabaseUnitException, SQLException {
            importHpOrgsAndCommunities();

            var filename = HpFileType.CONSANA_MEDICAL.name() + getFileNamePostfix();
            targetCommunityId = communityDao.findByOrganization_AlternativeIdAndOid(hpOrgAlternativeId, hpCommunityOid, IdAware.class)
                    .orElseThrow().getId();

            expectedData = getExpectedData();
            final int[] i = {2};
            expectedData.forEach(ed -> ed.getCsvData().setLineNumber(i[0]++));

            expectedData.sort(
                    Comparator
                            .comparing((Function<MedClaimExpectedDataDefinition, String>) ed -> StringUtils.defaultString(ed.getCsvData().getMemberIdentifier()))
                            .thenComparing(ed -> Optional.ofNullable(ed.getCsvData().getClaimNo()).map(String::length).orElse(0))
                            .thenComparing(ed -> StringUtils.defaultString(ed.getCsvData().getClaimNo()))
                            .thenComparing(ed -> StringUtils.defaultString(ed.getCsvData().getDiagnosisCode()))
            );

//            expectedData
//                    .forEach(ed -> System.out.printf("line %d, member %s, claimno %s, diagnosis %s, description %s\r\n",
//                            ed.getCsvData().getLineNumber(), ed.getCsvData().getMemberIdentifier(),
//                            ed.getCsvData().getClaimNo(), ed.getCsvData().getDiagnosisCode(), ed.getDescription()));


            additionalActions(expectedData);

            fileLogId = putFileToLocalCacheAndWaitUntilProcessed(loadTestCsv(getCsvFileName()), filename);
        }

        @Test
        void fileLogShouldHaveExpectedStatus() {
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
        void validateThatMedClaimEntityCreatedForEveryRecord() {
            var claims = healthPartnersMedClaimDao.findAllByHpFileLogId(fileLogId, Sort.by(HealthPartnersMedClaim_.ID));

            assertThat(claims)
                    .describedAs("HealthPartnersMedClaim should be created for each claim in input file")
                    .hasSameSizeAs(expectedData);

            for (int i = 0; i < expectedData.size(); i++) {
                var csvData = expectedData.get(i).getCsvData();
                var claim = claims.get(i);
                System.out.println(claim);

                if (expectedData.get(i).isSuccess()) {
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
                            .isEqualTo(expectedData.get(i).getErrorMessage());
                }

                assertThat(claim.getMemberIdentifier()).isEqualTo(csvData.getMemberIdentifier());
                assertThat(claim.getMemberFirstName()).isEqualTo(csvData.getMemberFirstName());
                assertThat(claim.getMemberMiddleName()).isEqualTo(csvData.getMemberMiddleName());
                assertThat(claim.getMemberLastName()).isEqualTo(csvData.getMemberLastName());
                assertThat(claim.getBirthDate()).isEqualTo(csvData.getDateOfBirth());
                assertThat(claim.getClaimNo()).isEqualTo(csvData.getClaimNo());
                assertThat(claim.getServiceDate()).isEqualTo(atStartOfDayCentralTime(csvData.getServiceDate()));
                assertThat(claim.getIcdVersion()).isEqualTo(csvData.getIcdVersion());
                assertThat(claim.getDiagnosisCode()).isEqualTo(csvData.getDiagnosisCode());
                assertThat(claim.getDiagnosisTxt()).isEqualTo(csvData.getDiagnosisTxt());
                assertThat(claim.getPhysicianFirstName()).isEqualTo(csvData.getPhysicianFirstName());
                assertThat(claim.getPhysicianMiddleName()).isEqualTo(csvData.getPhysicianMiddleName());
                assertThat(claim.getPhysicianLastName()).isEqualTo(csvData.getPhysicianLastName());

                if (expectedData.get(i).isSuccess()) {
                    assertThat(claim.getDuplicate()).isEqualTo(expectedData.get(i).isDuplicate());

                    if (!expectedData.get(i).isDuplicate()) {
                        assertThat(claim.getProblemObservation()).isNotNull();
                        assertThat(claim.getProblemObservationId()).isNotNull();
                    } else {
                        assertThat(claim.getProblemObservation()).isNull();
                        assertThat(claim.getProblemObservation()).isNull();
                    }
                } else {
                    if (expectedData.get(i).isDuplicate()) {
                        assertThat(claim.getDuplicate()).isTrue();
                    } else {
                        assertThat(claim.getDuplicate()).isIn(null, false);
                    }

                    assertThat(claim.getProblemObservation()).isNull();
                    assertThat(claim.getProblemObservation()).isNull();
                }
            }
        }

        @Test
        @Transactional
        void validateClientsData() {
            var clients = loadOrderedClientsByExpectedData(expectedData, targetCommunityId);
            var firstClientCsvEntries = expectedData.stream()
                    .filter(MedClaimExpectedDataDefinition::isSuccess)
                    .filter(c -> !c.isDuplicate())
                    .map(MedClaimExpectedDataDefinition::getCsvData)
                    .collect(Collectors.toMap(
                            MedClaimCSVDto::getMemberIdentifier,
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
        void validateClientProblemsAccordingToTestDataDefinitionCounts() {
            var clients = loadOrderedClientsByExpectedData(expectedData, targetCommunityId);
            var actualProblemsOrdered = loadOrderedProblems(clients);

            var expectedProblemCountsByClient = expectedData.stream()
                    .filter(MedClaimExpectedDataDefinition::isSuccess)
                    .filter(c -> !c.isDuplicate())
                    .collect(Collectors.groupingBy(
                            MedClaimExpectedDataDefinition::getExpectedClientOrder,
                            Collectors.mapping(
                                    MedClaimExpectedDataDefinition::getExpectedProblemOrder,
                                    Collectors.reducing(0, Math::max)
                            )
                    ));


            expectedProblemCountsByClient.forEach((clientOrder, problemsCount) -> {
                var client = clients.get(clientOrder - 1);

                assertThat(actualProblemsOrdered.get(client.getId()))
                        .describedAs("Test client with order %d, identifier %s problems count",
                                clientOrder,
                                client.getHealthPartnersMemberIdentifier())
                        .hasSize(problemsCount);
            });
        }

        @Test
        @Transactional
        void validateProblemsData() {
            var clients = loadOrderedClientsByExpectedData(expectedData, targetCommunityId);
            var actualProblemsOrdered = loadOrderedProblems(clients);

            for (int i = 0; i < expectedData.size(); i++) {
                var exp = expectedData.get(i);
                if (!exp.isSuccess() || exp.isDuplicate()) {
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
                        .isEqualTo(exp.getCsvData().getMemberIdentifier());

                var actualClientProblems = actualProblemsOrdered.get(client.getId());

                assertThat(actualClientProblems.size())
                        .describedAs("Expected client to have at least %d problems for test data definition %s",
                                exp.getExpectedProblemOrder(), exp)
                        .isGreaterThanOrEqualTo(exp.getExpectedProblemOrder());

                var problem = actualClientProblems.get(exp.getExpectedProblemOrder() - 1);

                assertThat(problem.getProblemObservations()).hasSize(1);
                var observation = problem.getProblemObservations().get(0);

                assertThat(observation.getProblem()).isEqualTo(problem);

                var softly = new SoftAssertions();
                verifyProblemData(softly, problem, client.getOrganization(), exp.getCsvData());
                verifyProblemObservationData(softly, observation, client.getOrganization(), exp);
                softly.assertAll();

            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    //DirtiesContext because processing occurs in new transaction data is not rolled back after test execution
    //todo investigate approach to delete data
    @Order(1)
    public class FirstWarnFileImport extends BaseMedClaimImport {
        static final String CSV_FILE_NAME = "med-claims-warn.txt";

        @Override
        String getFileNamePostfix() {
            return "_20210910_121314.txt";
        }

        @Override
        List<MedClaimExpectedDataDefinition> getExpectedData() {
            return HealthPartnersConsanaMedFlowTestSupport.createFirstWarnFileExpectedDefinition();
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
        void additionalActions(List<MedClaimExpectedDataDefinition> expectedData) {
            //do nothing
        }

        @Test
        @Transactional
        void validateCcdCodes() {
            //todo think how to check codes for second file
            var allCodesCount = ccdCodeDao.count();

            var expectedCreatedCodes = expectedData.stream()
                    .filter(MedClaimExpectedDataDefinition::isSuccess)
                    .filter(c -> !c.isDuplicate())
                    .filter(MedClaimExpectedDataDefinition::isShouldCreateCode)
                    .map(MedClaimExpectedDataDefinition::getCsvData)
                    .map(csvData -> new Pair<>(new Pair<>(csvData.getDiagnosisCode(), csvData.getDiagnosisTxt()), resolveCodeSystem(csvData.getIcdVersion())))
                    .collect(Collectors.toList());

            assertThat(allCodesCount)
                    .isEqualTo(1 +              //diagnosis type
                            2 +                 //pre-imported codes
                            expectedCreatedCodes.size()   //created codes
                    );

            var unknownCodes = unknownCcdCodeDao.findAll(Sort.by(UnknownCcdCode_.ID));

            assertThat(unknownCodes).hasSameSizeAs(expectedCreatedCodes);

            for (int i = 0; i < unknownCodes.size(); i++) {
                var unknownCode = unknownCodes.get(i);
                var expected = expectedCreatedCodes.get(i);

                assertThat(unknownCode.getCode())
                        .isEqualTo(expected.getFirst().getFirst());

                assertThat(unknownCode.getDisplayName())
                        .isEqualTo(expected.getFirst().getSecond());

                assertThat(unknownCode.getCodeSystem())
                        .isEqualTo(expected.getSecond().getOid());

                assertThat(unknownCode.getCodeSystemName())
                        .isEqualTo(expected.getSecond().getDisplayName());
            }
        }

        @Autowired
        private CcdGeneratorService ccdGeneratorService;

        @Test
//        @Transactional
        void generateCcd() throws IOException {
            var client = loadOrderedClientsByExpectedData(expectedData, targetCommunityId).get(1);
            var rep = ccdGeneratorService.generate(client.getId(), false);

            var file = new File("ccd-document-med.xml");
            if (file.exists()) {
                file.delete();
            }
            FileUtils.writeByteArrayToFile(file, rep.getInputStream().readAllBytes(), false);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    //DirtiesContext because processing occurs in new transaction data is not rolled back after test execution
    //todo investigate approach to delete data
    @Order(2)
    public class SecondOkFileWithDuplicatesImport extends BaseMedClaimImport {
        static final String CSV_FILE_NAME = "med-claims-ok-duplicates.txt";

        @Override
        String getFileNamePostfix() {
            return "_20210910_131415.txt";
        }

        @Override
        List<MedClaimExpectedDataDefinition> getExpectedData() {
            return HealthPartnersConsanaMedFlowTestSupport.createOkWithDuplicatesFileExpectedDefinition();
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
        void additionalActions(List<MedClaimExpectedDataDefinition> expectedData) {
            deactivateClientFromDuplicate(expectedData);

            failClaim(expectedData.get(0));
        }

        @Autowired
        TransactionTemplate transactionTemplate;

        private void deactivateClientFromDuplicate(List<MedClaimExpectedDataDefinition> expectedData) {
            //deactivate one of clients present in duplicates to test that client is activated for duplicate processing
            var duplicateIdentifier = extractDuplicatesMemberIdentifiers(expectedData)
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

        private void failClaim(MedClaimExpectedDataDefinition rxClaimExpectedDataDefinition) {
            var claimToFailExpected = expectedData.get(0);

            var foundClaims = healthPartnersMedClaimDao.findAll((root, query, cb) ->
                    cb.and(
                            cb.equal(root.get(HealthPartnersMedClaim_.memberIdentifier), claimToFailExpected.getCsvData().getMemberIdentifier()),
                            cb.equal(root.get(HealthPartnersMedClaim_.claimNo), claimToFailExpected.getCsvData().getClaimNo())
                    ));

            if (foundClaims.size() != 1) {
                throw new RuntimeException("Didn't find single claim to fail");
            }
            var claimEntityToFail = foundClaims.get(0);
            claimEntityToFail.setSuccess(false);
            healthPartnersMedClaimDao.save(claimEntityToFail);
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

        private Stream<String> extractDuplicatesMemberIdentifiers(List<MedClaimExpectedDataDefinition> expectedData) {
            return expectedData.stream()
                    .filter(MedClaimExpectedDataDefinition::isSuccess)
                    .filter(MedClaimExpectedDataDefinition::isDuplicate)
                    .map(MedClaimExpectedDataDefinition::getCsvData)
                    .map(MedClaimCSVDto::getMemberIdentifier);
        }
    }

    private void verifyProblemData(SoftAssertions softly, Problem problem, Organization organization, MedClaimCSVDto csvData) {
        softly.assertThat(problem).isNotNull();

        softly.assertThat(problem.getOrganization()).isEqualTo(organization);
        softly.assertThat(problem.getOrganizationId()).isEqualTo(organization.getId());

        softly.assertThat(problem.getStatusCode()).isEqualTo("active");

        var sd = atStartOfDayCentralTime(csvData.getServiceDate());
        softly.assertThat(DateTimeUtils.toEpochMilli(problem.getTimeLow()))
                .describedAs("Problem low date %s should be equal to service date %s", problem.getTimeLow(), sd)
                .isEqualTo(DateTimeUtils.toEpochMilli(sd));

        softly.assertThat(problem.getTimeHigh()).isNull();
        softly.assertThat(problem.getLegacyId()).isEqualTo(0);
        softly.assertThat(problem.getRank()).isNull();
    }

    private void verifyProblemObservationData(SoftAssertions softly, ProblemObservation observation, Organization organization,
                                              MedClaimExpectedDataDefinition exp) {
        var csvData = exp.getCsvData();
        softly.assertThat(observation.getOrganization()).isEqualTo(organization);
        softly.assertThat(observation.getOrganizationId()).isEqualTo(organization.getId());

        var sd = atStartOfDayCentralTime(csvData.getServiceDate());
        softly.assertThat(DateTimeUtils.toEpochMilli(observation.getProblemDateTimeLow()))
                .describedAs("Problem observation low date %s should be equal to service date %s",
                        observation.getProblemDateTimeLow(), sd)
                .isEqualTo(DateTimeUtils.toEpochMilli(sd));

        verifyTypeCcdCode(softly, observation.getProblemType(), "282291009", "2.16.840.1.113883.6.96",
                ValueSetEnum.PROBLEM_TYPE_2006.getOid()); // diagnosis

        var codeSystem = resolveCodeSystem(csvData.getIcdVersion());
        verifyValueCcdCode(softly, observation.getProblemCode(), csvData.getDiagnosisCode(), codeSystem.getOid(),
                csvData.getDiagnosisTxt(), exp.isShouldCreateCode());

        assertThat(observation.getProblemIcdCode()).isEqualTo(csvData.getDiagnosisCode());
        assertThat(observation.getProblemIcdCodeSet()).isEqualTo(codeSystem.getDisplayName());

        verifyAuthor(softly, observation.getAuthor(), organization, csvData);
        assertThat(observation.getManual()).isFalse();

        assertThat(observation.getLegacyId()).isEqualTo(0);
        assertThat(observation.getProblemDateTimeHigh()).isNull();
        assertThat(observation.getAgeObservationUnit()).isNull();
        assertThat(observation.getAgeObservationValue()).isNull();
        assertThat(observation.getHealthStatusObservationText()).isNull();
        assertThat(observation.getProblemName()).isNull();
        assertThat(observation.getProblemStatusText()).isNull();
        assertThat(observation.getNegationInd()).isNull();
        assertThat(observation.getHealthStatusCode()).isNull();
        assertThat(observation.getPrimary()).isNull();
        assertThat(observation.getRecordedDate()).isNull();
        assertThat(observation.getComments()).isNull();
        assertThat(observation.getConsanaId()).isNull();
    }

    private void verifyValueCcdCode(SoftAssertions softly, CcdCode ccdCode, String code, String codeSystem, String csvDiagnosisName,
                                    boolean isNewCode) {
        softly.assertThat(ccdCode).isNotNull();
        softly.assertThat(ccdCode.getCode()).isEqualTo(code);
        softly.assertThat(ccdCode.getCodeSystem()).isEqualTo(codeSystem);
        if (isNewCode) {
            softly.assertThat(ccdCode.getDisplayName()).isEqualTo(csvDiagnosisName);
        }
    }

    private void verifyTypeCcdCode(SoftAssertions softly, CcdCode ccdCode, String code, String codeSystem, String valueSet) {
        softly.assertThat(ccdCode).isNotNull();
        softly.assertThat(ccdCode.getCode()).isEqualTo(code);
        softly.assertThat(ccdCode.getCodeSystem()).isEqualTo(codeSystem);
        softly.assertThat(ccdCode.getValueSet()).isEqualTo(valueSet);
    }

    private void verifyAuthor(SoftAssertions softly, Author author, Organization organization,
                              MedClaimCSVDto csvData) {
        if (StringUtils.isAllEmpty(csvData.getPhysicianFirstName(), csvData.getPhysicianMiddleName(), csvData.getPhysicianLastName())) {
            softly.assertThat(author).isNull();
        } else {
            softly.assertThat(author.getOrganization()).isEqualTo(organization);
            softly.assertThat(author.getCommunity()).isNull();

            verifyPerson(softly, author.getPerson(), "author", organization,
                    csvData.getPhysicianFirstName(),
                    csvData.getPhysicianMiddleName(),
                    csvData.getPhysicianLastName());
        }
    }

    private CodeSystem resolveCodeSystem(Integer version) {
        if (version == null || version.equals(10)) {
            return CodeSystem.ICD_10_CM;
        }
        if (version.equals(9)) {
            return CodeSystem.ICD_9_CM;
        }
        throw new RuntimeException("Unresolved code system " + version);
    }

    private Set<String> extractMemberIdentifiers(List<MedClaimExpectedDataDefinition> expectedData) {
        return expectedData.stream()
                .filter(MedClaimExpectedDataDefinition::isSuccess)
                .filter(c -> !c.isDuplicate())
                .map(MedClaimExpectedDataDefinition::getCsvData)
                .map(HpCsvRecord::getMemberIdentifier)
                .collect(Collectors.toSet());
    }

    private Map<Long, List<Problem>> loadOrderedProblems(List<Client> clients) {
        var map = problemDao.listByClientIds(CareCoordinationUtils.toIds(clients, Collectors.toList()))
                .stream()
                .collect(Collectors.groupingBy(Problem::getClientId));
        map.forEach((clientId, problems) -> problems.sort(Comparator.comparing(Problem::getId)));
        return map;
    }

    private List<Client> loadOrderedClientsByExpectedData(List<MedClaimExpectedDataDefinition> expectedData, Long
            targetCommunityId) {
        return loadOrderedClients(extractMemberIdentifiers(expectedData), targetCommunityId);
    }

    private InputStream loadTestCsv(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "h2\\healthpartners\\med-claims\\" + name);
    }
}