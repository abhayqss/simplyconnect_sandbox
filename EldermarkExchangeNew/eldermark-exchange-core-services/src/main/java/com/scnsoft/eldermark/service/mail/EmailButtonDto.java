package com.scnsoft.eldermark.service.mail;

public class EmailButtonDto {

    private String label;
    private String url;

    public EmailButtonDto(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
