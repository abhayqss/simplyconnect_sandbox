package com.scnsoft.eldermark.dto;

public class ResetPasswordMailDto {

    private String url;
    
    private String toEmail;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }
}
