package com.scnsoft.eldermark.h2.healthPartners;

import com.scnsoft.eldermark.dao.ClientDao;
import com.scnsoft.eldermark.dao.CommunityDao;
import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersFileLogDao;
import com.scnsoft.eldermark.dto.healthpartners.HpCsvRecord;
import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Client_;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Person;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersFileLog;
import com.scnsoft.eldermark.entity.hieconsentpolicy.HieConsentPolicyType;
import com.scnsoft.eldermark.entity.inbound.ProcessingSummary;
import com.scnsoft.eldermark.h2.BaseH2IT;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersProcessingResultProvider;
import com.scnsoft.eldermark.service.healthpartners.client.HpClientFactoryFromHpClientInfo;
import org.apache.commons.io.FileUtils;
import org.assertj.core.api.SoftAssertions;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import javax.sql.DataSource;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseHealthPartnersIT extends BaseH2IT {

    @Value("${healthPartners.sftp.localStorage.base}")
    protected String healthPartnersLocalStorage;

    @Value("${healthPartners.dest.org.alternativeId}")
    protected String hpOrgAlternativeId;

    @Value("${healthPartners.dest.community.oid}")
    protected String hpCommunityOid;

    @Value("${healthPartners.test.dest.org.alternativeId}")
    protected String hpTestOrgAlternativeId;

    @Value("${healthPartners.test.dest.community.oid}")
    protected String hpTestCommunityOid;

    @Autowired
    protected ClientDao clientDao;

    @Autowired
    protected CommunityDao communityDao;

    @Autowired
    protected HealthPartnersFileLogDao healthPartnersFileLogDao;

    @Autowired
    protected DataSource dataSource;

    @Autowired
    private HealthPartnersProcessingResultProvider healthPartnersProcessingResultProvider;

    @Autowired
    protected DocumentEncryptionService documentEncryptionService;

    protected void importHpOrgsAndCommunities() throws SQLException, DatabaseUnitException {
        //dbunit can't run setup once before all tests, so running manually
        //todo investigate other approaches
        importDataset("create-hp-orgs-and-comms.xml");
    }

    protected void importDataset(String name) throws SQLException, DatabaseUnitException {
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(
                Thread.currentThread().getContextClassLoader().getResourceAsStream("h2\\datasets\\healthpartners\\" + name)
        );
        IDatabaseConnection dbConn = new DatabaseDataSourceConnection(dataSource);
        org.dbunit.operation.DatabaseOperation.REFRESH.execute(dbConn, dataSet);
    }


    protected void verifyClientData(SoftAssertions softly, HpCsvRecord csvData, Client client, boolean isActive) {
        softly.assertThat(client.getHealthPartnersMemberIdentifier())
                .describedAs("Test hp member identifier")
                .isEqualTo(csvData.getMemberIdentifier());

        softly.assertThat(client.getFirstName())
                .describedAs("Test client first name")
                .isEqualTo(csvData.getMemberFirstName());


        softly.assertThat(client.getMiddleName())
                .describedAs("Test client middle name")
                .isEqualTo(csvData.getMemberMiddleName());

        softly.assertThat(client.getLastName())
                .describedAs("Test client last name")
                .isEqualTo(csvData.getMemberLastName());

        softly.assertThat(client.getBirthDate())
                .describedAs("Test client DOB")
                .isEqualTo(csvData.getDateOfBirth());

        if (isActive) {
            softly.assertThat(client.getActive())
                    .describedAs("Test client created from claim is active")
                    .isTrue();
        } else {
            softly.assertThat(client.getActive())
                    .describedAs("Test termed client is inactive")
                    .isIn(null, Boolean.FALSE);
        }

        softly.assertThat(client.getHieConsentPolicyType()).isEqualTo(HieConsentPolicyType.OPT_IN);
        softly.assertThat(client.getHieConsentPolicyObtainedFrom()).isEqualTo(HpClientFactoryFromHpClientInfo.POLICY_OBTAINED_FROM_CONTRACT_VALUE);

        var person = client.getPerson();

        verifyPerson(softly, person, "client",
                client.getOrganization(),
                csvData.getMemberFirstName(),
                csvData.getMemberMiddleName(),
                csvData.getMemberLastName()
        );
    }

    protected void verifyPerson(SoftAssertions softly, Person person, String context, Organization organization,
                                String firstName, String middleName, String lastName) {
        softly.assertThat(person.getOrganization())
                .describedAs("Test %s person organization", context)
                .isEqualTo(organization);

        softly.assertThat(person.getNames())
                .describedAs("Test %s person has exactly one Name", context)
                .hasSize(1);

        var name = person.getNames().get(0);

        softly.assertThat(name.getGiven())
                .describedAs("Test %s person first name", context)
                .isEqualTo(firstName);

        softly.assertThat(name.getMiddle())
                .describedAs("Test %s person middle name", context)
                .isEqualTo(middleName);

        softly.assertThat(name.getFamily())
                .describedAs("Test %s person last name", context)
                .isEqualTo(lastName);
    }

    protected List<Client> loadOrderedClients(Collection<String> memberIdentifiers, Long targetCommunityId) {
        return clientDao.findAll(
                healthPartnersClient(memberIdentifiers, targetCommunityId),
                Sort.by(Client_.ID));
    }

    protected Specification<Client> healthPartnersClient(Collection<String> memberIdentifiers, Long targetCommunityId) {
        return (root, cq, cb) -> cb.and(
                root.get(Client_.healthPartnersMemberIdentifier).in(memberIdentifiers),
                cb.equal(root.get(Client_.communityId), targetCommunityId)
        );
    }

    protected Long putFileToLocalCacheAndWaitUntilProcessed(InputStream source, String targetFileName) throws
            IOException {
        var fileFuture = healthPartnersProcessingResultProvider.getFileLogFuture(targetFileName);
        var encrypted = documentEncryptionService.encrypt(source.readAllBytes());
        FileUtils.copyInputStreamToFile(
                new ByteArrayInputStream(encrypted),
                new File(healthPartnersLocalStorage, targetFileName)
        );

        return fileFuture.join();
    }


    protected void assertFileProcessedSuccess(Optional<HealthPartnersFileLog> fileLog) {
        assertFileProcessedStatus(fileLog, ProcessingSummary.ProcessingStatus.OK);
    }

    protected void assertFileProcessedStatus(Optional<HealthPartnersFileLog> fileLog, ProcessingSummary.ProcessingStatus expectedStatus) {
        switch (expectedStatus) {
            case OK:
                assertThat(fileLog).map(HealthPartnersFileLog::isSuccess)
                        .withFailMessage(
                                "Valid file should have been processed successfully, however it failed with error %s",
                                fileLog.map(HealthPartnersFileLog::getErrorMessage).orElse(null))
                        .hasValue(Boolean.TRUE);
                break;
            case WARN:
                assertThat(fileLog).map(HealthPartnersFileLog::isSuccess)
                        .withFailMessage(
                                "Valid file should have been processed with fail, however it was processed successfully",
                                fileLog.map(HealthPartnersFileLog::getErrorMessage).orElse(null))
                        .hasValue(Boolean.FALSE);
                assertThat(fileLog).map(HealthPartnersFileLog::getErrorMessage)
                        .matches(s -> s.isPresent() && s.get().startsWith("WARN STATUS:"));
                break;
            case ERROR:
                assertThat(fileLog).map(HealthPartnersFileLog::isSuccess)
                        .withFailMessage(
                                "Valid file should have been processed with fail, however it was processed successfully",
                                fileLog.map(HealthPartnersFileLog::getErrorMessage).orElse(null))
                        .hasValue(Boolean.FALSE);
                assertThat(fileLog).map(HealthPartnersFileLog::getErrorMessage)
                        .matches(s -> s.isPresent() && !s.get().startsWith("WARN STATUS:"));
                break;
        }

    }

}
