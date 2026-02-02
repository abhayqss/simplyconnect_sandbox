package com.scnsoft.eldermark.services.inbound.marco;

import com.scnsoft.eldermark.dao.inbound.marco.MarcoUnassignedStoragePathDao;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoInboundFile;
import com.scnsoft.eldermark.entity.inbound.marco.MarcoUnassignedStoragePath;
import com.scnsoft.eldermark.entity.inbound.marco.summary.MarcoProcessingSummary;
import com.scnsoft.eldermark.services.inbound.InboundFileGateway;
import com.scnsoft.eldermark.services.inbound.InboundFileGatewayException;
import com.scnsoft.eldermark.services.inbound.ReportService;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
@Conditional(MarcoInboundFilesServiceRunCondition.class)
public class MarcoLocalFileSystemInboundFileGateway implements InboundFileGateway<MarcoInboundFile, MarcoProcessingSummary> {

    private static final Logger logger = LoggerFactory.getLogger(MarcoLocalFileSystemInboundFileGateway.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("MMddyyyy_HHmmss_");

    @Value("${marco.metadataFile.postfix}")
    private String metadataFileNamePostfix;

    @Value("${marco.reportfile.postfix}")
    private String reportFileNamePostfix;

    private final ReportService reportService;
    private final MarcoUnassignedStoragePathDao unassignedStoragePathDao;

    private File baseDir;
    private File assignedDir;
    private File unassignedDir;

    private FilenameFilter metadataFileExcludeFilter;

    @Autowired
    public MarcoLocalFileSystemInboundFileGateway(ReportService reportService,
                                                  MarcoUnassignedStoragePathDao unassignedStoragePathDao,
                                                  @Value("${marco.localstorage.base}") String baseDirPath,
                                                  @Value("${marco.localstorage.assigned}") String assignedDirPath,
                                                  @Value("${marco.localstorage.unassigned}") String unassignedDirPath) {
        this.reportService = reportService;
        this.unassignedStoragePathDao = unassignedStoragePathDao;

        baseDir = new File(baseDirPath);
        baseDir.mkdirs();

        assignedDir = new File(baseDirPath, assignedDirPath);
        assignedDir.mkdirs();

        unassignedDir = new File(baseDirPath, unassignedDirPath);
        unassignedDir.mkdirs();

        metadataFileExcludeFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return !name.endsWith(metadataFileNamePostfix);
            }
        };
    }

    @Override
    public List<MarcoInboundFile> loadFiles() {
        logger.debug("Checking for files from Marco");
        final File[] files = baseDir.listFiles(metadataFileExcludeFilter);

        if (files == null) {
            return Collections.emptyList();
        }

        final List<MarcoInboundFile> result = new ArrayList<>(files.length);

        for (File file : files) {
            final File metadataFile = new File(baseDir, file.getName() + metadataFileNamePostfix);
            if (metadataFile.exists()) {
                result.add(new MarcoInboundFile(file, metadataFile));
            }
        }

        logger.debug("Found {} marco files", result.size());
        return result;
    }

    @Override
    public void afterProcessingStatusOk(MarcoInboundFile remoteFile, MarcoProcessingSummary summary) {
        afterProcessing(remoteFile, summary);
    }

    @Override
    public void afterProcessingStatusWarn(MarcoInboundFile remoteFile, MarcoProcessingSummary summary) {
        afterProcessing(remoteFile, summary);
    }

    @Override
    public void afterProcessingStatusError(MarcoInboundFile remoteFile, MarcoProcessingSummary summary) {
        afterProcessing(remoteFile, summary);
    }

    private void afterProcessing(MarcoInboundFile remoteFile, MarcoProcessingSummary summary) {
        try {
            File destinationFolder;

            if (summary.isAssigned()) {
                destinationFolder = assignedDir;
            } else {
                destinationFolder = unassignedDir;
                copyDocumentToUnassignedStorage(remoteFile, summary);
            }

            move(remoteFile.getDocument(), destinationFolder, buildFileName(remoteFile.getDocument().getName(), summary.getProcessedAt()));
            move(remoteFile.getMetadataFile(), destinationFolder, buildFileName(remoteFile.getMetadataFile().getName(), summary.getProcessedAt()));

            writeReportToFile(summary, destinationFolder);

        } catch (IOException e) {
            throw new InboundFileGatewayException(e);
        }
    }

    private void copyDocumentToUnassignedStorage(MarcoInboundFile remoteFile, MarcoProcessingSummary summary) throws IOException {
        logger.info("Trying to copy document to unassigned document storage for {}", summary.getMarcoIntegrationDocument().getOrganizationName());
        List<MarcoUnassignedStoragePath> unassignedStoragePaths = unassignedStoragePathDao.findAllByDatabaseNameAndDisabledIsFalse(
                summary.getMarcoIntegrationDocument().getOrganizationName());

        for (MarcoUnassignedStoragePath unassignedStoragePath: unassignedStoragePaths) {

            copy(remoteFile.getDocument(), unassignedStoragePath.getPath());
            logger.info("Document copied to {}", unassignedStoragePath.getPath());
        }
        logger.info("Copy document to unassigned document storage for {} done", summary.getMarcoIntegrationDocument().getOrganizationName());
    }

    private String buildFileName(String originalFileName, Date date) {
        return DATE_TIME_FORMATTER.print(date.getTime()) + originalFileName;
    }

    private void move(File source, File destinationFolder, String newFileName) {
        source.renameTo(new File(destinationFolder, newFileName));
    }

    private void copy(File document, String pathStr) throws IOException {
        File path = new File(pathStr);
        path.mkdirs();

        Files.copy(document.toPath(), new File(path, document.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private void writeReportToFile(MarcoProcessingSummary summary, File destinationFolder) throws IOException {
        File file = new File(destinationFolder, buildFileName(summary.getFileName(), summary.getProcessedAt()) + reportFileNamePostfix);

        if (!file.exists()) {
            file.createNewFile();
        }
        final FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(reportService.createLocalReport(summary));
        fileWriter.close();
    }

}
