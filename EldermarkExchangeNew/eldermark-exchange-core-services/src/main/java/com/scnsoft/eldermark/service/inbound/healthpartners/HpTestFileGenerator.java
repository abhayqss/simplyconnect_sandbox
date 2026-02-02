package com.scnsoft.eldermark.service.inbound.healthpartners;

import com.scnsoft.eldermark.dao.healthpartners.HealthPartnersFileLogDao;
import com.scnsoft.eldermark.entity.healthpartner.HealthPartnersFileLog;
import com.scnsoft.eldermark.entity.inbound.healthpartners.HpFileType;
import com.scnsoft.eldermark.exception.BusinessException;
import com.scnsoft.eldermark.service.DocumentEncryptionService;
import com.scnsoft.eldermark.service.healthpartners.HealthPartnersProcessingResultProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Component
@Transactional(readOnly = true)
public class HpTestFileGenerator {

    private static final Logger logger = LoggerFactory.getLogger(HpTestFileGenerator.class);

    @Value("${healthPartners.sftp.localStorage.base}")
    private String localStorageBaseDirPath;

    @Autowired
    private HpTestFileNameGenerator hpTestFileNameGenerator;

    @Autowired
    private HealthPartnersFileLogDao healthPartnersFileLogDao;

    @Autowired
    private HealthPartnersProcessingResultProvider healthPartnersProcessingResultProvider;

    @Autowired
    private DocumentEncryptionService documentEncryptionService;

    public HealthPartnersFileLog writeTestFile(String csv, HpFileType fileType) {
        return writeCsv(csv, hpTestFileNameGenerator.generate(fileType));
    }

    private HealthPartnersFileLog writeCsv(String csv, String filename) {
        try {
            var filePath = Paths.get(localStorageBaseDirPath + "/" + filename);
            var fileLogFuture = healthPartnersProcessingResultProvider.getFileLogFuture(filename);
            Files.copy(new ByteArrayInputStream(documentEncryptionService.encrypt(csv.getBytes(StandardCharsets.UTF_8))), filePath, StandardCopyOption.REPLACE_EXISTING);
            var fileLogId = fileLogFuture.join();
            return healthPartnersFileLogDao.findById(fileLogId).orElseThrow(
                    () -> new BusinessException("Failed to load file log with id " + fileLogId)
            );
        } catch (IOException e) {
            logger.warn("Error during pushing csv content to file", e);
            throw new RuntimeException(e);
        }
    }
}
