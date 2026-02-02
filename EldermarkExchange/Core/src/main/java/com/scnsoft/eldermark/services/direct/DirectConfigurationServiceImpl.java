package com.scnsoft.eldermark.services.direct;


import com.scnsoft.eldermark.dao.DatabasesDao;
import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.entity.DirectConfiguration;
import com.scnsoft.eldermark.services.SaveDocumentCallback;
import com.scnsoft.eldermark.shared.exceptions.FileIOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Component
public class DirectConfigurationServiceImpl implements DirectConfigurationService {
    private static final Logger logger = LoggerFactory.getLogger(WebServiceClientFactoryImpl.class);

    @Autowired
    private DatabasesDao databasesDao;

    @Value("${keystore.upload.basedir}")
    private String keystoresUploadDirName;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void setConfigured(String companyCode, boolean isConfigured) {
        Database database = databasesDao.getDatabaseByCompanyId(companyCode);

        DirectConfiguration config = database.getDirectConfig();
        if (config == null) {
            config = new DirectConfiguration();
            database.setDirectConfig(config);
        }

        config.setConfigured(isConfigured);

        databasesDao.update(database);
    }

    @Transactional
    @Override
    public boolean isConfigured(String companyCode) {
        Database database = databasesDao.getDatabaseByCompanyId(companyCode);
        DirectConfiguration config = database.getDirectConfig();

        return (config == null || config.getConfigured() == null) ? false : config.getConfigured();
    }

    @Override
    public void setPin(String companyCode, String pin) {
        Database database = databasesDao.getDatabaseByCompanyId(companyCode);

        DirectConfiguration config = database.getDirectConfig();
        if (config == null) {
            config = new DirectConfiguration();
            database.setDirectConfig(config);
        }

        config.setPin(pin);

        databasesDao.update(database);
    }

    @Override
    public void uploadKeystore(SaveDocumentCallback callback, String companyCode) {
        String oldLocation = getKeystoreLocation(companyCode);
        if(oldLocation != null) {
            File oldKeystore = new File(oldLocation);
            oldKeystore.delete();
        }

        File uploadDir = new File(keystoresUploadDirName);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String uuid = UUID.randomUUID().toString();
        String fileName = buildFileName(uuid);
        File uploadedFile = new File(uploadDir, fileName);

        boolean fileCreated;
        try {
            fileCreated = uploadedFile.createNewFile();
        } catch (IOException e) {
            logger.error("I/O error occured during file upload", e);
            throw new FileIOException("I/O error occured during file upload");
        }

        if (!fileCreated) {
            throw new FileIOException("File" + fileName + " already exists");
        }
        callback.saveToFile(uploadedFile);

        setKeystoreFile(companyCode, fileName);
    }

    @Override
    public String getKeystoreLocation(String companyCode) {
        final String location = getKeystoreRelativeLocation(companyCode);
        return (location == null) ? null : buildFileLocation(location);
    }

    @Override
    public String getKeystoreRelativeLocation(String companyCode) {
        Database database = databasesDao.getDatabaseByCompanyId(companyCode);

        DirectConfiguration config = database.getDirectConfig();

        return (config == null) ? null : config.getKeystoreFile();
    }

    @Override
    public String getKeystoresBaseLocation() {
        return keystoresUploadDirName;
    }

    @Override
    public DirectConfiguration find(String companyCode) {
        Database database = databasesDao.getDatabaseByCompanyId(companyCode);

        return database.getDirectConfig();
    }

    private void setKeystoreFile(String companyCode, String fileName) {
        Database database = databasesDao.getDatabaseByCompanyId(companyCode);

        DirectConfiguration config = database.getDirectConfig();
        if (config == null) {
            config = new DirectConfiguration();
            database.setDirectConfig(config);
        }

        config.setKeystoreFile(fileName);

        databasesDao.update(database);
    }

    private String buildFileLocation(String fileName) {
        return getKeystoresBaseLocation() + File.separator + fileName;
    }

    private String buildFileName(String documentUuid) {
        return "keystore_" + documentUuid;
    }

    public void setDatabasesDao(DatabasesDao databasesDao) {
        this.databasesDao = databasesDao;
    }

    public void setKeystoresUploadDirName(String keystoresUploadDirName) {
        this.keystoresUploadDirName = keystoresUploadDirName;
    }
}