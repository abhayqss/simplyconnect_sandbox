package com.scnsoft.eldermark.entity.inbound.document;

import java.io.File;


public class DocumentAssignmentInboundFile {

    private String organizationName;
    private String mpiPatientId;
    private File file;
    private String fileTitle;
    private DocumentAssignmentInputPath inputPath;


    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getMpiPatientId() {
        return mpiPatientId;
    }

    public void setMpiPatientId(String mpiPatientId) {
        this.mpiPatientId = mpiPatientId;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public DocumentAssignmentInputPath getInputPath() {
        return inputPath;
    }

    public void setInputPath(DocumentAssignmentInputPath inputPath) {
        this.inputPath = inputPath;
    }
}
