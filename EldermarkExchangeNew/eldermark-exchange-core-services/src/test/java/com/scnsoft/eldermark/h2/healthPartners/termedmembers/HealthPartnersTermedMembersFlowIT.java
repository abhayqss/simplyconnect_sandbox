package com.scnsoft.eldermark.h2.healthPartners.termedmembers;

import com.scnsoft.eldermark.beans.projection.IdAware;
import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersTermedMemberDao;
import com.scnsoft.eldermark.dto.healthpartners.HpCsvRecord;
import com.scnsoft.eldermark.dto.healthpartners.RxClaimCSVDto;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersRxClaim_;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.h2.BaseH2IT;
import com.scnsoft.eldermark.h2.healthPartners.BaseHealthPartnersIT;
import org.assertj.core.api.SoftAssertions;
import org.dbunit.DatabaseUnitException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
//DirtiesContext because processing occurs in new transaction data is not rolled back after test execution
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class HealthPartnersTermedMembersFlowIT extends BaseHealthPartnersIT {

    @Autowired
    private HealthPartnersTermedMemberDao healthPartnersTermedMemberDao;

    Long targetCommunityId;
    Long firstTermedFileLogId;
    List<TermedMemberExpectedDataDefinition> termedMembersExpectedData;

    @BeforeAll
    void init() throws DatabaseUnitException, SQLException {
        importHpOrgsAndCommunities();

        targetCommunityId = communityDao.findByOrganization_AlternativeIdAndOid(hpOrgAlternativeId, hpCommunityOid, IdAware.class)
                .orElseThrow().getId();
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Order(1)
    public class ValidTermedMembersImport extends BaseH2IT {
        static final String CSV_FILE_NAME = "termed-member.txt";

        @BeforeAll
        void init() throws IOException {
            termedMembersExpectedData = HealthPartnersTermedMembersTestSupport.termedMembersExpectedDefinition();


            var filename = HpFileType.CONSANA_TERMED_MEMBERS.name() + "_20210910_121314.txt";
            firstTermedFileLogId = putFileToLocalCacheAndWaitUntilProcessed(
                    loadTestCsv(CSV_FILE_NAME),
                    filename
            );
        }

        @Test
        void firstFileProcessedSuccessfully() {
            assertFileProcessedSuccess(healthPartnersFileLogDao.findById(firstTermedFileLogId));
        }

        @Test
        @Transactional
        void validateTermedMemberEntityCreatedForEveryRecord() {
            var termedMembers = healthPartnersTermedMemberDao.findAllByHpFileLogId(firstTermedFileLogId, Sort.by(HealthPartnersRxClaim_.ID));

            //todo add failed claims
            assertThat(termedMembers)
                    .describedAs("HealthPartnersTermedMember should be created for each record in input file")
                    .hasSameSizeAs(termedMembersExpectedData);

            for (int i = 0; i < termedMembersExpectedData.size(); i++) {
                var csvData = termedMembersExpectedData.get(i).getCsvData();
                var termedMember = termedMembers.get(i);

                assertThat(termedMember.isSuccess())
                        .describedAs("Excepted termed member at index %d to be processed successfully, " +
                                "however it failed with error %s", i, termedMember.getErrorMessage())
                        .isTrue();

                assertThat(termedMember.getMemberIdentifier()).isEqualTo(csvData.getMemberIdentifier());
                assertThat(termedMember.getMemberFirstName()).isEqualTo(csvData.getMemberFirstName());
                assertThat(termedMember.getMemberMiddleName()).isEqualTo(csvData.getMemberMiddleName());
                assertThat(termedMember.getMemberLastName()).isEqualTo(csvData.getMemberLastName());
                assertThat(termedMember.getBirthDate()).isEqualTo(csvData.getDateOfBirth());
            }
        }

        @Test
        @Transactional
        void validateClientIsCreatedForUniqueMemberIdentifier() {
            var uniqueMemberIdentifiers = extractMemberIdentifiersFromExpected(termedMembersExpectedData);
            var clients = loadOrderedClients(uniqueMemberIdentifiers, targetCommunityId);

            assertThat(clients).hasSize(uniqueMemberIdentifiers.size());
        }

        @Test
        @Transactional
        void validateClientsData() {
            var clients = loadOrderedClients(extractMemberIdentifiersFromExpected(termedMembersExpectedData), targetCommunityId);
            var firstClientCsvEntries = termedMembersExpectedData.stream()
                    .map(TermedMemberExpectedDataDefinition::getCsvData)
                    .collect(Collectors.toMap(
                            HpCsvRecord::getMemberIdentifier,
                            Function.identity(),
                            (firstEntry, secondEntry) -> firstEntry,
                            HashMap::new
                    ));

            var softly = new SoftAssertions();
            for (var client : clients) {
                verifyClientData(softly, firstClientCsvEntries.get(client.getHealthPartnersMemberIdentifier()), client, false);
            }

            softly.assertAll();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Order(2)
    public class ClaimsImportToActivateTermedMembers extends BaseH2IT {
        static final String CSV_FILE_NAME = "rx-claims.txt";

        List<RxClaimCSVDto> rxClaims;
        Long claimFileLogId;

        @BeforeAll
        void init() throws IOException {
            rxClaims = HealthPartnersTermedMembersTestSupport.rxClaims();

            claimFileLogId = putFileToLocalCacheAndWaitUntilProcessed(
                    loadTestCsv(CSV_FILE_NAME),
                    HpFileType.CONSANA_RX.name() + "_20210910_121516.txt"
            );
        }

        @Test
        void claimsFileProcessedSuccessfully() {
            assertFileProcessedSuccess(healthPartnersFileLogDao.findById(claimFileLogId));
        }

        @Test
        @Transactional
        void validateClaimImportActivatesClients() {
            var clientsFromClaim = extractMemberIdentifiers(rxClaims);

            var clients = loadOrderedClients(extractMemberIdentifiersFromExpected(termedMembersExpectedData), targetCommunityId);

            for (var claimClientIdentifier : clientsFromClaim) {

                clients.stream()
                        .filter(c -> c.getHealthPartnersMemberIdentifier().equals(claimClientIdentifier))
                        .findFirst()
                        //if client not found - he just wasn't present in first step termed members import
                        .ifPresent(client ->
                                assertThat(client.getActive())
                                        .describedAs("Inactive client %s should be activated when occurs in claim import",
                                                client.getHealthPartnersMemberIdentifier())
                                        .isTrue());
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Order(3)
    public class TermedMembersSecondImportToDeactivateActiveMembers extends BaseH2IT {

        Long termedFileLogIdReimport;

        @BeforeAll
        void init() throws IOException {
            var filename = HpFileType.CONSANA_TERMED_MEMBERS.name() + "_20210910_121314.txt";
            termedFileLogIdReimport = putFileToLocalCacheAndWaitUntilProcessed(
                    loadTestCsv("termed-member.txt"),
                    filename
            );
        }

        @Test
        void termedMemberReimportProcessedSuccessfully() {
            assertFileProcessedSuccess(healthPartnersFileLogDao.findById(firstTermedFileLogId));
        }

        @Test
        @Transactional
        void validateActiveClientsDeactivated() {
            var memberIdentifiers = extractMemberIdentifiersFromExpected(termedMembersExpectedData);
            var clients = loadOrderedClients(memberIdentifiers, targetCommunityId);

            for (var client : clients) {

                assertThat(client.getActive())
                        .describedAs("Client with member identifier %s should be inactive")
                        .isNotEqualTo(Boolean.TRUE);
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @Order(4)
    public class ClaimsImportWithDuplicateToActivateTermedMembers extends BaseH2IT {
        static final String CSV_FILE_NAME = "rx-claims.txt";

        List<RxClaimCSVDto> rxClaims;
        Long claimFileLogId;

        @BeforeAll
        void init() throws IOException {
            rxClaims = HealthPartnersTermedMembersTestSupport.rxClaims();

            claimFileLogId = putFileToLocalCacheAndWaitUntilProcessed(
                    loadTestCsv(CSV_FILE_NAME),
                    HpFileType.CONSANA_RX.name() + "_20210910_121516.txt"
            );
        }

        @Test
        @Transactional
        void claimsFileProcessedSuccessfully() {
            assertFileProcessedSuccess(healthPartnersFileLogDao.findById(claimFileLogId));
        }

        @Test
        @Transactional
        void validateClaimImportActivatesClients() {
            var clientsFromClaim = extractMemberIdentifiers(rxClaims);

            var clients = loadOrderedClients(extractMemberIdentifiersFromExpected(termedMembersExpectedData), targetCommunityId);

            for (var claimClientIdentifier : clientsFromClaim) {

                clients.stream()
                        .filter(c -> c.getHealthPartnersMemberIdentifier().equals(claimClientIdentifier))
                        .findFirst()
                        //if client not found - he just wasn't present in first step termed members import
                        .ifPresent(client ->
                                assertThat(client.getActive())
                                        .describedAs("Inactive client %s should be activated when occurs in claim import",
                                                client.getHealthPartnersMemberIdentifier())
                                        .isTrue());
            }
        }
    }

    private InputStream loadTestCsv(String name) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(
                "h2\\healthpartners\\termed-members\\" + name);
    }

    private Collection<String> extractMemberIdentifiersFromExpected(List<TermedMemberExpectedDataDefinition> termedMembersExpectedData) {
        return termedMembersExpectedData.stream()
                .map(TermedMemberExpectedDataDefinition::getCsvData)
                .map(HpCsvRecord::getMemberIdentifier)
                .collect(Collectors.toSet());

    }

    private Set<String> extractMemberIdentifiers(List<? extends HpCsvRecord> hpCsvRecords) {
        return hpCsvRecords.stream().map(HpCsvRecord::getMemberIdentifier).collect(Collectors.toSet());
    }
}
