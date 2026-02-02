package com.scnsoft.eldermark.services.inbound.therap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.pastdev.jsch.SessionManager;
import com.scnsoft.eldermark.entity.inbound.summary.ProcessingSummary;
import com.scnsoft.eldermark.entity.inbound.therap.summary.TherapTotalProcessingSummary;
import com.scnsoft.eldermark.services.inbound.InboundFileGateway;
import com.scnsoft.eldermark.services.inbound.InboundFileGatewayException;
import com.scnsoft.eldermark.services.inbound.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

@Service("therapSftpInboundFileGateway")
@Conditional(TherapInboundFilesServiceRunCondition.class)
public class TherapSftpInboundFileGateway implements InboundFileGateway<File, TherapTotalProcessingSummary> {

    @Value("${therap.sftp.disabled}")
    private Boolean sftpDisabled;

    @Value("${therap.sftp.workingDirectory}")
    private String sftpWorkingDirectory;

    @Value("${therap.sftp.localStorage.base}")
    private String localStorageBaseDirPath;

    @Value("${therap.sftp.statusFolder.ok}")
    private String okDirName;

    @Value("${therap.sftp.statusFolder.warn}")
    private String warnDirName;

    @Value("${therap.sftp.statusFolder.error}")
    private String errorDirName;

    @Value("${therap.sftp.reportfile.postfix}")
    private String reportFilePostfix;

    private final SessionManager sessionManager;
    private final ReportService reportService;

    private File localStorageOkDir;
    private File localStorageWarnDir;
    private File localStorageErrorDir;

    @Autowired
    public TherapSftpInboundFileGateway(@Qualifier("therapJschSessionManager") SessionManager sessionManager, ReportService reportService) {
        this.sessionManager = sessionManager;
        this.reportService = reportService;
    }

    @PostConstruct
    public void prepareLocalStorageDirectories() {
        final File localStorageBaseDir = new File(localStorageBaseDirPath);

        localStorageOkDir = new File(localStorageBaseDir, okDirName);
        localStorageOkDir.mkdirs();

        localStorageWarnDir = new File(localStorageBaseDir, warnDirName);
        localStorageWarnDir.mkdirs();

        localStorageErrorDir = new File(localStorageBaseDir, errorDirName);
        localStorageErrorDir.mkdirs();
    }

    @Override
    public List<File> loadFiles() {
        if (Boolean.TRUE.equals(sftpDisabled)) {
            return Collections.emptyList();
        }
        ChannelSftp channel = null;
        final List<File> remoteFiles = new ArrayList<>();
        try {

            channel = openAndConnectChannel();
            cacheFilesInLocalStorage(channel, remoteFiles);

        } catch (JSchException | SftpException e) {
            throw new InboundFileGatewayException(e);
        } finally {
            if (channel != null) channel.disconnect();
        }
        return remoteFiles;
    }

    private ChannelSftp openAndConnectChannel() throws JSchException {
        ChannelSftp channel = (ChannelSftp) sessionManager.getSession().openChannel("sftp");
        channel.connect();
        return channel;
    }

    private void cacheFilesInLocalStorage(ChannelSftp channel, List<File> remoteFiles) throws SftpException {
        final String oldPath = channel.pwd();
        channel.cd(sftpWorkingDirectory);

        final Vector fileList = channel.ls(sftpWorkingDirectory);
        for (Object aFileList : fileList) {
            ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) aFileList;
            if (!entry.getAttrs().isDir()) {
                channel.get(entry.getFilename(), localStorageBaseDirPath);
                remoteFiles.add(new File(localStorageBaseDirPath + entry.getFilename()));
            }
        }

        channel.cd(oldPath);
    }

    @Override
    public void afterProcessingStatusOk(File remoteFile, TherapTotalProcessingSummary processingSummary) {
        afterProcessing(remoteFile, processingSummary, okDirName, localStorageOkDir);
    }

    @Override
    public void afterProcessingStatusWarn(File remoteFile, TherapTotalProcessingSummary processingSummary) {
        afterProcessing(remoteFile, processingSummary, warnDirName, localStorageWarnDir);
    }

    @Override
    public void afterProcessingStatusError(File remoteFile, TherapTotalProcessingSummary processingSummary) {
        afterProcessing(remoteFile, processingSummary, errorDirName, localStorageErrorDir);
    }

    private void afterProcessing(File remoteFile, TherapTotalProcessingSummary processingSummary, String sftpFolder, File localStorageFolder) {
        if (Boolean.TRUE.equals(sftpDisabled)) {
            return;
        }
        try {
            final String newFileName = TherapUtils.buildProcessedFileName(processingSummary);
            moveToFolderInSftp(remoteFile, processingSummary, newFileName, sftpFolder);
            moveToFolderInLocalStorage(remoteFile, processingSummary, newFileName, localStorageFolder);
        } catch (IOException | JSchException | SftpException e) {
            throw new InboundFileGatewayException(e);
        }
    }

    private void moveToFolderInSftp(File remoteFile, ProcessingSummary processingSummary, String newFileName, String dirName) throws JSchException, SftpException, JsonProcessingException {
        ChannelSftp channel = null;
        try {
            channel = openAndConnectChannel();
            final String newFileFullPath = sftpWorkingDirectory + '/' + dirName + '/' + newFileName;
            channel.rename(sftpWorkingDirectory + '/' + remoteFile.getName(), newFileFullPath);
            channel.put(new ByteArrayInputStream(reportService.createRemoteReport(processingSummary).getBytes()),
                    newFileFullPath + reportFilePostfix);
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    private void moveToFolderInLocalStorage(File remoteFile, TherapTotalProcessingSummary processingSummary, String newFileName, File folder) throws IOException {
        remoteFile.renameTo(new File(folder, newFileName));
        writeReportToFile(new File(folder, newFileName + reportFilePostfix), processingSummary);
    }

    private void writeReportToFile(File file, TherapTotalProcessingSummary processingSummary) throws IOException {
        if (!file.exists()) {
            file.createNewFile();
        }
        final FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(reportService.createLocalReport(processingSummary));
        fileWriter.close();
    }

}
