package com.scnsoft.eldermark.shared.carecoordination.service;

/**
 * Created by pzhurba on 09-Dec-15.
 */
public class FaxDto {

    private String faxUserName;
    private String faxPassword;
    private String fax;
    private String responsibility;
    private String to;
    private String phone;
    private String subject;
    private String from;
    private String date;
    private String url;


    public String getFaxUserName() {
        return faxUserName;
    }

    public void setFaxUserName(String faxUserName) {
        this.faxUserName = faxUserName;
    }

    public String getFaxPassword() {
        return faxPassword;
    }

    public void setFaxPassword(String faxPassword) {
        this.faxPassword = faxPassword;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getResponsibility() {
        return responsibility;
    }

    public void setResponsibility(String responsibility) {
        this.responsibility = responsibility;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
