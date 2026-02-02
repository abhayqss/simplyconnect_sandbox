package com.scnsoft.eldermark.consana.sync.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ConsanaDetectedIssueDto {

    private String date;
    private ConsanaCodeableConceptDto category;
    private String severity;
    private String detail;
    private List<ConsanaExtensionDto> extension;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ConsanaCodeableConceptDto getCategory() {
        return category;
    }

    public void setCategory(ConsanaCodeableConceptDto category) {
        this.category = category;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public List<ConsanaExtensionDto> getExtension() {
        return extension;
    }

    public void setExtension(List<ConsanaExtensionDto> extension) {
        this.extension = extension;
    }
}
