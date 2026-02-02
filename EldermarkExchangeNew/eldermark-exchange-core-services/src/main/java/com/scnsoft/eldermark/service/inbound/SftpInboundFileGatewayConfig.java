package com.scnsoft.eldermark.service.inbound;

import com.jcraft.jsch.ChannelSftp;

import java.io.File;
import java.util.Comparator;
import java.util.function.Predicate;

public class SftpInboundFileGatewayConfig {

    private final Boolean isSftpEnabled;
    private final String sftpWorkingDirectory;
    private final String localStorageBaseDirPath;
    private final String okDirName;
    private final String warnDirName;
    private final String errorDirName;
    private final String reportFilePostfix;

    private final Predicate<ChannelSftp.LsEntry> fileFilter;

    private final SftpAcknowledgeStrategy sftpAcknowledgeStrategy;

    private final Comparator<File> fileSort;

    private final boolean canCreateSFTPFolders;

    public SftpInboundFileGatewayConfig(Boolean isSftpEnabled,
                                        String sftpWorkingDirectory,
                                        String localStorageBaseDirPath,
                                        String okDirName,
                                        String warnDirName,
                                        String errorDirName,
                                        String reportFilePostfix,
                                        Predicate<ChannelSftp.LsEntry> fileFilter,
                                        SftpAcknowledgeStrategy sftpAcknowledgeStrategy,
                                        Comparator<File> fileSort,
                                        boolean canCreateSFTPFolders) {
        this.isSftpEnabled = isSftpEnabled;
        this.sftpWorkingDirectory = sftpWorkingDirectory;
        this.localStorageBaseDirPath = localStorageBaseDirPath;
        this.okDirName = okDirName;
        this.warnDirName = warnDirName;
        this.errorDirName = errorDirName;
        this.reportFilePostfix = reportFilePostfix;
        this.fileFilter = fileFilter;
        this.sftpAcknowledgeStrategy = sftpAcknowledgeStrategy;
        this.fileSort = fileSort;
        this.canCreateSFTPFolders = canCreateSFTPFolders;
    }

    public Boolean getSftpEnabled() {
        return isSftpEnabled;
    }

    public String getSftpWorkingDirectory() {
        return sftpWorkingDirectory;
    }

    public String getLocalStorageBaseDirPath() {
        return localStorageBaseDirPath;
    }

    public String getOkDirName() {
        return okDirName;
    }

    public String getWarnDirName() {
        return warnDirName;
    }

    public String getErrorDirName() {
        return errorDirName;
    }

    public String getReportFilePostfix() {
        return reportFilePostfix;
    }

    public Predicate<ChannelSftp.LsEntry> getFileFilter() {
        return fileFilter;
    }

    public SftpAcknowledgeStrategy getSftpAcknowledgeStrategy() {
        return sftpAcknowledgeStrategy;
    }

    public Comparator<File> getFileSort() {
        return this.fileSort;
    }

    public boolean isCanCreateSFTPFolders() {
        return canCreateSFTPFolders;
    }
}
