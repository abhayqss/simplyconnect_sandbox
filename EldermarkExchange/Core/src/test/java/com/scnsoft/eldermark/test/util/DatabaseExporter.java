package com.scnsoft.eldermark.test.util;

import com.scnsoft.eldermark.entity.CcdCode;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.Organization;
import com.scnsoft.eldermark.entity.Resident;
import com.scnsoft.eldermark.services.DatabasesService;
import com.scnsoft.eldermark.services.ResidentService;
import com.scnsoft.eldermark.shared.ResidentFilterUiDto;
import com.scnsoft.eldermark.shared.SearchScope;
import com.scnsoft.eldermark.shared.administration.MatchStatus;
import com.scnsoft.eldermark.shared.administration.MergeStatus;
import com.scnsoft.eldermark.shared.administration.SearchMode;
import com.scnsoft.eldermark.test.TestApplicationMsSqlConfig;
import org.apache.commons.beanutils.BeanToPropertyValueTransformer;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.*;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Export one or many tables from a database to a flat XML dataset file (used by DbUnit).
 * Note that if you want to specify a schema you can do this in the constructor of {@code DatabaseConnection}.
 *
 * @author phomal
 * Created on 4/27/2017.
 */
public class DatabaseExporter {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(TestApplicationMsSqlConfig.class);

        // database connection
        DataSource dataSource = (DataSource)context.getBean("dataSource");
        IDatabaseConnection connection = new DatabaseConnection(dataSource.getConnection());

        // dependencies lookup
        ResidentService residentService = context.getBean(ResidentService.class);
        DatabasesService databasesService = context.getBean(DatabasesService.class);

