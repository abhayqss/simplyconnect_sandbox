package com.scnsoft.eldermark.services.inbound.marco;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

import java.util.Date;

public class MarcoDocumentMetadata {

    private String organizationName;
    private String firstName;
    private String lastName;
    private String fullName;

    @XStreamAlias("dateOfBirth")
    private String dateOfBirthStr;

    private String ssn;
    private String fileTitle;
    private String author;
    private String documentOriginalName;


    @XStreamOmitField
    private Date dateOfBirth;

    public MarcoDocumentMetadata() {
    }

    public MarcoDocumentMetadata(String organizationName, String firstName, String lastName, String fullName,
                                 String dateOfBirthStr, String ssn, String fileTitle, String author, String documentOriginalName) {
        this.organizationName = organizationName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.dateOfBirthStr = dateOfBirthStr;
        this.ssn = ssn;
        this.fileTitle = fileTitle;
        this.author = author;
        this.documentOriginalName = documentOriginalName;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getDateOfBirthStr() {
        return dateOfBirthStr;
    }

    public void setDateOfBirthStr(String dateOfBirthStr) {
        this.dateOfBirthStr = dateOfBirthStr;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public String getDocumentOriginalName() {
        return documentOriginalName;
    }

    public void setDocumentOriginalName(String documentOriginalName) {
        this.documentOriginalName = documentOriginalName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
