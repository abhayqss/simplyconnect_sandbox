package com.scnsoft.eldermark.exchange;

import com.scnsoft.eldermark.exchange.dao.target.DatabaseDao;
import com.scnsoft.eldermark.exchange.dao.target.OrganizationDao;
import com.scnsoft.eldermark.exchange.model.target.Database;
import com.scnsoft.eldermark.exchange.services.*;
import com.scnsoft.eldermark.exchange.services.employees.EmployeePasswordSyncService;
import com.scnsoft.eldermark.exchange.services.employees.EmployeeSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentDocumentsUpdateSyncService;
import com.scnsoft.eldermark.exchange.services.residents.ResidentSyncService;
import com.scnsoft.eldermark.framework.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class DataSyncMain {
    private static Logger logger = LoggerFactory.getLogger(DataSyncMain.class);

    private static final String IGNORE_SYNC_FLAG_PROPERTY = "ignore.sync.flag";
    private static final String DATABASE_URLS_PROPERTY = "database.urls";
    private static final String CLOUD_DATABASE_URLS_PROPERTY = "cloud.database.urls";
    private static final String RUN_IN_BACKGROUND_PROPERTY = "run.in.background";

    private static final String CONFIG_FILE_PATH_ARG = "--config-file-path";

    public static void main(String[] args) throws Exception {
        try {
            String configFilePath = null;
            TimeZone.setDefault(TimeZone.getTimeZone("Etc/UTC"));
            int i = 0;
            while (i < args.length) {
                String arg = args[i];
                if (CONFIG_FILE_PATH_ARG.equals(arg)) {
                    if (i + 1 >= args.length) {
                        System.err.println("Missing value for argument " + CONFIG_FILE_PATH_ARG);
                        System.exit(1);
                    }
                    configFilePath = args[i + 1];
                    i++;
                }

                i++;
            }
            if (configFilePath == null) {
                System.err.println("Required argument '" + CONFIG_FILE_PATH_ARG + "' is missing");
                System.exit(1);
            }

            Configuration c = loadConfiguration(configFilePath);
            logger.info("Loaded configuration: " + c.toString());

            ApplicationContext ctx;
            if (c.isRunInBackground()) {
                ctx = new ClassPathXmlApplicationContext("/spring/exchangeDataSyncContext.xml", "/spring/quartzContext.xml");
            } else {
                ctx = new ClassPathXmlApplicationContext("/spring/exchangeDataSyncContext.xml");
            }

            Set<SyncService> syncServices = new HashSet<SyncService>();

            syncServices.add(ctx.getBean(DiagnosisSetupSyncService.class));
            syncServices.add(ctx.getBean(AllergySyncService.class));
            syncServices.add(ctx.getBean(CompanySyncService.class));
            syncServices.add(ctx.getBean(ContactSyncService.class));
            syncServices.add(ctx.getBean(EmployeeCompanySyncService.class));
            syncServices.add(ctx.getBean(EmployeeSyncService.class));   // Deletion isn't supported
            syncServices.add(ctx.getBean(MedicalProfessionalSyncService.class));
            syncServices.add(ctx.getBean(MedicationSyncService.class));
            syncServices.add(ctx.getBean(PharmacySyncService.class));
            syncServices.add(ctx.getBean(ProblemSyncService.class));
            syncServices.add(ctx.getBean(ResAdmittanceHistorySyncService.class));
            syncServices.add(ctx.getBean(LivingStatusSyncService.class));
            syncServices.add(ctx.getBean(ResidentSyncService.class));   // Deletion isn't supported
            syncServices.add(ctx.getBean(VitalSignSyncService.class));
            syncServices.add(ctx.getBean(ResPharmacySyncService.class));
            syncServices.add(ctx.getBean(MedicalProfessionalRoleSyncService.class));
            syncServices.add(ctx.getBean(ResMedProfessionalSyncService.class));
            syncServices.add(ctx.getBean(SystemSetupSyncService.class));
            syncServices.add(ctx.getBean(GroupSyncService.class));
            syncServices.add(ctx.getBean(RoleSyncService.class));
            syncServices.add(ctx.getBean(ResPaySourceHistorySyncService.class));
            syncServices.add(ctx.getBean(CareHistorySyncService.class));
            syncServices.add(ctx.getBean(ImmunizationSyncService.class));

            Set<SyncService> cloudSyncServices = new HashSet<SyncService>();
            cloudSyncServices.add(ctx.getBean(CompanySyncService.class));
            cloudSyncServices.add(ctx.getBean(EmployeeCompanySyncService.class));
            cloudSyncServices.add(ctx.getBean(EmployeeSyncService.class));
            cloudSyncServices.add(ctx.getBean(ResidentSyncService.class));
            cloudSyncServices.add(ctx.getBean(SystemSetupSyncService.class));
            cloudSyncServices.add(ctx.getBean(GroupSyncService.class));
            cloudSyncServices.add(ctx.getBean(RoleSyncService.class));

            /**
             * This the service which is used to update the tossed residents documents to keeper residents.
             * Used to update the historical data.
             */
//            syncServices.add(ctx.getBean(ResidentDocumentsUpdateSyncService.class));

            /**
             * This service is used only to update the password of employees separately.
             */
//            syncServices.add(ctx.getBean(EmployeePasswordSyncService.class));
//            cloudSyncServices.add(ctx.getBean(EmployeePasswordSyncService.class));

            /**
             * Below are syncServices which are specific to dropped JReports
             */
            /*
            syncServices.add(ctx.getBean(CommunicationSyncService.class));
            syncServices.add(ctx.getBean(CommunicationTypeSyncService.class));
            syncServices.add(ctx.getBean(InquirySyncService.class));
            syncServices.add(ctx.getBean(OccupancyGoalSyncService.class));
            syncServices.add(ctx.getBean(ProfessionalContactSyncService.class));
            syncServices.add(ctx.getBean(ProspectSyncService.class));
            syncServices.add(ctx.getBean(ResUnitHistorySyncService.class));
            syncServices.add(ctx.getBean(UnitHistorySyncService.class));
            syncServices.add(ctx.getBean(UnitSyncService.class));
            syncServices.add(ctx.getBean(UnitTypeSyncService.class));
            syncServices.add(ctx.getBean(FuneralHomeSyncService.class));
            syncServices.add(ctx.getBean(LoaReasonSyncService.class));
            syncServices.add(ctx.getBean(MedProviderSyncService.class));
            syncServices.add(ctx.getBean(MedScheduleCodeSyncService.class));
            syncServices.add(ctx.getBean(MedProviderScheduleSyncService.class));
            syncServices.add(ctx.getBean(MedTimeCodeSyncService.class));
            syncServices.add(ctx.getBean(MedicationTreatmentSetupSyncService.class));
            syncServices.add(ctx.getBean(ResLeaveOfAbsenceSyncService.class));
            syncServices.add(ctx.getBean(ResMedDupSyncService.class));
            syncServices.add(ctx.getBean(MedDeliverySyncService.class));
            syncServices.add(ctx.getBean(MedIncidentSyncService.class));
            syncServices.add(ctx.getBean(ResIncidentSyncService.class));
            syncServices.add(ctx.getBean(MedProviderScheduleLogSyncService.class));
            syncServices.add(ctx.getBean(UnitStationSyncService.class));
            syncServices.add(ctx.getBean(ResMedProviderSyncService.class));
            syncServices.add(ctx.getBean(UnitTypeRateHistorySyncService.class));
            syncServices.add(ctx.getBean(ReferralSourceSyncService.class));
            syncServices.add(ctx.getBean(ReferralSourceOrganizationSyncService.class));
            */

            DatabaseDao databaseDao = ctx.getBean(DatabaseDao.class);
            OrganizationDao organizationDao = ctx.getBean(OrganizationDao.class);

            List<Pair<List<DatabaseInfo>, Set<SyncService>>> databasesWithSyncServicesList = new ArrayList<Pair<List<DatabaseInfo>, Set<SyncService>>>();

            List<DatabaseInfo> databases = new ArrayList<DatabaseInfo>();
            if (!StringUtils.isEmpty(c.getDatabaseUrls())) {
                for (String databaseUrl : c.getDatabaseUrls()) {
                    Database database = databaseDao.getDatabase(databaseUrl);
                    DatabaseInfo databaseInfo = createDatabaseInfo(database);
                    databases.add(databaseInfo);
                }
                databasesWithSyncServicesList.add(new Pair<List<DatabaseInfo>, Set<SyncService>>(databases, syncServices));
            }

            List<DatabaseInfo> cloudDatabases = new ArrayList<DatabaseInfo>();
            if (!StringUtils.isEmpty(c.getCloudDatabaseUrls())) {
                for (String databaseUrl : c.getCloudDatabaseUrls()) {
                    Database database = databaseDao.getDatabase(databaseUrl);
                    DatabaseInfo databaseInfo = createDatabaseInfo(database);
                    cloudDatabases.add(databaseInfo);
                }
                databasesWithSyncServicesList.add(new Pair<List<DatabaseInfo>, Set<SyncService>>(cloudDatabases, cloudSyncServices));
            }


            DataSync dataSync = ctx.getBean(DataSync.class);
            dataSync.setDatabasesWithSyncServices(databasesWithSyncServicesList);
            if(isAddDataSyncListener(syncServices, databases, ctx) || isAddDataSyncListener(cloudSyncServices, cloudDatabases, ctx)) {
            	dataSync.addListener(ctx.getBean("syncRevisionListener", DataSyncListener.class));
            	dataSync.addListener(ctx.getBean("syncLogListener", DataSyncListener.class));
            }
			dataSync.setStatsListener(ctx.getBean(DataSyncStatsListener.class)); 
			dataSync.setIsSyncDeleteRecord(isSyncDeleteRecord(syncServices, ctx));
            dataSync.setTargetOrganizationsIdMapping(organizationDao.getTargetOrganizationsIdMapping());
            if (c.isRunInBackground()) {
                SchedulerFactoryBean schedulerFactoryBean = ctx.getBean(SchedulerFactoryBean.class);
                schedulerFactoryBean.start();
            } else {
                Date date1 = new Date();
                dataSync.run();
                Date date2 = new Date();
                System.out.println("DATASYNC took "+(date2.getTime()-date1.getTime())/1000 +" seconds");
            }
        } catch (Exception e) {
            logger.error("Data sync error: " + e.getMessage(), e);
        }
    }

    private static DatabaseInfo createDatabaseInfo(Database database) {
        DatabaseInfo databaseInfo = new DatabaseInfo();
        databaseInfo.setId(database.getId());
        databaseInfo.setUrl(database.getUrl());
        databaseInfo.setLastSyncDate(database.getLastSyncDate());
        databaseInfo.setRemoteHost(database.getRemoteHost());
        databaseInfo.setRemotePort(database.getRemotePort());
        databaseInfo.setRemoteUsername(database.getRemoteUsername());
        databaseInfo.setRemotePassword(database.getRemotePassword());
        databaseInfo.setRemoteUseSsl(database.getRemoteUseSsl());
        databaseInfo.setIsXmlSync(database.getIsXmlSync());
        databaseInfo.setLastSyncedTime(database.getLastSyncedEpoch());
        databaseInfo.setIsInitialSync(database.getIsInitialSync());
        databaseInfo.setConsanaXOwningId(database.getConsanaXOwningId());
        return databaseInfo;
    }

    private static boolean isAddDataSyncListener(Set<SyncService> syncServices, List<DatabaseInfo> databases,
            ApplicationContext ctx) {
        if (syncServices.contains(ctx.getBean(EmployeePasswordSyncService.class))
                || syncServices.contains(ctx.getBean(ResidentDocumentsUpdateSyncService.class))) {
            if (syncServices.size() > 1) {
                throw new RuntimeException("EmployeePasswordSyncService & ResidentDocumentsUpdateSyncService can't be run be with other sync services");
            }
            for (DatabaseInfo databaseInfo : databases) {
                if (databaseInfo.getIsInitialSync()) {
                    throw new RuntimeException(
                            "EmployeePasswordSyncService & ResidentDocumentsUpdateSyncService could be run only after completion of initial sync service. "
                                    + "Initial sync not completed for Organisation with URL: " + databaseInfo.getUrl());
                }
            }
            return false;
        }
        return true;
    }
    
    private static boolean isSyncDeleteRecord(Set<SyncService> syncServices, ApplicationContext ctx) {
    	if(syncServices.contains(ctx.getBean(EmployeePasswordSyncService.class))) {
    		return false;
    	}
    	return true;
    }

    private static Configuration loadConfiguration(String configFilePath) throws IOException {
        InputStream is = null;
        try {
            is = new BufferedInputStream(new FileInputStream(configFilePath));

            Properties p = new Properties();
            p.load(is);

            String databaseUrlsProperty = p.getProperty(DATABASE_URLS_PROPERTY);
            String cloudDatabaseUrlsProperty = p.getProperty(CLOUD_DATABASE_URLS_PROPERTY);
            if (databaseUrlsProperty == null && cloudDatabaseUrlsProperty == null) {
                throw new RuntimeException("Missing both required properties'" + DATABASE_URLS_PROPERTY + ", " + CLOUD_DATABASE_URLS_PROPERTY + "'");
            }
            List<String> databaseUrls = null;
            if (!StringUtils.isEmpty(databaseUrlsProperty)) {
                databaseUrls = Arrays.asList(databaseUrlsProperty.split(","));
            }
            List<String> cloudDatabaseUrls = null;
            if (!StringUtils.isEmpty(cloudDatabaseUrlsProperty)) {
                cloudDatabaseUrls = Arrays.asList(cloudDatabaseUrlsProperty.split(","));
            }

            Boolean runInBackground = readBooleanProperty(p, RUN_IN_BACKGROUND_PROPERTY);
            if (runInBackground == null) {
                runInBackground = false;
            }

            return new Configuration(databaseUrls, cloudDatabaseUrls, runInBackground);
        } catch (IOException e) {
            logger.error("I/O error", e);
            throw e;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    logger.error("Failed to close input stream", e);
                }
            }
        }
    }

    private static Boolean readBooleanProperty(Properties properties, String propertyName) {
        String propertyValue = properties.getProperty(propertyName);
        Boolean propertyBooleanValue = null;
        if (propertyValue != null) {
            if ("true".equalsIgnoreCase(propertyValue)) {
                propertyBooleanValue = true;
            } else if ("false".equalsIgnoreCase(propertyValue)) {
                propertyBooleanValue = false;
            } else {
                throw new RuntimeException("Invalid property '" + propertyName +
                        "': either 'true' or 'false' value is expected");
            }
        }
        return propertyBooleanValue;
    }
}
