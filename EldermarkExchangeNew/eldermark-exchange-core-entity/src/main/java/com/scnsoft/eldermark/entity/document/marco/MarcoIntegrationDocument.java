package com.scnsoft.eldermark.entity.document.marco;

import com.scnsoft.eldermark.entity.document.Document;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "MarcoIntegrationDocumentsLog")
public class MarcoIntegrationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "received_timestamp", nullable = false)
    private Instant receivedTime;

    @Column(name = "organization_name")
    private String organizationName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "date_of_birth", length = 100)
    private String dateOfBirthStr;

    @Column(name = "ssn", length = 100)
    private String ssn;

    @Column(name = "file_title")
    private String fileTitle;

    @Column(name = "author")
    private String author;

    @Column(name = "document_original_name")
    private String documentOriginalName;

    @JoinColumn(name = "document_id")
    @OneToOne
    private Document document;

    @Column(name = "unassigned_reason", length = 30)
    @Enumerated(EnumType.STRING)
    private MarcoUnassignedReason unassignedReason;

    @Column(name = "full_name")
    private String fullName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Instant receivedTime) {
        this.receivedTime = receivedTime;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
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

    public String getDateOfBirthStr() {
        return dateOfBirthStr;
    }

    public void setDateOfBirthStr(String dateOfBirthStr) {
        this.dateOfBirthStr = dateOfBirthStr;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getFileTitle() {
        return fileTitle;
    }

    public void setFileTitle(String fileTitle) {
        this.fileTitle = fileTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDocumentOriginalName() {
        return documentOriginalName;
    }

    public void setDocumentOriginalName(String documentOriginalName) {
        this.documentOriginalName = documentOriginalName;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public MarcoUnassignedReason getUnassignedReason() {
        return unassignedReason;
    }

    public void setUnassignedReason(MarcoUnassignedReason unassignedReason) {
        this.unassignedReason = unassignedReason;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