        // datasets creation
        //exportSingleResident(connection, residentService, databasesService);
        exportManyResidents(connection, residentService, databasesService);
    }

    private static void exportSingleResident(IDatabaseConnection connection, ResidentService residentService, DatabasesService databasesService) throws DataSetException, IOException {
        // select resident
        Resident resident = residentService.getResident(47);    // Charles
        if (!isAllowed(databasesService.getDatabaseById(resident.getDatabaseId()))) {
            // residents from 4D may contain real data so they are not allowed to appear in test datasets
            return;
        }

        // partial database export
        IDataSet residentDataSet = exportResidentToDataSet(connection, resident, true);
        IDataSet nullMatchesDataSet = exportResidentsMatchingDataToDataSet(connection, Collections.<Resident>emptySet(), null);
        IDataSet[] dataSets = new IDataSet[] {residentDataSet, nullMatchesDataSet};
        residentDataSet = new CompositeDataSet(dataSets, true);

        FlatXmlDataSet.write(residentDataSet, new FileOutputStream(getFileFromClassPath("single.xml")));
    }

    private static void exportManyResidents(IDatabaseConnection connection, ResidentService residentService, DatabasesService databasesService) throws DataSetException, IOException, URISyntaxException {
        // select residents
        //ResidentFilterUiDto filter = buildFilterForAdministrationSuggestedMatches(null);
        ResidentFilterUiDto filter = buildFilterForAdministrationManualMatching();
        List<Resident> residents = residentService.getResidents(filter);
        // residents from 4D may contain real data so they are not allowed to appear in test datasets
        residents = excludeResidentsFrom4D(databasesService, residents);
        // manually pick some test residents from 4D that contain fake data
        residents.addAll(residentService.getResidents(Arrays.asList(
                113L, 271L,         // Samuel
                297L,                   // Mary
                231L, 244L, 248L, 346L    // Test
        )));

        IDataSet importDataSet = exportResidentsCommonDataToDataSet(connection, residents);
        FlatXmlDataSet.write(importDataSet, new FileOutputStream(getFileFromClassPath("import-new.xml")));

        IDataSet residentsDataSet = exportResidentsToDataSet(connection, residents);
        IDataSet matchesDataSet = exportResidentsMatchingDataToDataSet(connection, residents, null);
        IDataSet[] dataSets = new IDataSet[] {residentsDataSet, matchesDataSet};
        residentsDataSet = new CompositeDataSet(dataSets, true);

        FlatXmlDataSet.write(residentsDataSet, new FileOutputStream(getFileFromClassPath("residents-with-matches-new.xml")));
    }

    private static List<Resident> excludeResidentsFrom4D(DatabasesService databasesService, List<Resident> residents) {
        Iterator<Resident> iterator = residents.iterator();
        while (iterator.hasNext()) {
            Resident resident = iterator.next();
            if (!isAllowed(databasesService.getDatabaseById(resident.getDatabaseId()))) {
                iterator.remove();
            }
        }
        return residents;
    }

    private static boolean isAllowed(Database database) {
        return StringUtils.isBlank(database.getRemoteHost());
    }



    private static File getFileFromClassPath(String path) throws IOException {
        // NOTE: Working directory should be Core project dir (EldermarkExchange/Core)
        return new File("src/test/resources/datasets/", path);
    }

    // TODO extract to search filter builder?
    private static ResidentFilterUiDto buildFilterForAdministrationManualMatching() {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();

        filter.setSearchScopes(Collections.singleton(SearchScope.ELDERMARK));
        filter.setMode(SearchMode.MATCH_ALL);
        filter.setSsnRequired(false);
        filter.setDateOfBirthRequired(false);

        return filter;
    }

    // TODO extract to search filter builder?
    private static ResidentFilterUiDto buildFilterForAdministrationSuggestedMatches(String query) {
        ResidentFilterUiDto filter = new ResidentFilterUiDto();

        filter.setSearchScopes(Collections.singleton(SearchScope.ELDERMARK));
        filter.setMode(SearchMode.MATCH_ANY_LIKE);
        filter.setMatchStatus(MatchStatus.MAYBE_MATCHED);
        filter.setMergeStatus(MergeStatus.NOT_MERGED);
        filter.setSsnRequired(false);
        filter.setDateOfBirthRequired(false);

        filter.setFirstName(query);
        filter.setLastName(query);
        filter.setSsn(query);
        filter.setCommunity(query);
        filter.setProviderOrganization(query);

        return filter;
    }

    private static IDataSet exportResidentsMatchingDataToDataSet(IDatabaseConnection connection, Collection<Resident> residents, MatchStatus matchStatus)
            throws DataSetException {
        QueryDataSet queryDataSet = new QueryDataSet(connection);

        String residentIds = convertAnyCollectionToCsv(residents, "id");
        String query = "SELECT * FROM MPI_merged_residents WHERE surviving_resident_id IN " + residentIds + " AND merged_resident_id IN " + residentIds;
        if (MatchStatus.MAYBE_MATCHED.equals(matchStatus)) {
            query += " AND probably_matched = 1";
        } else if (MatchStatus.SURELY_MATCHED.equals(matchStatus)) {
            query += " AND merged = 1";
        }
        queryDataSet.addTable("MPI_merged_residents", query);

        return queryDataSet;
    }

    private static IDataSet exportResidentsCommonDataToDataSet(IDatabaseConnection connection, Collection<Resident> residents) throws DataSetException {
        QueryDataSet partialDataSet = new QueryDataSet(connection);

        Set<CcdCode> codes = new HashSet<CcdCode>();
        Set<Organization> organizations = new HashSet<Organization>();
        Set<Long> databases = new HashSet<Long>();

        for (Resident resident : residents) {
            List<CcdCode> residentCodes = Arrays.asList(
                    resident.getMaritalStatus(),
                    resident.getGender(),
                    resident.getRace(),
                    resident.getEthnicGroup(),
                    resident.getReligion()
            );
            codes.addAll(residentCodes);
            List<Organization> residentOrganizations = Arrays.asList(
                    resident.getFacility(),
                    resident.getProviderOrganization()
            );
            organizations.addAll(residentOrganizations);
            databases.add(resident.getDatabaseId());
        }
        databases.removeAll(Collections.singleton(null));

        partialDataSet.addTable("CcdCode", "SELECT * FROM CcdCode WHERE id IN " + convertAnyCollectionToCsv(codes, "id"));
        partialDataSet.addTable("SourceDatabase", "SELECT * FROM SourceDatabase WHERE id IN (" +
                StringUtils.join(databases, ',') + ")");
        partialDataSet.addTable("Organization", "SELECT * FROM Organization WHERE id IN " +
                convertAnyCollectionToCsv(organizations, "id"));

        // exclude sensitive data
        HashMap<String, Set<String>> excludedColumns = new HashMap<String, Set<String>>();
        excludedColumns.put("SourceDatabase", new HashSet<String>(
                Arrays.asList("last_success_sync_date", "last_synced_epoch", "is_xml_sync",
                        "name_and_port", "remote_username", "remote_password", "remote_host", "remote_port", "remote_use_ssl")));
        excludedColumns.put("Organization", new HashSet<String>(Arrays.asList("provider_npi")));
        IDataSet filteredDataSet = filterDataSet(partialDataSet, excludedColumns);

        return filteredDataSet;
    }

    /**
     * Generates a combined data set with the personal data of the specified residents and all the related data.
     *
     * @throws DataSetException
     */
    private static IDataSet exportResidentsToDataSet(IDatabaseConnection connection, Collection<Resident> residents) throws DataSetException {
        List<IDataSet> dataSets = new ArrayList<IDataSet>(residents.size());
        for (Resident resident : residents) {
            IDataSet dataSet = exportResidentToDataSet(connection, resident, false);
            dataSets.add(dataSet);
        }

        return new CompositeDataSet(dataSets.toArray(new IDataSet[dataSets.size()]), true);
    }

    /**
     * Generates a data set with the personal data of the specified resident and all the related data.
     *
     * @throws DataSetException
     */
    private static IDataSet exportResidentToDataSet(IDatabaseConnection connection, Resident resident, boolean includeCommonData) throws DataSetException {
        QueryDataSet partialDataSet = new QueryDataSet(connection);
        IDataSet commonInfoDataSet = null;

        if (includeCommonData) {
            commonInfoDataSet = exportResidentsCommonDataToDataSet(connection, Collections.singletonList(resident));
        }

        // include personal data
        Long personId = resident.getPerson().getId();
        partialDataSet.addTable("Person", "SELECT * FROM Person WHERE id = " + personId);
        partialDataSet.addTable("PersonAddress", "SELECT * FROM PersonAddress WHERE person_id = " + personId);
        partialDataSet.addTable("PersonTelecom", "SELECT * FROM PersonTelecom WHERE person_id = " + personId);
        partialDataSet.addTable("name", "SELECT * FROM name WHERE person_id = " + personId);
        //List<Language> languages = resident.getLanguages();
        //partialDataSet.addTable("Language", "SELECT * FROM Language WHERE id IN (" + convertLanguagesToCsv(languages, "id") + ")");
        if (resident.getCustodian() != null) {
            partialDataSet.addTable("Custodian", "SELECT * FROM Custodian WHERE id = " + resident.getCustodian().getId());
        }
        partialDataSet.addTable("resident", "SELECT * FROM resident WHERE id = " + resident.getId());

        // exclude sensitive data or data that is not required for testing
        HashMap<String, Set<String>> excludedColumns = new HashMap<String, Set<String>>();
        excludedColumns.put("resident", new HashSet<String>(Arrays.asList("hash_key", "birth_order", "birth_place", "death_indicator",
                "citizenship", "mother_person_id", "mother_account_number", "patient_account_number", "last_updated", "created_by_id")));
        IDataSet filteredDataSet = filterDataSet(partialDataSet, excludedColumns);

        if (includeCommonData) {
            IDataSet[] dataSets = {commonInfoDataSet, filteredDataSet};
            return new CompositeDataSet(dataSets, true);
        } else {
            return filteredDataSet;
        }
    }

    private static String convertAnyCollectionToCsv(Collection objects, String property) {
        List<Long> ids = new ArrayList<Long>();
        CollectionUtils.collect(objects, new BeanToPropertyValueTransformer(property, true), ids);
        ids.removeAll(Collections.singleton(null));
        return String.format(" (%s)", StringUtils.defaultIfBlank(StringUtils.join( ids, ','), "-1"));
    }

    /**
     * Generates a new data set with the columns declared in the "excludedColumns" map removed.
     *
     * @param src Source data set.
     * @param excludedColumns Map of table names and column names. Columns in this map are removed in the resulting data set.
     * @return Data set with the columns declared in the "excludedColumns" map removed.
     *         Tables that are not specified in the "excludedColumns" map are left untouched.
     * @throws DataSetException
     */
    private static IDataSet filterDataSet(IDataSet src, Map<String, Set<String>> excludedColumns) throws DataSetException {
        if (excludedColumns == null) {
            return src;
        }

        ArrayList<ITable> tables = new ArrayList<ITable>(src.getTableNames().length);

        for (String tableName : src.getTableNames()) {
            if (excludedColumns.containsKey(tableName)) {
                ITable filteredTable = DefaultColumnFilter.excludedColumnsTable(
                        src.getTable(tableName),
                        excludedColumns.get(tableName).toArray(new String[0]));

                tables.add(filteredTable);
            } else {
                tables.add(src.getTable(tableName));
            }
        }

        return new DefaultDataSet(tables.toArray(new ITable[0]), src.isCaseSensitiveTableNames());
    }

}
