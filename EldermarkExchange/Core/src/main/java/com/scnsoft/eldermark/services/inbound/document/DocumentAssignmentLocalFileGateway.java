package com.scnsoft.eldermark.services.inbound.document;

import com.scnsoft.eldermark.dao.inbound.document.DocumentAssignmentInputPathDao;
import com.scnsoft.eldermark.dao.inbound.document.DocumentAssignmentUnassignedStoragePathDao;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentInboundFile;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentInputPath;
import com.scnsoft.eldermark.entity.inbound.document.DocumentAssignmentUnassignedStoragePath;
import com.scnsoft.eldermark.entity.inbound.document.summary.DocumentAssignmentProcessingSummary;
import com.scnsoft.eldermark.services.inbound.InboundFileGateway;
import com.scnsoft.eldermark.services.inbound.InboundFileGatewayException;
import com.scnsoft.eldermark.services.inbound.InboundFilesServiceRunCondition;
import com.scnsoft.eldermark.services.inbound.ReportService;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Conditional(DocumentAssignmentRunCondition.class)
public class DocumentAssignmentLocalFileGateway implements InboundFileGateway<DocumentAssignmentInboundFile, DocumentAssignmentProcessingSummary> {
    private static final Logger logger = LoggerFactory.getLogger(DocumentAssignmentLocalFileGateway.class);

    @Autowired
    private ReportService reportService;

    private final DocumentAssignmentInputPathDao inputPathDao;

    @Autowired
    private DocumentAssignmentUnassignedStoragePathDao unassignedStoragePathDao;

    private final List<DocumentAssignmentInputPath> INPUT_PATHS;

    @Value("${document.assignment.reportfile.postfix}")
    private String reportFileNamePostfix;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormat.forPattern("MMddyyyy_HHmmss_");
    private final String ASSIGNED_SUB_FOLDER = "_assigned";
    private final String UNASSIGNED_SUB_FOLDER = "_unassigned";

