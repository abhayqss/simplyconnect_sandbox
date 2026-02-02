package com.scnsoft.eldermark.entity.inbound.therap;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TherapMailNotificationDto {

    private String fileName;
    private String status;

    private String recipientName;
    private String recipientEmail;

    private List<FileProcessingResultDto> enrollments = new ArrayList<>();
    private List<FileProcessingResultDto> idfs = new ArrayList<>();
    private List<FileProcessingResultDto> events = new ArrayList<>();

    private byte[] errorReport;


    public static class FileProcessingResultDto {
        private String status;
        private String fileName;
        private int notProcessed;
        private int total;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getNotProcessed() {
            return notProcessed;
        }

        public void setNotProcessed(int notProcessed) {
            this.notProcessed = notProcessed;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        @Override
        public String toString() {
            return "FileProcessingResultDto{" +
                    "status='" + status + '\'' +
                    ", fileName='" + fileName + '\'' +
                    ", notProcessed=" + notProcessed +
                    ", total=" + total +
                    '}';
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public List<FileProcessingResultDto> getEnrollments() {
        return enrollments;
    }

    public void setEnrollments(List<FileProcessingResultDto> enrollments) {
        this.enrollments = enrollments;
    }

    public List<FileProcessingResultDto> getIdfs() {
        return idfs;
    }

    public void setIdfs(List<FileProcessingResultDto> idfs) {
        this.idfs = idfs;
    }

    public List<FileProcessingResultDto> getEvents() {
        return events;
    }

    public void setEvents(List<FileProcessingResultDto> events) {
        this.events = events;
    }

    public byte[] getErrorReport() {
        return errorReport;
    }

    public void setErrorReport(byte[] errorReport) {
        this.errorReport = errorReport;
    }

    @Override
    public String toString() {
        return "TherapMailNotificationDto{" +
                "fileName='" + fileName + '\'' +
                ", status='" + status + '\'' +
                ", recipientName='" + recipientName + '\'' +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", events=[" + StringUtils.join(events, ", ") +
                "], idfs=[" + StringUtils.join(idfs, ", ") +
                "], errorReport=" + errorReport +
                '}';
    }
}
