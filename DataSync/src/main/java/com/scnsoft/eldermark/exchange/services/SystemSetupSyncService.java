package com.scnsoft.eldermark.exchange.services;

import com.scnsoft.eldermark.exchange.dao.source.SystemSetupSourceDao;
import com.scnsoft.eldermark.exchange.dao.target.EmployeeDao;
import com.scnsoft.eldermark.exchange.dao.target.SystemSetupDao;
import com.scnsoft.eldermark.exchange.model.source.SystemSetupData;
import com.scnsoft.eldermark.exchange.model.target.SystemSetup;
import com.scnsoft.eldermark.framework.*;
import com.scnsoft.eldermark.framework.connector4d.Sql4DOperations;
import com.scnsoft.eldermark.framework.context.DatabaseSyncContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SystemSetupSyncService implements SyncService {
    private static final Logger logger = LoggerFactory.getLogger(SystemSetupSyncService.class);

    @Autowired
    private DataSyncListenersManager listenersManager;

    @Autowired
    private SystemSetupSourceDao systemSetupSourceDao;

    @Autowired
    private SystemSetupDao systemSetupDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private TransactionOperations transactionOperations;


    @Override
    public List<Class<? extends SyncService>> dependsOn() {
        return Collections.emptyList();
    }

    @Override
    public void syncNewAndUpdated(DatabaseSyncContext syncContext,
                                  final PerformanceStatisticsHolder performanceStatisticsHolder) {
        final DatabaseInfo database = syncContext.getDatabase();
        Sql4DOperations sqlOperations = syncContext.getSql4dOperations();

        final SystemSetupData systemSetupData = loadSystemSetupData(sqlOperations, performanceStatisticsHolder);
        final boolean isNew = !systemSetupDao.exists(database.getId());
        reportErrorIfInvalid(systemSetupData, syncContext);

        if (systemSetupData == null || StringUtils.isEmpty(systemSetupData.getLoginCompanyId())) {
            return; //To be removed after all 4D source databases would update schema with SystemSetup.Login_Company_Id
        }

        transactionOperations.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                if (isNew) {
                    SystemSetup systemSetup = new SystemSetup();
                    systemSetup.setDatabaseId(database.getId());
                    systemSetup.setUpdatable(createUpdatable(systemSetupData));

                    insert(systemSetup, performanceStatisticsHolder);

                    // in case employee had been inserted before systemSetupData became valid
                    //employeeDao.updateSecureEmails(database.getId());
                } else {
                    Long pk = database.getId();
                    update(createUpdatable(systemSetupData), pk, performanceStatisticsHolder);
                }
            }
        });
    }

    @Override
    public void afterNewAndUpdatedSynced(DatabaseSyncContext context) {
        //Nothing to do here
    }

    @Override
    public DeletionRelatedOperations getDeletionRelatedOperations() {
        //Deletion isn't supported, return null
        return null;
    }

    private SystemSetupData loadSystemSetupData(Sql4DOperations sqlOperations,
                                                PerformanceStatisticsHolder performanceStatisticsHolder) {
        logger.info("{}: loading source data...", SystemSetupData.SYSTEM_SETUP_TABLE);
        long startTime = System.currentTimeMillis();
        SystemSetupData systemSetupData = systemSetupSourceDao.getSystemSetup(sqlOperations);
        long executionTime = System.currentTimeMillis() - startTime;
        performanceStatisticsHolder.registerExecutionTime(SystemSetupData.SYSTEM_SETUP_TABLE, DataSyncStep.LOAD_SOURCE_DATA, executionTime);
        logger.info("{}: loaded source data in {} ms", SystemSetupData.SYSTEM_SETUP_TABLE, executionTime);
        return systemSetupData;
    }

    private void insert(SystemSetup systemSetup, PerformanceStatisticsHolder performanceStatisticsHolder) {
        logger.info("{}: inserting data...", SystemSetupData.SYSTEM_SETUP_TABLE);
        long startTime = System.currentTimeMillis();
        systemSetupDao.insert(systemSetup);
        long executionTime = System.currentTimeMillis() - startTime;
        performanceStatisticsHolder.registerExecutionTime(SystemSetupData.SYSTEM_SETUP_TABLE, DataSyncStep.INSERT_ENTITIES, executionTime);
        logger.info("{}: inserted data in {} ms", SystemSetupData.SYSTEM_SETUP_TABLE, executionTime);
    }

    private void update(SystemSetup.Updatable updatable, long id, PerformanceStatisticsHolder performanceStatisticsHolder) {
        logger.info("{}: updating data...", SystemSetupData.SYSTEM_SETUP_TABLE);
        long updateStartTime = System.currentTimeMillis();
        systemSetupDao.update(updatable, id);
        long updateTime = System.currentTimeMillis() - updateStartTime;
        performanceStatisticsHolder.registerExecutionTime(SystemSetupData.SYSTEM_SETUP_TABLE, DataSyncStep.UPDATE_ENTITIES, updateTime);
        logger.info("{}: updated data in {} ms", SystemSetupData.SYSTEM_SETUP_TABLE, updateTime);
    }

    private SystemSetup.Updatable createUpdatable(SystemSetupData systemSetupData) {
        SystemSetup.Updatable updatable = new SystemSetup.Updatable();
        if (systemSetupData != null) {
            updatable.setLoginCompanyId(systemSetupData.getLoginCompanyId());
        }
        return updatable;
    }

    private void reportErrorIfInvalid(SystemSetupData data, DatabaseSyncContext databaseSyncContext) {
        if (data == null || StringUtils.isEmpty(data.getLoginCompanyId())) {
            ErrorMessage message = new ErrorMessage();
            message.setDate(new Date());
            message.setDatabase(databaseSyncContext.getDatabase());
            message.setTableName(SystemSetupData.SYSTEM_SETUP_TABLE);
            message.setText("Company ID is not defined.");

            List<ErrorMessage> messages = new ArrayList<ErrorMessage>();
            messages.add(message);
            listenersManager.notifyAboutErrors(messages);
        }
    }
}