    private static final FileFilter DIRECTORY_FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };

    private static final FileFilter NON_DIRECTORY_FILE_FILTER = new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            return !pathname.isDirectory();
        }
    };

    private final File baseDir;

    @Autowired
    public DocumentAssignmentLocalFileGateway(DocumentAssignmentInputPathDao inputPathDao,
                                              @Value("${document.assignment.localstorage.base}") String baseDirPath) {
        this.inputPathDao = inputPathDao;

        baseDir = new File(baseDirPath);
        baseDir.mkdirs();

        INPUT_PATHS = new CopyOnWriteArrayList<>();
    }

    @Scheduled(fixedDelay = 1000 * 60)
    public void reloadInputPaths() {
        INPUT_PATHS.clear();
        INPUT_PATHS.addAll(inputPathDao.findAllByDisabledIsFalse());
        logger.info("Reloaded document assignment flow input paths, total {}", INPUT_PATHS.size());
    }

    @Override
    public List<DocumentAssignmentInboundFile> loadFiles() {
        List<DocumentAssignmentInboundFile> result = new ArrayList<>();

        try {
            //1. take files already present in cache
            result.addAll(loadFilesInLocalCache());

            //2. add files from additional inputs
            for (DocumentAssignmentInputPath inputPath : INPUT_PATHS) {
                List<DocumentAssignmentInboundFile> cachedFiles = cacheFilesInLocalStorage(inputPath);
                result.addAll(cachedFiles);
            }

        } catch (RuntimeException ex) {
            throw new InboundFileGatewayException(ex);
        }
        return result;
    }

    private List<DocumentAssignmentInboundFile> loadFilesInLocalCache() {
        List<DocumentAssignmentInboundFile> result = new ArrayList<>();


        File[] directories = ArrayUtils.nullToEmpty(baseDir.listFiles(DIRECTORY_FILE_FILTER), File[].class);

        for (File subDirectory : directories) {
            File[] files = subDirectory.listFiles(NON_DIRECTORY_FILE_FILTER);
            for (File file : ArrayUtils.nullToEmpty(files, File[].class)) {
                result.add(createInboundFile(file, null));
            }
        }

        return result;
    }

    private List<DocumentAssignmentInboundFile> cacheFilesInLocalStorage(DocumentAssignmentInputPath inputPath) {
        List<DocumentAssignmentInboundFile> result = new ArrayList<>();

        File inputDirectory = new File(inputPath.getInputPath());
        if (!inputDirectory.exists()) {
            return result;
        }

        File cacheDirectory = new File(baseDir, inputPath.getDatabase().getName());
        cacheDirectory.mkdirs();

        File[] inputFiles = ArrayUtils.nullToEmpty(inputDirectory.listFiles(NON_DIRECTORY_FILE_FILTER), File[].class);

        for (File inputFile : inputFiles) {
            inputFile = move(inputFile, cacheDirectory, inputFile.getName());
            result.add(createInboundFile(inputFile, inputPath));
        }

        return result;

    }

    private DocumentAssignmentInboundFile createInboundFile(File file, DocumentAssignmentInputPath inputPath) {
        final DocumentAssignmentInboundFile inboundFile = new DocumentAssignmentInboundFile();

        inboundFile.setFile(file);
        inboundFile.setOrganizationName(file.getParentFile().getName());
        fillPatientIdAndTitle(inboundFile);
        inboundFile.setInputPath(inputPath);

        return inboundFile;
    }

    private void fillPatientIdAndTitle(DocumentAssignmentInboundFile inboundFile) {
        String fullTitle = inboundFile.getFile().getName();

        String fileTitle = null;
        String mpiPatientId = null;

        int separatorIdx = fullTitle.indexOf('_');

        if (separatorIdx == -1) {
            fileTitle = fullTitle;
        } else {
            mpiPatientId = fullTitle.substring(0, separatorIdx);

            if (separatorIdx < fullTitle.length() - 1) {
                fileTitle = fullTitle.substring(separatorIdx + 1);
            }
        }

        inboundFile.setFileTitle(fileTitle);
        inboundFile.setMpiPatientId(mpiPatientId);
    }

    @Override
    public void afterProcessingStatusOk(DocumentAssignmentInboundFile remoteFile, DocumentAssignmentProcessingSummary summary) {
        afterProcessing(remoteFile, summary);
    }

    @Override
    public void afterProcessingStatusWarn(DocumentAssignmentInboundFile remoteFile, DocumentAssignmentProcessingSummary summary) {
        afterProcessing(remoteFile, summary);
    }

    @Override
    public void afterProcessingStatusError(DocumentAssignmentInboundFile remoteFile, DocumentAssignmentProcessingSummary summary) {
        afterProcessing(remoteFile, summary);
    }


    private void afterProcessing(DocumentAssignmentInboundFile remoteFile, DocumentAssignmentProcessingSummary summary) {
        try {
            File destinationFolder;

            final File organizationLocalStorage = remoteFile.getFile().getParentFile();
            if (summary.getAssigned()) {
                destinationFolder = new File(organizationLocalStorage, ASSIGNED_SUB_FOLDER);
            } else {
                destinationFolder = new File(organizationLocalStorage, UNASSIGNED_SUB_FOLDER);

                copyDocumentToUnassignedStorage(remoteFile);
            }

            destinationFolder.mkdirs();

            move(remoteFile.getFile(), destinationFolder, buildFileName(remoteFile.getFile().getName(), summary.getProcessedAt()));

            writeReportToFile(summary, destinationFolder);

        } catch (IOException e) {
            throw new InboundFileGatewayException(e);
        }
    }

    private File move(File source, File destinationFolder, String newFileName) {
        File result = new File(destinationFolder, newFileName);
        source.renameTo(result);
        return result;
    }

    private void copy(File document, String pathStr) throws IOException {
        File path = new File(pathStr);
        path.mkdirs();

        Files.copy(document.toPath(), new File(path, document.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private String buildFileName(String originalFileName, Date date) {
        return DATE_TIME_FORMATTER.print(date.getTime()) + originalFileName;
    }

    private void copyDocumentToUnassignedStorage(DocumentAssignmentInboundFile remoteFile) throws IOException {
        logger.info("Trying to copy document to unassigned document storage for {}", remoteFile.getOrganizationName());

        List<DocumentAssignmentUnassignedStoragePath> unassignedStoragePaths = unassignedStoragePathDao.findAllByDatabaseNameAndDisabledIsFalse(remoteFile.getOrganizationName());

        for (DocumentAssignmentUnassignedStoragePath unassignedStoragePath : unassignedStoragePaths) {
            copy(remoteFile.getFile(), unassignedStoragePath.getPath());
            logger.info("Document copied to {}", unassignedStoragePath.getPath());

        }
        logger.info("Copy document to unassigned document storage for {} done", remoteFile.getOrganizationName());
    }

    //todo unify
    private void writeReportToFile(DocumentAssignmentProcessingSummary summary, File destinationFolder) throws IOException {
        File file = new File(destinationFolder, buildFileName(summary.getFileName(), summary.getProcessedAt()) + reportFileNamePostfix);

        if (!file.exists()) {
            file.createNewFile();
        }
        final FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(reportService.createLocalReport(summary));
        fileWriter.close();
    }

}
